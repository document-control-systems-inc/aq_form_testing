package com.f2m.aquarius.magic;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AquariusAPIClient {
	
	private final static Logger logger = Logger.getLogger(AquariusMagicFolder.class.getName());
	private ObjectMapper mapper = new ObjectMapper();

	private JsonNode configParams = null;
	
	private final String homeDirectory = "Home";
	private final String uploadDirectory = "Upload";
	//TODO: obtener los valores de esta variable:
	private final String domain = "AquariusAWS";
	private String token = null;
	private JsonNode wsResponse;
	private int wsErrorCode;
	
	//BackFile:
	private AquariusBackFile backFile;
	
	public AquariusAPIClient() throws Exception {
		getConfig();
	}
	
	public AquariusAPIClient(JsonNode configParams) {
		this.configParams = configParams;
	}
	
	private void getConfig() throws Exception {
		AquariusConfFile config = new AquariusConfFile();
		configParams = config.readConfFile();
	}
	
	private String getUrl() {
		return configParams.get("ws_url").asText();
	}
	
	public String getUserName() {
		return configParams.get("userName").asText();
	}
	
	private String getPassword() {
		return configParams.get("password").asText();
	}
	
	private JsonNode createParam(String key, String value) {
		ObjectNode newObject = mapper.createObjectNode();
		newObject.put("key", key);
		newObject.put("value", value);
		return newObject;
	}
	
	private boolean executeUploadService(String path, String fileName, String id) {
		String ws = getUrl() + "/document";
		try {
			DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
			HttpPost httppost = new HttpPost(ws);

			// Se agrega el archivo:
			MultipartEntity entity = new MultipartEntity();
			
			File file = new File(fileName);
			
			FileBody name = new FileBody(file);
			StringBody content = new StringBody("Filename: " + fileName);
			
			ObjectNode newObject = mapper.createObjectNode();
			newObject.put("title", file.getName());
			System.out.println(newObject.toString());
			
			entity.addPart("file", name);
			entity.addPart("content", content);
			// se agregan las propiedades:
			entity.addPart("token", new StringBody(token));
			entity.addPart("documentclass", new StringBody("1"));
			entity.addPart("properties", new StringBody(newObject.toString()));
			entity.addPart("path", new StringBody(path));
			httppost.setEntity(entity);
			HttpResponse response = httpclient.execute(httppost);
			
			
			if (response.getStatusLine().getStatusCode() != 200) {
				logger.log(Level.SEVERE, "Error al consultar servicio: " + ws + " HTTP error code : " + response.getStatusLine().getStatusCode());
				return false;
			}
			HttpEntity responseEntity = response.getEntity();
			return parseWsResponse(toJsonNode(EntityUtils.toString(responseEntity, "UTF-8")));
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error al consultar servicio: " + ws + " - " + e.getMessage(), e);
			return false;
		}
	}
	
	private boolean executeService(String ws, String method, List<JsonNode> argsHeader, String body) {
		try {
			URL url = new URL(getUrl() + ws);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(method);
			conn.setRequestProperty("Accept", "application/json");
			if (argsHeader != null) {
				for (JsonNode arg:argsHeader) {
					conn.setRequestProperty(arg.findValue("key").asText(), arg.findValue("value").asText());
				}
			}
			if (body != null) {
				conn.setRequestProperty("Content-Type","application/json");
				conn.setDoOutput(true);
				byte[] outputInBytes = body.getBytes("UTF-8");
				OutputStream os = conn.getOutputStream();
				os.write(outputInBytes);
				os.close();
			}
			if (conn.getResponseCode() != 200) {
				logger.log(Level.SEVERE, "Error al consultar servicio: " + ws + " HTTP error code : " + conn.getResponseCode());
				return false;
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream()), "UTF-8"));
			String output;
			String response = "";
			while ((output = br.readLine()) != null) {
				response += output;
			}
			conn.disconnect();
			return parseWsResponse(toJsonNode(response));
			
		} catch (MalformedURLException e) {
			logger.log(Level.SEVERE, "Error al consultar servicio: " + ws + " - " + e.getMessage(), e);
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Error al consultar servicio: " + ws + " - " + e.getMessage(), e);
		}
		return false;
	}
	
	private JsonNode toJsonNode(String json) {
		try {
			JsonNode jsonResponse = mapper.readTree(json);
			return jsonResponse;
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error al parsear el json: " + json + " - " + e.getMessage(), e);
		}
		return null;
	}
	
	private boolean parseWsResponse(JsonNode response) {
		wsResponse = null;
		wsErrorCode = -1;
		if (response != null) {
			wsErrorCode = response.findValue("status").asInt(); 
			if (wsErrorCode == 0) {
				wsResponse = response.findValue("exito");
				return true;
			}
		}
		return false;
		
	}
	
	public boolean testLogin() {
		if (login()) {
			return logout();
		}
		return false;
	}
	
	
	private boolean logout() {
		List<JsonNode> args = new ArrayList<JsonNode>();
		args.add(createParam("token", token));
		return (executeService("/logout", "GET", args, null));
	}
	
	private boolean login() {
		List<JsonNode> args = new ArrayList<JsonNode>();
		args.add(createParam("user", getUserName()));
		args.add(createParam("password", getPassword()));
		if (executeService("/login/", "GET", args, null)) {
			token = wsResponse.asText();
			return true;
		}
		return false;
	}
	
	private boolean listFiles(File f, String remotePath) {
		if (f.exists()) {
			if (f.isDirectory()) {
				String folderName = f.getName();
				System.out.println("Directorio:\t" + folderName);
				if (validateFolder(folderName, remotePath)) {
					backFile.createFile(folderName, AquariusBackFile.FOLDER, f.lastModified(), remotePath + "/" + folderName);
					ArrayList<File> files = new ArrayList<File>(Arrays.asList(f.listFiles()));
					for (File file:files) {
						listFiles(file, remotePath + "/" + folderName);
					}
					return true;
				} else {
					System.out.println("Error al crear el folder: " + folderName);
					return false;
				}
			} else if (f.isFile()) {
				System.out.println("\tArchivo:\t" + f.getName() + "\tRuta Remota: " + remotePath + "/" + f.getName() +  "\tRuta Local: " + f.getAbsolutePath());
				System.out.println("Fecha de modificación: " + f.lastModified());
				if (!validateObject(remotePath + "/" + f.getName())) {
					System.out.println("File no existe... se procede a subir...");
					if (executeUploadService(remotePath, f.getAbsolutePath(), null)) {
						backFile.createFile(f.getName(), AquariusBackFile.FILE, f.lastModified(), remotePath + "/" + f.getName());
						System.out.println("Se subió el archivo: " + f.getName());
					} else {
						System.out.println("Error al subir el archivo: " + f.getName());
					}
				} else {
					backFile.createFile(f.getName(), AquariusBackFile.FILE, f.lastModified(), remotePath + "/" + f.getName());
					long fechaRemoto = wsResponse.findValue("properties").findValue("createdOn").findValue("time").asLong();
					if (fechaRemoto < f.lastModified()) {
						System.out.println("Se actualiza versión en remoto: " + wsResponse);
					}
				}
				return true;
			} else {
				return false;
			}
		} else {
			System.out.println("El archivo " + f.getName() + " no existe");
			return false;
		}
	}
	
	private boolean createFolder(String parentPath, String name) {
		if (!validateObject(parentPath + "/" + name)) {
			List<JsonNode> args = new ArrayList<JsonNode>();
			args.add(createParam("token", token));
			args.add(createParam("path", parentPath));
			args.add(createParam("name", name));
			if (executeService("/folder/path", "PUT", args, null)) {
				return true;
			}
			return false;
		}
		return true;
	}
	
	private boolean validateObject(String path) {
		List<JsonNode> args = new ArrayList<JsonNode>();
		args.add(createParam("token", token));
		args.add(createParam("path", path));
		if (executeService("/object/path", "GET", args, null)) {
			return true;
		}
		return false;
	}
	
	private boolean validateFolder(String folder, String path) {
		if (!validateObject(path + "/" + folder)) {
			if (!createFolder(path, folder)) {
				System.out.println("Error al crear la carpeta: " + folder + "\t Error code: " + wsErrorCode);
				return false;
			}
		}
		return true;
	}
	
	private boolean validatePath(String path) {
		String[] pathElements = path.split("/");
		if (pathElements.length > 2) {
			String relativePath = "/" + pathElements[1];
			for (int i = 2; i < pathElements.length; i++) {
				if (pathElements[i].length() > 0) {
					if (!validateObject(relativePath + "/" + pathElements[i])) {
						if (!createFolder(relativePath, pathElements[i])) {
							System.out.println("Error al crear la carpeta: " + pathElements[i] + "\t Error code: " + wsErrorCode);
							return false;
						}
					}
					relativePath += "/" + pathElements[i];
				}
			}
			return true;
		} else {
			System.out.println("Path mal formado...");
			return false;
		}
	}
	
	private String createUploadPath(String remotePath) {
		if (remotePath == null || remotePath.length() == 0) {
			remotePath = "/" + domain + "/" + homeDirectory + "/" + getUserName() + "/" + uploadDirectory;
		}
		if (validatePath(remotePath)) {
			return remotePath;
		}
		return null;
	}
	
	public void send(String folderPath, String remotePath) {
		logger.log(Level.INFO, "Send: " + folderPath);
		
		if (login()) {
			logger.log(Level.INFO, "Inicio Sesión: " + token);
			File folder = new File(folderPath);
			if (folder.exists()) {
				remotePath = createUploadPath(remotePath);
				if (remotePath != null) {
					try {
						backFile = new AquariusBackFile(folder, getUserName(), remotePath);
						listFiles(folder, remotePath);
						backFile.writeBackFile();
					} catch (Exception e) {
						logger.log(Level.SEVERE, "Error al enviar - " + e.getMessage(), e);
					}
				}
			}
			if (logout()) {
				logger.log(Level.INFO, "Cerrando Sesión: " + token);
			}
		}
	}
	
	private boolean callgetDefTask(String id) {
		List<JsonNode> args = new ArrayList<JsonNode>();
		args.add(createParam("token", token));
		if (id != null && id.length() > 0) {
			args.add(createParam("id", id));
		}
		if (executeService("/bpm/taskDef", "GET", args, null)) {
			return true;
		}
		return false;
	}
	
	private boolean callgetDefWorkflow(String id) {
		List<JsonNode> args = new ArrayList<JsonNode>();
		args.add(createParam("token", token));
		if (id != null && id.length() > 0) {
			args.add(createParam("id", id));
		}
		if (executeService("/bpm/workflowDef", "GET", args, null)) {
			return true;
		}
		return false;
	}
	
	private boolean callsetDefTask(String taskDef) {
		List<JsonNode> args = new ArrayList<JsonNode>();
		args.add(createParam("token", token));
		args.add(createParam("taskDef", taskDef));
		if (executeService("/bpm/taskDef", "PUT", args, null)) {
			return true;
		}
		return false;
	}
	
	private boolean callgetTask(String idTask) {
		List<JsonNode> args = new ArrayList<JsonNode>();
		args.add(createParam("token", token));
		if (idTask != null && idTask.length() > 0) {
			args.add(createParam("id", idTask));
		}
		if (executeService("/bpm/task", "GET", args, null)) {
			return true;
		}
		return false;
	}
	
	private boolean callgetTaskList(String status) {
		List<JsonNode> args = new ArrayList<JsonNode>();
		args.add(createParam("token", token));
		args.add(createParam("status", status));
		if (executeService("/bpm/task/list", "GET", args, null)) {
			return true;
		}
		return false;
	}
	
	private boolean callstartTask(String idTask) {
		List<JsonNode> args = new ArrayList<JsonNode>();
		args.add(createParam("token", token));
		args.add(createParam("id", idTask));
		if (executeService("/bpm/task/start", "PUT", args, null)) {
			return true;
		}
		return false;
	}
	
	private boolean callPauseTask(String idTask) {
		List<JsonNode> args = new ArrayList<JsonNode>();
		args.add(createParam("token", token));
		args.add(createParam("id", idTask));
		if (executeService("/bpm/task/pause", "PUT", args, null)) {
			return true;
		}
		return false;
	}
	
	//TODO: Cambiar para que form y comments vengan en el mismo Json
	private boolean callFinishTask(String idTask, String output, String form, String comments) {
		List<JsonNode> args = new ArrayList<JsonNode>();
		args.add(createParam("token", token));
		args.add(createParam("id", idTask));
		args.add(createParam("output", output));
		if (form != null && form.length() > 0) {
			args.add(createParam("form", form));
		}
		if (comments != null && comments.length() > 0) {
			args.add(createParam("comments", comments));
		}
		if (executeService("/bpm/task/finish", "PUT", args, null)) {
			return true;
		}
		return false;
	}
	
	//TODO: Cambiar para que form y comments vengan en el mismo Json
	private boolean callSaveTask(String idTask, String form, String comments) {
		List<JsonNode> args = new ArrayList<JsonNode>();
		args.add(createParam("token", token));
		args.add(createParam("id", idTask));
		if (form != null && form.length() > 0) {
			args.add(createParam("form", form));
		}
		if (comments != null && comments.length() > 0) {
			args.add(createParam("comments", comments));
		}
		if (executeService("/bpm/task/save", "PUT", args, null)) {
			return true;
		}
		return false;
	}
	
	private boolean callcreateWorkflow(String idWorkflowDef, String file) {
		List<JsonNode> args = new ArrayList<JsonNode>();
		args.add(createParam("token", token));
		args.add(createParam("idDefWorkflow", idWorkflowDef));
		String body = null;
		if (file != null && file.length() > 0) {
			body = file;
		}
		System.out.println("FILE: " + file);
		if (executeService("/bpm/workflow", "POST", args, body)) {
			return true;
		}
		return false;
	}
	
	private boolean callstartWorkflow(String idWorkflow) {
		List<JsonNode> args = new ArrayList<JsonNode>();
		args.add(createParam("token", token));
		args.add(createParam("id", idWorkflow));
		if (executeService("/bpm/workflow/start", "PUT", args, null)) {
			return true;
		}
		return false;
	}
	
	public boolean setDefTask(String taskDef) {
		boolean response = false;
		logger.log(Level.INFO, "setDefTask: " + taskDef);
		if (login()) {
			logger.log(Level.INFO, "Inicio Sesión: " + token);
			response = callsetDefTask(taskDef);
			if (logout()) {
				logger.log(Level.INFO, "Cerrando Sesión: " + token);
			}
		}
		return response;
	}
	
	public JsonNode getDefTasks(String id) {
		JsonNode response = null;
		logger.log(Level.INFO, "getDefTask: ");
		if (login()) {
			logger.log(Level.INFO, "Inicio Sesión: " + token);
			if (callgetDefTask(id)) {
				response = wsResponse;
			}
			if (logout()) {
				logger.log(Level.INFO, "Cerrando Sesión: " + token);
			}
		}
		return response;
	}
	
	public JsonNode getDefWorkflows(String id) {
		JsonNode response = null;
		logger.log(Level.INFO, "getDefWorkflow: ");
		if (login()) {
			logger.log(Level.INFO, "Inicio Sesión: " + token);
			if (callgetDefWorkflow(id)) {
				response = wsResponse;
			}
			if (logout()) {
				logger.log(Level.INFO, "Cerrando Sesión: " + token);
			}
		}
		return response;
	}
	
	public JsonNode getTasks(String idTask) {
		JsonNode response = null;
		logger.log(Level.INFO, "getTask: ");
		if (login()) {
			logger.log(Level.INFO, "Inicio Sesión: " + token);
			if (callgetTask(idTask)) {
				response = wsResponse;
			}
			if (logout()) {
				logger.log(Level.INFO, "Cerrando Sesión: " + token);
			}
		}
		return response;
	}
	
	public JsonNode getTaskList(String status) {
		JsonNode response = null;
		logger.log(Level.INFO, "getTaskList: " + status);
		if (login()) {
			logger.log(Level.INFO, "Inicio Sesión: " + token);
			if (callgetTaskList(status)) {
				response = wsResponse;
			}
			if (logout()) {
				logger.log(Level.INFO, "Cerrando Sesión: " + token);
			}
		}
		return response;
	}
	
	public JsonNode startTask(String idTask) {
		JsonNode response = null;
		logger.log(Level.INFO, "startTask: " + idTask);
		if (login()) {
			logger.log(Level.INFO, "Inicio Sesión: " + token);
			if (callstartTask(idTask)) {
				response = wsResponse;
			}
			if (logout()) {
				logger.log(Level.INFO, "Cerrando Sesión: " + token);
			}
		}
		return response;
	}
	
	public JsonNode pauseTask(String idTask) {
		JsonNode response = null;
		logger.log(Level.INFO, "pauseTask: " + idTask);
		if (login()) {
			logger.log(Level.INFO, "Inicio Sesión: " + token);
			if (callPauseTask(idTask)) {
				response = wsResponse;
			}
			if (logout()) {
				logger.log(Level.INFO, "Cerrando Sesión: " + token);
			}
		}
		return response;
	}
	
	public JsonNode saveComments(String idTask, String comments) {
		JsonNode response = null;
		logger.log(Level.INFO, "SaveTaskComments: " + idTask + "\t" + comments);
		if (login()) {
			logger.log(Level.INFO, "Inicio Sesión: " + token);
			if (callSaveTask(idTask, null, comments)) {
				response = wsResponse;
			}
			if (logout()) {
				logger.log(Level.INFO, "Cerrando Sesión: " + token);
			}
		}
		return response;
	}
	
	public JsonNode saveForm(String idTask, String form) {
		JsonNode response = null;
		logger.log(Level.INFO, "SaveTaskForm: " + idTask + "\t" + form);
		if (login()) {
			logger.log(Level.INFO, "Inicio Sesión: " + token);
			if (callSaveTask(idTask, form, null)) {
				response = wsResponse;
			}
			if (logout()) {
				logger.log(Level.INFO, "Cerrando Sesión: " + token);
			}
		}
		return response;
	}
	
	public JsonNode finishTask(String idTask, String output, String form, String comments) {
		JsonNode response = null;
		logger.log(Level.INFO, "FinishTask: " + idTask + "\t" + output);
		if (login()) {
			logger.log(Level.INFO, "Inicio Sesión: " + token);
			if (callFinishTask(idTask, output, form, comments)) {
				response = wsResponse;
			}
			if (logout()) {
				logger.log(Level.INFO, "Cerrando Sesión: " + token);
			}
		}
		return response;
	}
	
	public JsonNode createWorkflow(String idDefWorkflow, String file) {
		JsonNode response = null;
		logger.log(Level.INFO, "createWorkflow: " + idDefWorkflow);
		if (login()) {
			logger.log(Level.INFO, "Inicio Sesión: " + token);
			if (callcreateWorkflow(idDefWorkflow, file)) {
				response = wsResponse;
			}
			if (logout()) {
				logger.log(Level.INFO, "Cerrando Sesión: " + token);
			}
		}
		return response;
	}
	
	public JsonNode startWorkflow(String idWorkflow) {
		JsonNode response = null;
		logger.log(Level.INFO, "startWorkflow: " + idWorkflow);
		if (login()) {
			logger.log(Level.INFO, "Inicio Sesión: " + token);
			if (callstartWorkflow(idWorkflow)) {
				response = wsResponse;
			}
			if (logout()) {
				logger.log(Level.INFO, "Cerrando Sesión: " + token);
			}
		}
		return response;
	}
	
	public static void main(String[] args) {
		AquariusAPIClient api;
		try {
			api = new AquariusAPIClient();
			api.send("C:\\Users\\gomado\\Documents\\F2M\\Aquarius\\Repositorio F2M\\aquarius\\Test\\", null);
			System.out.println(System.currentTimeMillis());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
