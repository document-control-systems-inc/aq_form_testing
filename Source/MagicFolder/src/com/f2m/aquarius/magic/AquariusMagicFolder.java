package com.f2m.aquarius.magic;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.f2m.aquarius.magic.gui.SelectTaskController;
import com.f2m.aquarius.magic.gui.SelectWorkflowController;
import com.f2m.aquarius.services.MonitorMagicFolder;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AquariusMagicFolder extends Application {

	private final static Logger logger = Logger.getLogger(AquariusMagicFolder.class.getName());
	private static FileHandler fh = null;
	private AquariusWindowsContextMenu contextMenu = new AquariusWindowsContextMenu();
	private AquariusAPIClient apiClient = null;
	private WinService winService = new WinService();
	
	public AquariusMagicFolder () {
		init();
	}
	
	@Override
	public void init() {
		try {
			System.setProperty("java.util.logging.SimpleFormatter.format", 
		            "%1$tF %1$tT %4$s %2$s %5$s%6$s%n");
			fh = new FileHandler("MagicFolder.log", true);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
		Logger l = Logger.getLogger("");
		fh.setFormatter(new SimpleFormatter());
		l.addHandler(fh);
		l.setLevel(Level.INFO);
	}
	
	public void createContextMenu() {
		contextMenu.createTree();
	}
	
	public void removeContextMenu() {
		contextMenu.removeTree();
	}
	
	public void send(boolean wizard, String folderPath) throws Exception {
		String remotePath = null;
		if (wizard) {
			logger.log(Level.INFO, "Iniciando wizard...");
		}
		logger.log(Level.INFO, "Iniciando env�o de archivos...");
		apiClient = new AquariusAPIClient();
		apiClient.send(folderPath, remotePath);
		logger.log(Level.INFO, "Se ha finalizado env�o de archivos...");
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		
		List<String> args = getParameters().getRaw();
		AquariusMagicFolder magicFolder = new AquariusMagicFolder();
		logger.log(Level.INFO, "Iniciando Proceso");
		
		if (args.size() == 0) {
			magicFolder.createContextMenu();
			if (winService.createWindowsService("AquariusMagicFolder", "Aquarius Magic Folder", "", " -service")) {
				logger.log(Level.INFO, "Se ha creado el servicio");
			}
			Platform.exit();
		} else {
			logger.log(Level.INFO, "Par�metros: " + Arrays.toString(args.toArray()));
			switch(args.get(0)) {
				case "-startService":
					
					break;
				case "-service":
					logger.log(Level.INFO, "Llamado desde Windows Service");
			        Platform.exit();
					break;
				case "-send":
					if (args.size() == 3) {
						if (args.get(2).equals("-w")) {
							magicFolder.send(true, args.get(1));
						} else {
							logger.log(Level.INFO, "Par�metros mal formado: Se requiere -w");
						}
					} else if (args.size() == 2) {
						magicFolder.send(false, args.get(1));
					} else {
						logger.log(Level.INFO, "Par�metros mal formado: exceso de par�metros");
					}
					Platform.exit();
					break;
				case "-uninstall":
					magicFolder.removeContextMenu();
					winService.deleteWindowsService("AquariusMagicFolder");
					Platform.exit();
					break;
				case "-workflow":
					if (args.size() == 3) {
						if (args.get(2).equals("-task")) {
							logger.log(Level.INFO, "Iniciando Select Task");
							FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/f2m/aquarius/magic/gui/SelectTask.fxml"));
					        Parent root = fxmlLoader.load();
							SelectTaskController controller = fxmlLoader.<SelectTaskController>getController();
							controller.setFolderPath(args.get(1));
							primaryStage.setTitle("Asignar Tarea");
					        primaryStage.setScene(new Scene(root));
					        primaryStage.show();
						} else if (args.get(2).equals("-workflow")) {
							logger.log(Level.INFO, "Iniciando Select Workflow");
							File folder = new File(args.get(1));
							if (folder.exists()) {
								FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/f2m/aquarius/magic/gui/SelectWorkflow.fxml"));
						        Parent root = fxmlLoader.load();
								SelectWorkflowController controller = fxmlLoader.<SelectWorkflowController>getController();
								controller.setFolderPath(args.get(1));
								primaryStage.setTitle("Asignar Proceso");
						        primaryStage.setScene(new Scene(root));
						        primaryStage.show();
							} else {
								logger.log(Level.INFO, "Carpeta no existe. Se requiere seleccionar una carpeta");
								Platform.exit();
							}
						} else {
							logger.log(Level.INFO, "Par�metros mal formado: Se requiere -task o -workflow");
							Platform.exit();
						}
					} else {
						logger.log(Level.INFO, "Iniciando Bandeja de Tareas");
						FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/f2m/aquarius/magic/gui/TaskList.fxml"));
				        Parent root = fxmlLoader.load();
						primaryStage.setTitle("Bandeja de Tareas");
				        primaryStage.setScene(new Scene(root));
				        primaryStage.setUserData(this.getHostServices());
				        primaryStage.show();
					}
					break;
				case "-conf":
					if (args.size() == 2) {
						if (args.get(1).equals("-conn")) {
							try {
								logger.log(Level.INFO, "Se seleccionó configuración de conexión");
								FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/f2m/aquarius/magic/gui/Settings.fxml"));
						        Parent root = fxmlLoader.load();
								primaryStage.setTitle("Configuración de Conexión");
						        primaryStage.setScene(new Scene(root));
						        logger.log(Level.INFO, "Antes del show");
						        primaryStage.show();
							} catch (Exception e) {
								logger.log(Level.SEVERE, "Error al ejecutar la pantalla", e);
							}
						} else {
							logger.log(Level.INFO, "Par�metros mal formado: Se requiere -conn");
							Platform.exit();
						}
					} else {
						logger.log(Level.INFO, "Par�metros mal formado: exceso de par�metros");
						Platform.exit();
					}
					break;
			}
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
