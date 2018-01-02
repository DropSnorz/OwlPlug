package com.dropsnorz.owlplug;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.dropsnorz.owlplug.services.PluginExplorer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Hello world!
 *
 */
@SpringBootApplication
public class App extends Application
{
	
	private ConfigurableApplicationContext context;
	private Parent rootNode;
	
    
    @Override
    public void init() throws Exception {
        SpringApplicationBuilder builder = new SpringApplicationBuilder(App.class);
        context = builder.run(getParameters().getRaw().toArray(new String[0]));
 
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
        loader.setControllerFactory(context::getBean);
        rootNode = loader.load();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        double width = 800;
        double height = 400;
 
        Scene scene = new Scene(rootNode, width, height);
        String css = App.class.getResource("/owlplug.css").toExternalForm();
		scene.getStylesheets().add(css);
        
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.show();
        
        
    }
 
    @Override
    public void stop() throws Exception {
        context.close();
    }
    
    public static void main(String[] args) {
        launch(App.class, args);
    }
}
