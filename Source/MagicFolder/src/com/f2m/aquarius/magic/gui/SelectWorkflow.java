package com.f2m.aquarius.magic.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SelectWorkflow extends Application {

	private static String folderPath;
	
	public static void setFolder(String folder) {
		folderPath = folder;
	}
	
	public static void main(String[] args) {
		setFolder(args[0]);
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
            // Read file fxml and draw interface.
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/f2m/aquarius/magic/gui/SelectWorkflow.fxml"));
	        Parent root = fxmlLoader.load();
			SelectWorkflowController controller = fxmlLoader.<SelectWorkflowController>getController();
			controller.setFolderPath(folderPath);
            primaryStage.setTitle("Seleccionar Proceso");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
         
        } catch(Exception e) {
            e.printStackTrace();
        }
		
	}

}
