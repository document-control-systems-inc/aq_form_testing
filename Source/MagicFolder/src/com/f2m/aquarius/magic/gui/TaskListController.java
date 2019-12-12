package com.f2m.aquarius.magic.gui;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f2m.aquarius.magic.AquariusAPIClient;
import com.fasterxml.jackson.databind.JsonNode;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TaskListController implements Initializable {
	private final static Logger logger = Logger.getLogger(TaskListController.class.getName());
	
	@FXML
	private Button btnCancelar;
	@FXML
	private Button btnAceptar;
	@FXML
	private TableView<TaskRow> tblList;
	@FXML
	private CheckBox chkInitial;
	@FXML
	private CheckBox chkInProgress;
	@FXML
	private CheckBox chkPause;
	@FXML
	private CheckBox chkFinalized;
	@FXML
	private CheckBox chkCancelled;
	
	
	private AquariusAPIClient api = null;
	
	public void updateStatus(ActionEvent event) {
		logger.log(Level.INFO, "Actualizando status");
		updateTaskList();
	}
	
	private String getStatus() {
		String actualStatus = "";
		if (chkInitial.isSelected()) {
			actualStatus += "0";
		}
		if (chkInProgress.isSelected()) {
			if (actualStatus.length() > 0) {
				actualStatus += "|";
			}
			actualStatus += "1";
		}
		if (chkPause.isSelected()) {
			if (actualStatus.length() > 0) {
				actualStatus += "|";
			}
			actualStatus += "2";
		}
		if (chkFinalized.isSelected()) {
			if (actualStatus.length() > 0) {
				actualStatus += "|";
			}
			actualStatus += "3";
		}
		if (chkCancelled.isSelected()) {
			if (actualStatus.length() > 0) {
				actualStatus += "|";
			}
			actualStatus += "4";
		}
		return actualStatus;
	}
	
	
	public HostServices getHostServices() {
		Stage stage = (Stage) btnCancelar.getScene().getWindow();
		if (stage.getUserData() != null) {
			HostServices hostServices = (HostServices) stage.getUserData();
			return hostServices;
		}
		return null;
	}
	
	
	public void closeWindowCancel(ActionEvent event) {
		logger.log(Level.INFO, "Cerrando ventana TaskList - Cancelar");
		Stage stage = (Stage) btnCancelar.getScene().getWindow();
		stage.close();
	}
	
	public void createWorkflow(ActionEvent event) {
		logger.log(Level.INFO, "Creando Workflow");
		try {
			Stage stage = (Stage) btnCancelar.getScene().getWindow();
			FileChooser fileChooser = new FileChooser();
			List<File> files = fileChooser.showOpenMultipleDialog(stage);
			
			/*
			String idDefWorkflow = "54ad6796-ea29-4bf1-9878-15e92c14c79e";
			api = new AquariusAPIClient();
			JsonNode workflow = api.createWorkflow(idDefWorkflow);
			if (workflow != null) {
				if (api.startWorkflow(workflow.asText()) == null) {
					System.out.println("Error al iniciar el Workflow!!!");
				}
			} else {
				System.out.println("Error al crear el Workflow");
			}
			updateTaskList();
			*/
		} catch (Exception e) {
			
		}
	}
	
	private String getValue(JsonNode node) {
		String response = "";
		if (!node.isNull()) {
			response = node.asText();
		}
		return response;
	}
	
	public void updateTaskList() {
		String status = getStatus();
		ObservableList<TaskRow> list = FXCollections.observableArrayList();
		try {
			JsonNode tasks = api.getTaskList(status);
			if (tasks != null) {
				for (JsonNode task:tasks) {
					TaskRow row = new TaskRow(getValue(task.findValue("id")), 
							getValue(task.findValue("workflow")), 
							getValue(task.findValue("task")),
							getValue(task.findValue("file")),
							getValue(task.findValue("createdOn")),
							getValue(task.findValue("dueDate")),
							getValue(task.findValue("responsable")),
							getValue(task.findValue("status")),
							this);
					list.add(row);
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error al actualizar la tabla - " + e.getMessage(), e);
		}
		tblList.getItems().clear();
		tblList.setItems(list);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			api = new AquariusAPIClient();
			TableColumn<TaskRow, String> colWorkflow = new TableColumn<TaskRow, String>("Proceso");
			TableColumn<TaskRow, String> colTask = new TableColumn<TaskRow, String>("Tarea");
			TableColumn<TaskRow, String> colFile = new TableColumn<TaskRow, String>("Archivo");
			TableColumn<TaskRow, String> colCreatedDate = new TableColumn<TaskRow, String>("Creada");
			TableColumn<TaskRow, String> colDueDate = new TableColumn<TaskRow, String>("Vencimiento");
			TableColumn<TaskRow, String> colResponsable = new TableColumn<TaskRow, String>("Responsable");
			TableColumn<TaskRow, String> colStatus = new TableColumn<TaskRow, String>("Status");
			TableColumn<TaskRow, Button> action = new TableColumn<TaskRow, Button>("Acción");
			
			colWorkflow.setCellValueFactory(new PropertyValueFactory<>("workflowName"));
			colTask.setCellValueFactory(new PropertyValueFactory<>("taskName"));
			colFile.setCellValueFactory(new PropertyValueFactory<>("fileName"));
			colCreatedDate.setCellValueFactory(new PropertyValueFactory<>("createdDate"));
			colDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
			colResponsable.setCellValueFactory(new PropertyValueFactory<>("responsable"));
			colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
			action.setCellValueFactory(new PropertyValueFactory<>("action"));
			updateTaskList();
			tblList.getColumns().addAll(colWorkflow, colTask, colFile, colCreatedDate, colDueDate, colResponsable, colStatus, action);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error al inicializar la forma - " + e.getMessage(), e);
			Stage stage = (Stage) btnCancelar.getScene().getWindow();
			stage.close();
		}
		
		
	}

}
