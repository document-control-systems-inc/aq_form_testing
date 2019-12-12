package com.f2m.aquarius.magic.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class TaskOut extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
            // Read file fxml and draw interface.
            Parent root = FXMLLoader.load(getClass()
                    .getResource("/com/f2m/aquarius/magic/gui/TaskOut.fxml"));
 
            primaryStage.setTitle("Agregar Salida");
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
            
         
        } catch(Exception e) {
            e.printStackTrace();
        }
		
	}

}
