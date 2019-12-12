package com.f2m.aquarius.magic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AquariusBackFile {

	public static final String FOLDER = "folder";
	public static final String FILE = "file";
	private final static Logger logger = Logger.getLogger(AquariusBackFile.class.getName());
	private File folderPath = null;
	private String user = null;
	private String remotePath = null;
	private ObjectMapper mapper = new ObjectMapper();
	private final String backFileName = ".aquariusBackFile";
	private JsonNode backFile = null;
	private Calendar calendar = Calendar.getInstance();
	
	public AquariusBackFile() { }
	
	public AquariusBackFile(File folderPath, String user, String remotePath) throws Exception {
		this.folderPath = folderPath;
		this.user = user;
		this.remotePath = remotePath;
		readBackFile();
	}
	
	public void setPath(File folderPath) throws Exception {
		this.folderPath = folderPath;
	}
	
	public void setUser(String user) throws Exception {
		this.user = user;
	}
	
	public void setRemotePath(String remotePath) throws Exception {
		this.remotePath = remotePath;
	}
	
	public JsonNode getbackFile() {
		return backFile;
	}
	
	public void readBackFile() throws Exception {
		//FileInputStream fileIn = null;
		//ObjectInputStream objectIn = null;
		BufferedReader br = null;
		File file = new File(folderPath + "\\" + backFileName);
		if (file.exists()) {
			try {
				String response = "";
				br = new BufferedReader(new FileReader(file));
				String st;
				while ((st = br.readLine()) != null) {
					response += st;
				}
				if (!parse(response)) {
					createBackFile();
				}
				/*
				fileIn = new FileInputStream(folderPath + "\\" + backFileName);
				objectIn = new ObjectInputStream(fileIn);
				Object obj = objectIn.readObject();
				if (!parse(obj)) {
					createBackFile();
				}
				*/
			} catch (FileNotFoundException e) {
				logger.log(Level.SEVERE, "Error al leer el archivo backFile - " + e.getMessage(), e);
				throw new Exception("Error al leer el archivo backFile");
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Error al leer el archivo backFile - " + e.getMessage(), e);
				throw new Exception("Error al leer el archivo backFile");
			} finally {
				if(br != null) {
					br.close();
				}
				/*
				if (objectIn != null) {
					objectIn.close();
					fileIn.close();
				}
				*/
			}
		} else {
			createBackFile();
		}
	}
	
	public boolean writeBackFile() {
		//FileOutputStream fileOut = null;
		//ObjectOutputStream objectOut = null;
		BufferedWriter bw = null;
		File file = new File(folderPath + "\\" + backFileName);
		boolean response = false;
		try {
			bw = new BufferedWriter(new FileWriter(file));
			if (backFile != null) {
				bw.write(backFile.toString());
			}
			/*
			fileOut = new FileOutputStream(folderPath + "\\" + backFileName);
			objectOut = new ObjectOutputStream(fileOut);
			objectOut.writeObject(backFile.toString());
			*/
			response = true;
		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE, "Error al escribir el archivo backFile - " + e.getMessage(), e);
			response = false;
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Error al escribir el archivo backFile - " + e.getMessage(), e);
			response = false;
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
			} catch (IOException e) { }
			
			/*
			if (objectOut != null) {
				
					objectOut.close();
					fileOut.close();
				
			}
			*/
		}
		return response;
	}
	
	private boolean parse(String obj) {
		if (obj != null) {
			try {
				backFile = mapper.readTree(obj);
				return true;
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Error al parsear el objeto backFile - " + e.getMessage(), e);
			}
		}
		return false;
	}
	
	private void createBackFile() {
		ObjectNode newObject = mapper.createObjectNode();
		newObject.put("id", UUID.randomUUID().toString());
		newObject.put("name", folderPath.getName());
		newObject.put("createdBy", user);
		newObject.put("createdOn", calendar.getTimeInMillis());
		newObject.put("remotePath", remotePath);
		newObject.putArray("files");
		newObject.putArray("log");
		backFile = newObject;
	}
	
	public void createFile(String name, String type, long modifiedOn, String remotePath) {
		JsonNode files = backFile.get("files");
		boolean exists = false;
		if (files.isArray()) {
			for (JsonNode file : files) {
				if (file.get("name").asText().equals(name)) {
					//TODO: validar que existe y que sea menor
					exists = true;
				}
			}
		}
		if (!exists) {
			ObjectNode newObject = mapper.createObjectNode();
			newObject.put("name", name);
			newObject.put("type", type);
			newObject.put("modifiedOn", modifiedOn);
			newObject.put("remotePath", remotePath);
			ArrayNode arrayFiles = (ArrayNode) backFile.get("files");
			arrayFiles.add(newObject);
		}
	}
	
	public void createLog(String action) {
		ObjectNode newObject = mapper.createObjectNode();
		newObject.put("id", UUID.randomUUID().toString());
		newObject.put("action", action);
		newObject.put("createdBy", user);
		newObject.put("createdOn", calendar.getTimeInMillis());
		ArrayNode arrayLog = (ArrayNode) backFile.get("log");
		arrayLog.add(newObject);
	}
	
	public static void main (String[] args) {
		try {
			AquariusBackFile log = new AquariusBackFile(new File("C:\\Users\\gomado\\Documents\\F2M\\Aquarius\\Repositorio F2M\\aquarius\\Test\\MagicFolder"), "ECM", "/prueba");
			log.createFile("nombre 1", "directory", 1111, "id");
			log.writeBackFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
