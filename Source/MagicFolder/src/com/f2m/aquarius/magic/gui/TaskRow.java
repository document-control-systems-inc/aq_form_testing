package com.f2m.aquarius.magic.gui;

import com.f2m.aquarius.magic.AquariusAPIClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TaskRow {

	private String id;
	private String workflowName;
	private String taskName;
	private String fileName;
	private String createdDate;
	private String dueDate;
	private String responsable;
	private String status;
	private Button action;
	private TaskListController parentController;

	public TaskRow(String id, String workflowName, String taskName, String fileName, String createdDate, String dueDate,
			String responsable, String status, TaskListController parentController) {
		this.id = id;
		this.workflowName = workflowName;
		this.taskName = taskName;
		this.fileName = fileName;
		this.createdDate = createdDate;
		this.dueDate = dueDate;
		this.responsable = responsable;
		this.status = status;
		this.parentController = parentController;
		String btnAction = "";
		switch (this.status) {
			case "Creado":
				btnAction = "Iniciar";
				break;
			case "En progreso":
				btnAction = "Continuar";
				break;
			case "Pospuesta":
				btnAction = "Reanudar";
				break;
			case "Finalizada":
				btnAction = "Ver";
				break;
			case "Cancelada":
				btnAction = "Ver";
				break;
		}
		this.action = new Button(btnAction);
		this.action.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				System.out.println("Ejecutando tarea con Id: " + id);
				try {
					AquariusAPIClient api = new AquariusAPIClient();
					JsonNode tasks = api.getTasks(id);
					if (tasks != null) {
						for (JsonNode task:tasks) {
							String status = task.findValue("status").asText();
							switch(status) {
								case "0":
								case "2":
									api.startTask(id);
									break;
							}
							
							FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/f2m/aquarius/magic/gui/RunTask.fxml"));
					        Parent parent = fxmlLoader.load();
					        RunTaskController controller = fxmlLoader.<RunTaskController>getController();
					        controller.setTaskInfo((ObjectNode)task);
					        Scene scene = new Scene(parent);
					        Stage stage = new Stage();
					        stage.setUserData(parentController.getHostServices());
					        stage.initModality(Modality.APPLICATION_MODAL);
					        stage.setScene(scene);
					        stage.setTitle("Tarea");
					        stage.showAndWait();
					        break;
						}
					}
					parentController.updateTaskList();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getWorkflowName() {
		return workflowName;
	}

	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	public String getDueDate() {
		return dueDate;
	}

	public void setDueDate(String dueDate) {
		this.dueDate = dueDate;
	}

	public String getResponsable() {
		return responsable;
	}

	public void setResponsable(String responsable) {
		this.responsable = responsable;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Button getAction() {
		return action;
	}

	public void setAction(Button action) {
		this.action = action;
	}

	public TaskListController getParentController() {
		return parentController;
	}

	public void setParentController(TaskListController parentController) {
		this.parentController = parentController;
	}
}
