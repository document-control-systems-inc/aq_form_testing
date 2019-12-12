package com.f2m.aquarius.magic.gui;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f2m.aquarius.magic.AquariusAPIClient;
import com.f2m.aquarius.utils.GeneralUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RunTaskController implements Initializable {
	private final static Logger logger = Logger.getLogger(RunTaskController.class.getName());
	
	@FXML
	private Button btnCancelar;
	@FXML
	private Label lblTaskName;
	@FXML
	private TextArea txtComments;
	@FXML
	private TextField txtComment;
	@FXML
	private Button btnAddComment;
	@FXML
	private Button btnPause;
	@FXML
	private Button btnSave;
	@FXML
	private HBox panButtons;
	@FXML
	private TabPane tabPane;
	@FXML
	private Tab panForm;
	@FXML
	private VBox panFormElements;
	@FXML
	private Label lblCreationDate;
	@FXML
	private Label lblCreator;
	@FXML
	private Label lblStartDate;
	@FXML
	private Label lblDueDate;
	@FXML
	private Label lblExecutionTime;
	@FXML
	private Label lblFinalizationDate;
	@FXML
	private Label lblResponsable;
	@FXML
	private Label lblStatus;
	@FXML
	private Label lblId;
	@FXML
	private VBox panFilesElements;
	@FXML
	private Tab panFiles;
	
	private ObjectNode taskInfo = null;
	private JsonNode defTask = null;
	private AquariusAPIClient api = null;
	private ObjectMapper mapper = new ObjectMapper();
	private ArrayList<String> listMandatory = new ArrayList<String>();
	private GeneralUtils gutils = new GeneralUtils();
	
	public void setTaskInfo(ObjectNode info) throws Exception {
		this.taskInfo = info;
		if (taskInfo != null) {
			logger.info("Se recibió la siguiente tarea: " + taskInfo.toString());
			panForm.setDisable(true);
			api = new AquariusAPIClient();
			String name = null;
			JsonNode defTasks = api.getDefTasks(taskInfo.get("taskDef").asText());
			if (defTasks != null) {
				for (JsonNode defTaskAux:defTasks) {
					defTask = defTaskAux;
					name = defTask.get("taskDef").get("name").asText();
					if (name != null) {
						lblTaskName.setText(name);
					}
					JsonNode options = defTask.get("taskDef").get("options");
					for (JsonNode option: options) {
						Button button1 = new Button(option.asText());
						button1.setOnAction(new EventHandler<ActionEvent>() {
						    @Override
						    public void handle(ActionEvent event) {
						    	Button button1 = (Button) event.getSource();
						    	handleOptionsEvent(button1.getText());
						    }
						});
						panButtons.getChildren().add(0, button1);
					}
					JsonNode forms = defTask.get("taskDef").get("form");
					if (forms != null) {
						panForm.setDisable(false);
						for (JsonNode form: forms) {
							HBox renglon = new HBox();
							renglon.setSpacing(10);
							renglon.setPadding(new Insets(5,5,5,5));
							String lblName = "";
							if (form.get("isMandatoty").asBoolean()) {
								lblName += "* ";
							}
							lblName += form.get("name").asText() + ":";
							Label etiqueta = new Label(lblName);
							renglon.getChildren().add(etiqueta);
							panFormElements.getChildren().add(renglon);
							String componentName = "";
							switch(form.get("type").asText()) {
								case "textbox":
									TextField texto = new TextField();
									componentName = "frmtxt" + form.get("id").asText(); 
									texto.setId(componentName);
									texto.setText(getFormValue(componentName));
									renglon.getChildren().add(texto);
									break;
								case "checkbox":
									if (form.get("items") != null) {
										VBox panItems = new VBox();
										panItems.setSpacing(5);
										panItems.setPadding(new Insets(5,5,5,5));
										componentName = "frmChk" + form.get("id").asText();
										panItems.setId(componentName);
										//TODO: Leer los datos de la tarea y marcar los que se hayan marcado previamente
										for (JsonNode items:form.get("items")) {
											CheckBox check = new CheckBox(items.asText());
											panItems.getChildren().add(check);
										}
										renglon.getChildren().add(panItems);
									}
									break;
							}
							if (form.get("isMandatoty").asBoolean()) {
								listMandatory.add(componentName);
							}
						}
					}
				}
			}
			setStringComment();
			setTaskInfo();
			setFileInfo();
			//TODO Colocar los archivos
		} else {
			logger.log(Level.SEVERE, "No hay datos para completar la pantalla");
		}
	}
	
	private void setTaskInfo() {
		lblCreationDate.setText(gutils.getTimeString(taskInfo.findValue("createdOn")));
		lblCreator.setText(taskInfo.findValue("createdBy").asText().toUpperCase());
		lblStartDate.setText(gutils.getTimeString(taskInfo.findValue("startedOn")));
		lblDueDate.setText(gutils.getTimeString(taskInfo.findValue("dueDate")));
		//TODO: Calcular tiempo de ejecución
		lblExecutionTime.setText("");
		lblFinalizationDate.setText(gutils.getTimeString(taskInfo.findValue("finishedOn")));
		lblResponsable.setText(taskInfo.findValue("assignedTo").asText().toUpperCase());
		//TODO: obtener texto de status
		lblStatus.setText(taskInfo.findValue("status").asText());
		lblId.setText(taskInfo.findValue("id").asText());
	}
	
	private void putFileElement(JsonNode file, String tab) {
		if (file.findValue("name") != null && !file.findValue("name").asText().startsWith(".")) {
			HBox renglon = new HBox();
			renglon.setSpacing(10);
			renglon.setPadding(new Insets(5,5,5,5));
			String lblName = tab + file.findValue("name").asText();
			Label etiqueta = new Label(lblName);
			renglon.getChildren().add(etiqueta);
			panFilesElements.getChildren().add(renglon);
			if (file.findValue("type").asInt() == 1) {
				File openFile = new File(file.findValue("localPath").asText());
		    	if (openFile != null) {
		    		Button button1 = new Button("Abrir");
					button1.setUserData(file.findValue("localPath").asText());
					button1.setOnAction(new EventHandler<ActionEvent>() {
					    @Override
					    public void handle(ActionEvent event) {
					    	logger.info("Evento botón Archivos...");
					    	Button button1 = (Button) event.getSource();
					    	logger.info("Abriendo archivo: " + button1.getUserData().toString());
					    	File openFile = new File(button1.getUserData().toString());
					    	if (openFile!=null) {
					    		getHostServices().showDocument(button1.getUserData().toString());
					    	}
					    }
					});
					renglon.getChildren().add(button1);
		    	}
			}
			
			if (file.findValue("childs") != null) {
				for (JsonNode child:file.findValue("childs")) {
					putFileElement(child, tab + "\t");
				}
			}
		}
	}
	
	
	private void setFileInfo() {
		JsonNode files = taskInfo.findValue("file");
		putFileElement(files, "");
		
		
	}
	
	private String getFormValue(String field) {
		String value = "";
		if (taskInfo.get("form") != null && taskInfo.get("form").isArray()) {
			for (JsonNode form: taskInfo.get("form")) {
				if (form.findValue("id").asText().equals(field)) {
					value = form.findValue("value").asText();
					break;
				}
			}
		}
		return value;
	}
	
	private void setStringComment() {
		String comments = "";
		if (taskInfo.get("comments") != null && taskInfo.get("comments").isArray()) {
			for (JsonNode comment: taskInfo.get("comments")) {
				try {
					String autor = comment.findValue("createdBy").asText();
					String comentario = comment.findValue("comment").asText();
					String date = gutils.getTimeString(comment.findValue("createdOn"));
					comments += "[" + date + "] " + autor + ":\n\t" + comentario + "\n";
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		txtComments.setText(comments);
	}
	
	private void getFormInfo() {
		//TODO: Validar que estén todos los campos obligatorios
		taskInfo.remove("form");
		ArrayNode formArray = taskInfo.putArray("form");
		for (Node node: panFormElements.getChildren()) {
			HBox renglon = (HBox) node;
			for (Node subNode: renglon.getChildren()) {
				if (subNode.getId() != null) {
					ObjectNode newObject = mapper.createObjectNode();
					newObject.put("id", subNode.getId());
					String value = "";
					switch(subNode.getId().substring(0, 6)) {
						case "frmtxt":
							TextField texto = (TextField) subNode;
							value = texto.getText(); 
							break;
						case "frmChk":
							VBox panItems = (VBox) subNode;
							for (Node checks:panItems.getChildren()) {
								CheckBox check = (CheckBox) checks;
								if (check.isSelected()) {
									if (value.length() > 0) {
										value += "|";
									}
									value = check.getText();
								}
							}
							break;
					}
					newObject.put("value", value);
					formArray.add(newObject);
				}
			}
		}
	}
	
	public void handleOptionsEvent(String option) {
		System.out.println("La opción elegida fue..." + option);
		try {
			api = new AquariusAPIClient();
			JsonNode response = api.finishTask(taskInfo.findValue("id").asText(), option, taskInfo.findValue("form").toString(), taskInfo.findValue("comments").toString());
			if (response != null && !response.isNull()) {
				Stage stage = (Stage) btnCancelar.getScene().getWindow();
				stage.close();
			} else {
				System.out.println("Error al intentar finalizar la tarea");
			}
			
		} catch (Exception e) {
			
		}
		
	}
	
	public void addComment() throws Exception {
		if (txtComment.getText().length() > 0) {
			api = new AquariusAPIClient();
			String autor = api.getUserName();
			String comentario = txtComment.getText();
			ObjectNode newObject = mapper.createObjectNode();
			newObject.put("createdBy", autor);
			newObject.put("comment", comentario);
			newObject.putPOJO("createdOn", gutils.getTime());
			ArrayNode commentsArray = null;
			if (taskInfo.get("comments") != null && taskInfo.get("comments").isArray()) {
				commentsArray = (ArrayNode)taskInfo.get("comments");
			} else {
				taskInfo.remove("comments");
				commentsArray = taskInfo.putArray("comments");
			}
			commentsArray.add(newObject);
			setStringComment();
			txtComment.setText("");
			api.saveComments(taskInfo.findValue("id").asText(), taskInfo.findValue("comments").toString());
		}
	}
	
	private HostServices getHostServices() {
		Stage stage = (Stage) btnCancelar.getScene().getWindow();
		logger.info("Obteniendo el HostServices");
		if (stage.getUserData() != null) {
			HostServices hostServices = (HostServices) stage.getUserData();
			logger.info("Se ha obtenido el HostService...");
			return hostServices;
		}
		return null;
	}
	
	public void closeWindowCancel(ActionEvent event) {
		logger.log(Level.INFO, "Cerrando ventana RunTask - Cancelar");
		Stage stage = (Stage) btnCancelar.getScene().getWindow();
		stage.close();
	}
	
	public void pauseTaskAction(ActionEvent event) {
		logger.log(Level.INFO, "PauseTaskAction");
		try {
			api = new AquariusAPIClient();
			JsonNode response = api.pauseTask(taskInfo.findValue("id").asText());
			if (response != null && !response.isNull()) {
				Stage stage = (Stage) btnCancelar.getScene().getWindow();
				stage.close();
			} else {
				System.out.println("Error al intentar posponer la tarea");
			}
		} catch (Exception e) {
			
		}
	}
	
	public void saveTaskAction(ActionEvent event) {
		logger.log(Level.INFO, "SaveTaskAction");
		try {
			getFormInfo();
			JsonNode response = api.saveForm(taskInfo.findValue("id").asText(), taskInfo.findValue("form").toString());
			if (response != null && !response.isNull()) {
				Stage stage = (Stage) btnCancelar.getScene().getWindow();
				stage.close();
			} else {
				System.out.println("Error al intentar guardar la tarea");
			}
		} catch (Exception e) {
			
		}
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
	}

}
