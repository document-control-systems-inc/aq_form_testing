package com.f2m.aquarius.magic.gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class TaskOutController implements Initializable {
	
	private final static Logger logger = Logger.getLogger(TaskOutController.class.getName());
	@FXML
	private Button btnCancelar;
	@FXML
	private Button btnAceptar;
	@FXML
	private Button btnAdd;
	@FXML
	private Button btnUp;
	@FXML
	private Button btnDown;
	@FXML
	private Button btnRemove;
	@FXML
	private ListView<String> lstSalidas;
	@FXML
	private TextField txtSalida;
	
	private List<String> originalList;
	
	
	public void addItems(List<String> items) {
		originalList = new ArrayList<String>();
		for (int i = 0; i < items.size(); i++) {
			originalList.add(items.get(i));
		}
		lstSalidas.getItems().clear();
		lstSalidas.getItems().addAll(items);
	}
	
	public List<String> getItems() {
		return originalList;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {}

	public void closeWindowCancel(ActionEvent event) {
		logger.log(Level.INFO, "Botón Cancelar");
		Stage stage = (Stage) btnCancelar.getScene().getWindow();
		stage.close();
	}
	
	public void closeWindowAccept(ActionEvent event) {
		logger.log(Level.INFO, "Botón Aceptar");
		originalList = lstSalidas.getItems();
		Stage stage = (Stage) btnAceptar.getScene().getWindow();
		stage.close();
	}
	
	public void addSalida(ActionEvent event) {
		if (txtSalida.getText().length() == 0) {
			logger.log(Level.INFO, "Error: Falta texto");
		} else {
			if (lstSalidas.getItems().contains(txtSalida.getText())) {
				logger.log(Level.INFO, "Error: Ya contiene la salida");
			} else {
				lstSalidas.getItems().add(txtSalida.getText());
				txtSalida.setText("");
				txtSalida.requestFocus();
			}
		}
	}
	
	public void upSalida(ActionEvent event) {
		if (lstSalidas.getSelectionModel().getSelectedIndex() > 0) {
			int indexSeleccionado = lstSalidas.getSelectionModel().getSelectedIndex();
			String itemSeleccionado = lstSalidas.getItems().remove(indexSeleccionado);
			lstSalidas.getItems().add(indexSeleccionado - 1, itemSeleccionado);
			lstSalidas.getSelectionModel().select(indexSeleccionado - 1);
		}
	}
	
	public void downSalida(ActionEvent event) {
		if (lstSalidas.getSelectionModel().getSelectedIndex() < lstSalidas.getItems().size() - 1) {
			int indexSeleccionado = lstSalidas.getSelectionModel().getSelectedIndex();
			String itemSeleccionado = lstSalidas.getItems().remove(indexSeleccionado);
			lstSalidas.getItems().add(indexSeleccionado + 1, itemSeleccionado);
			lstSalidas.getSelectionModel().select(indexSeleccionado + 1);
		}
	}
	
	public void removeSalida(ActionEvent event) {
		if (lstSalidas.getSelectionModel().getSelectedIndex() > -1) {
			lstSalidas.getItems().remove(lstSalidas.getSelectionModel().getSelectedIndex());
		}
	}
}
