package com.f2m.aquarius.magic.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f2m.aquarius.magic.AquariusAPIClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TaskController implements Initializable {

	private final static Logger logger = Logger.getLogger(TaskController.class.getName());
	@FXML
	private Button btnCancelar;
	@FXML
	private Button btnAceptar;
	@FXML
	private TextField txtName;
	@FXML
	private TextField txtFile;
	@FXML
	private RadioButton rbtDuration;
	@FXML
	private RadioButton rbtDueDate;
	@FXML
	ToggleGroup dueTask;
	@FXML
	ToggleGroup multifile;
	@FXML
	private RadioButton rbtSimple;
	@FXML
	private RadioButton rbtMultiple;
	//Duración:
	@FXML
	Label lblDuration;
	@FXML
	TextField txtDuration;
	@FXML
	ComboBox<String> cmbDuration;
	//Fecha:
	@FXML
	Label lblDueDate;
	@FXML
	DatePicker dtpDueDate;
	@FXML
	private ListView<String> lstSalidas;
	
	private ObjectMapper mapper = new ObjectMapper();
	private AquariusAPIClient api = null;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dueTask.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {

				if (dueTask.getSelectedToggle() != null) {
					RadioButton selectedRadioButton = (RadioButton) dueTask.getSelectedToggle();
					switch (selectedRadioButton.getId()) {
					case "rbtDueDate":
						lblDuration.setVisible(false);
						txtDuration.setVisible(false);
						cmbDuration.setVisible(false);
						lblDueDate.setVisible(true);
						dtpDueDate.setVisible(true);
						break;
					case "rbtDuration":
						lblDuration.setVisible(true);
						txtDuration.setVisible(true);
						cmbDuration.setVisible(true);
						lblDueDate.setVisible(false);
						dtpDueDate.setVisible(false);
						break;
					}
				}

			}
		});
		cmbDuration.getItems().addAll(new String("minutos"), new String("horas"), new String("días"), new String("meses"), new String("años"));
		cmbDuration.getSelectionModel().select(0);
	}
	
	public void closeWindowCancel(ActionEvent event) {
		logger.log(Level.INFO, "Cerrando ventana Task");
		Stage stage = (Stage) btnCancelar.getScene().getWindow();
		stage.close();
	}
	
	public void closeWindowAccept(ActionEvent event) {
		if (txtName.getText().length() == 0) {
			logger.log(Level.INFO, "Error: Falta nombre de Tarea");
		} else {
			if (lstSalidas.getItems().size() == 0) {
				logger.log(Level.INFO, "Error: Falta opciones de salida");
			} else {
				if (rbtDuration.isSelected()) {
					if (txtDuration.getText().length() == 0) {
						logger.log(Level.INFO, "Error: Falta colocar duración");
					} else {
						//TODO: Validar que sea numérico
						if (cmbDuration.getSelectionModel().getSelectedIndex() == -1) {
							logger.log(Level.INFO, "Error: No se ha seleccionado el tipo de duración");
						} else {
							try {
								saveTask();
							} catch (Exception e) {
								logger.log(Level.SEVERE, "Error: No se ha guardado la tarea", e);
							}
						}
					}
				} else {
					if (dtpDueDate.getEditor().getText().length() == 0) {
						logger.log(Level.INFO, "Error: Falta elegir fecha");
					} else {
						try {
							saveTask();
						} catch (Exception e) {
							logger.log(Level.SEVERE, "Error: No se ha guardado la tarea", e);
						}
					}
				}
			}
		}
		
		
	}
	
	private void saveTask() throws Exception {
		ObjectNode newObject = mapper.createObjectNode();
		newObject.put("name", txtName.getText());
		ArrayNode options = newObject.putArray("options");
		for (String option: lstSalidas.getItems()) {
			options.add(option);
		}
		if (rbtSimple.isSelected()) {
			newObject.put("multifile", false);
		} else {
			newObject.put("multifile", true);
		}
		if (rbtDuration.isSelected()) {
			ObjectNode jsonDuration = mapper.createObjectNode();
			jsonDuration.put("quantity", Integer.parseInt(txtDuration.getText()));
			jsonDuration.put("type", cmbDuration.getSelectionModel().getSelectedIndex());
			newObject.putPOJO("duration", jsonDuration);
		} else {
			newObject.put("dueDate", dtpDueDate.getEditor().getText());
		}
		logger.log(Level.INFO, "Json: " + newObject);
		api = new AquariusAPIClient();
		if (api.setDefTask(newObject.toString())) {
			Stage stage = (Stage) btnCancelar.getScene().getWindow();
			stage.close();
		} else {
			logger.log(Level.INFO, "Error al guardar la Definición de Tarea");
		}
	}
	
	public void addSalidas(ActionEvent event) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/f2m/aquarius/magic/gui/TaskOut.fxml"));
	        Parent parent = fxmlLoader.load();
	        TaskOutController controller = fxmlLoader.<TaskOutController>getController();
	        List<String> items = new ArrayList<String>();
	        for (int i = 0; i < lstSalidas.getItems().size(); i++) {
	        	items.add(lstSalidas.getItems().get(i));
	        }
	        controller.addItems(items);
	        Scene scene = new Scene(parent);
	        Stage stage = new Stage();
	        stage.initModality(Modality.APPLICATION_MODAL);
	        stage.setScene(scene);
	        stage.setTitle("Modificar Salidas");
	        stage.showAndWait();
	        lstSalidas.getItems().clear();
	        lstSalidas.getItems().addAll(controller.getItems());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
