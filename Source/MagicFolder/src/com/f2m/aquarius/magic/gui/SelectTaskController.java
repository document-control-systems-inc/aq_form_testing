package com.f2m.aquarius.magic.gui;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f2m.aquarius.magic.AquariusAPIClient;
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

public class SelectTaskController implements Initializable {
	private final static Logger logger = Logger.getLogger(SelectTaskController.class.getName());
	
	@FXML
	private Button btnCancelar;
	@FXML
	private Button btnAceptar;
	@FXML
	private Button btnAddTask;
	@FXML
	private ListView<String> lstTareas;
	
	private JsonNode defTasks;
	private AquariusAPIClient api = null;
	private String folderPath;
	
	public void setFolderPath(String folderPath) {
		this.folderPath = folderPath;
	}
	
	public void closeWindowCancel(ActionEvent event) {
		logger.log(Level.INFO, "Cerrando ventana SelectTask - Cancelar");
		Stage stage = (Stage) btnCancelar.getScene().getWindow();
		stage.close();
	}
	
	public void closeWindowAccept(ActionEvent event) {
		logger.log(Level.INFO, "Cerrando ventana SelectTask - Aceptar");
		if (lstTareas.getSelectionModel().getSelectedIndex() == -1) {
			System.out.println("Error: Falta seleccionar una tarea");
		} else {
			int count = 0;
			for (JsonNode defTask:defTasks) {
				if (count == lstTareas.getSelectionModel().getSelectedIndex()) {
					String name = defTask.get("taskDef").get("name").asText();
					String id = defTask.get("id").asText();
					logger.log(Level.INFO, "id: " + id + "\tName: " + name);
					break;
				}
				count++;
			}
			System.out.println(folderPath);
			Stage stage = (Stage) btnCancelar.getScene().getWindow();
			stage.close();
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
			defTasks = api.getDefTasks(null);
			if (defTasks != null) {
				for (JsonNode defTask:defTasks) {
					String name = defTask.get("taskDef").get("name").asText();
					if (name != null) {
						lstTareas.getItems().add(name);
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
