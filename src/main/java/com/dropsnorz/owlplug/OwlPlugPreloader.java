package com.dropsnorz.owlplug;

import javafx.application.Preloader;
import javafx.application.Preloader.StateChangeNotification.Type;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class OwlPlugPreloader extends Preloader {

	private Stage preloaderStage;

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.preloaderStage = primaryStage;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Preloader.fxml"));
		Parent root = loader.load();

		Scene scene = new Scene(root);
		String css = OwlPlugPreloader.class.getResource("/owlplug.css").toExternalForm();
		scene.getStylesheets().add(css);

		primaryStage.initStyle(StageStyle.UNDECORATED);
		primaryStage.setWidth(400);
		primaryStage.setHeight(100);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	@Override
	public void handleStateChangeNotification(StateChangeNotification stateChangeNotification) {
		if (stateChangeNotification.getType() == Type.BEFORE_START) {
			preloaderStage.hide();
		}
		
	}
	
	 @Override
	    public void handleApplicationNotification(PreloaderNotification pn) {
	        if (pn instanceof PreloaderProgressMessage) {
	        	
	        	PreloaderProgressMessage ppm = (PreloaderProgressMessage) pn;
	        	
	        	if(ppm.getType().equals("error")) {
	                Alert alert = new Alert(AlertType.ERROR);
	                alert.setTitle("Error");
	                alert.setHeaderText("Error");
	                alert.setContentText(ppm.getMessage());
	                alert.showAndWait();
	        	}
	        }
	    }  
}
