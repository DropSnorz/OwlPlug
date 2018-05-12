package com.dropsnorz.owlplug;

import java.util.prefs.Preferences;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

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
		builder.headless(false);
		context = builder.run(getParameters().getRaw().toArray(new String[0]));

		FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
		loader.setControllerFactory(context::getBean);
		rootNode = loader.load();
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		double width = 900;
		double height = 600;

		Scene scene = new Scene(rootNode, width, height);
		String css = App.class.getResource("/owlplug.css").toExternalForm();
		scene.getStylesheets().add(css);

		primaryStage.setScene(scene);
		primaryStage.setHeight(height);
		primaryStage.setWidth(width);
		primaryStage.setMinHeight(height);
		primaryStage.setMinWidth(width);
		primaryStage.centerOnScreen();
		primaryStage.show();

	}

	@Bean
	Preferences getPreference() {
		return 	Preferences.userRoot().node("com.dropsnorz.owlplug.user");

	}

	@Bean 
	ServletWebServerFactory servletWebServerFactory(){
		return new TomcatServletWebServerFactory();
	}

	@Override
	public void stop() throws Exception {
		context.close();
	}

	public static void main(String[] args) {
		launch(App.class, args);
	}
}
