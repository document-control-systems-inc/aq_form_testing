package com.f2m.aquarius.magic.gui;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f2m.aquarius.magic.AquariusAPIClient;
import com.f2m.aquarius.magic.AquariusConfFile;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SettingsController implements Initializable {

	private final static Logger logger = Logger.getLogger(SettingsController.class.getName());
	private ObjectMapper mapper = new ObjectMapper();
	@FXML
	private Button btnAceptar;
	@FXML
	private Button btnCancelar;
	@FXML
	private TextField txtURL;
	@FXML
	private TextField txtUser;
	@FXML
	private PasswordField txtPwd;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		try {
			logger.log(Level.INFO, "Iniciando pantalla de Configuración");
			AquariusConfFile confFile = new AquariusConfFile();
			JsonNode configParams = confFile.readConfFile();
			if (configParams != null) {
				String url = configParams.get("ws_url").asText();
				if (url != null && url.length() > 0) {
					txtURL.setText(url);
				}
				String user = configParams.get("userName").asText();
				if (user != null && user.length() > 0) {
					txtUser.setText(user);
				}
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Error al intentar abrir el archivo de configuraci�n", e);
		}
	}
	
	public void saveChages(ActionEvent event) {
		logger.log(Level.INFO, "Settings - Bot�n Aceptar");
		//Obtengo informaci�n de la URL, user y pwd:
		if (txtURL.getText().length() == 0) {
			
			logger.log(Level.INFO, "Error: Falta URL");
		} else {
			if (txtUser.getText().length() == 0) {
				System.out.println("Error: Falta User");
			} else {
				if (txtPwd.getText().length() == 0) {
					logger.log(Level.INFO, "Error: Falta Pwd");
				} else {
					ObjectNode newObject = mapper.createObjectNode();
					newObject.put("ws_url", txtURL.getText());
					newObject.put("userName", txtUser.getText());
					newObject.put("password", txtPwd.getText());
					
					AquariusAPIClient api = new AquariusAPIClient(newObject);
					if (api.testLogin()) {
						AquariusConfFile conf = new AquariusConfFile();
						conf.writeConf(newObject);
						Stage stage = (Stage) btnCancelar.getScene().getWindow();
						stage.close();
					} else {
						logger.log(Level.INFO, "Error: No se puede conectar al servidor");
					}
				}
			}
		}
	}
	
	public void closeWindow(ActionEvent event) {
		Stage stage = (Stage) btnCancelar.getScene().getWindow();
		stage.close();
	}
}
