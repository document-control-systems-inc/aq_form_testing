package com.f2m.aquarius.magic.gui;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f2m.aquarius.magic.AquariusAPIClient;
import com.f2m.aquarius.utils.FileUtils;
import com.fasterxml.jackson.databind.JsonNode;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SelectWorkflowController implements Initializable {
	private final static Logger logger = Logger.getLogger(SelectWorkflowController.class.getName());
	
	@FXML
	private Button btnCancelar;
	@FXML
	private Button btnAceptar;
	@FXML
	private Button btnAddTask;
	@FXML
	private ListView<String> lstProcesos;
	
	private JsonNode defWorkflows;
	private AquariusAPIClient api = null;
	private FileUtils futils = new FileUtils();
	private String folderPath;
	
	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}
	
	public void closeWindowCancel(ActionEvent event) {
		logger.log(Level.INFO, "Cerrando ventana SelectWorkflow - Cancelar");
		Stage stage = (Stage) btnCancelar.getScene().getWindow();
		stage.close();
	}
	
	public void closeWindowAccept(ActionEvent event) {
		logger.log(Level.INFO, "Cerrando ventana SelectWorkflow - Aceptar");
		if (lstProcesos.getSelectionModel().getSelectedIndex() == -1) {
			logger.log(Level.SEVERE, "Falta seleccionar un proceso");
		} else {
			File folder = new File(folderPath);
			if (folder.exists()) {
				int count = 0;
				boolean startProcess = false;
				for (JsonNode defWorkflow:defWorkflows) {
					if (count == lstProcesos.getSelectionModel().getSelectedIndex()) {
						String name = defWorkflow.get("workflowDef").get("name").asText();
						String id = defWorkflow.get("id").asText();
						logger.log(Level.INFO, "id: " + id + "\tName: " + name);
						try {
							api = new AquariusAPIClient();
							JsonNode files = futils.getFileInfo(folderPath);
							String strFiles = null;
							if (files != null) {
								strFiles = files.toString();
							}
							JsonNode workflow = api.createWorkflow(id, strFiles);
							if (workflow != null) {
								if (api.startWorkflow(workflow.asText()) == null) {
									//TODO ventana de error
									logger.log(Level.SEVERE, "No se pudo iniciar el workflow con id: " + id);
								} else {
									startProcess = true;
								}
							} else {
								//TODO: Mensaje de error
								logger.log(Level.SEVERE, "Error al crear la instancia del proceso");
							}
						} catch (Exception e) {
							logger.log(Level.SEVERE, "Error al iniciar el proceso " + name + " con id " + id, e);
						}
						break;
					}
					count++;
				}
				if (startProcess) {
					Stage stage = (Stage) btnCancelar.getScene().getWindow();
					stage.close();
				}
			} else {
				logger.log(Level.SEVERE, "La ruta especificada no pertenece a un Folder: " + folderPath);
			}
		}
	}
	
	public void addTask(ActionEvent event) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/f2m/aquarius/magic/gui/Task.fxml"));
	        Parent parent = fxmlLoader.load();
	        
	        Scene scene = new Scene(parent);
	        Stage stage = new Stage();
	        stage.initModality(Modality.APPLICATION_MODAL);
	        stage.setScene(scene);
	        stage.setTitle("Agregar Tarea");
	        stage.showAndWait();
	        //TODO: Actualizar tareas
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			api = new AquariusAPIClient();
			defWorkflows = api.getDefWorkflows(null);
			if (defWorkflows != null) {
				for (JsonNode defWorkflow:defWorkflows) {
					String name = defWorkflow.findValue("workflowDef").findValue("name").asText();
					if (name != null) {
						lstProcesos.getItems().add(name);
					}
					//lstTareas.getItems().add(.get("name").asText());
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error al inicializar la forma - " + e.getMessage(), e);
			Stage stage = (Stage) btnCancelar.getScene().getWindow();
			stage.close();
		}
	}

}