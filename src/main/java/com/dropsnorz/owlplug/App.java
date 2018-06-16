package com.dropsnorz.owlplug;

import java.util.prefs.Preferences;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import com.dropsnorz.owlplug.core.controllers.MainController;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


@SpringBootApplication
public class App extends Application
{

	private ConfigurableApplicationContext context;
	private Parent rootNode;

	@Override
	public void init() throws Exception {

		try {
			SpringApplicationBuilder builder = new SpringApplicationBuilder(App.class);
			builder.headless(false);
			context = builder.run(getParameters().getRaw().toArray(new String[0]));

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
			loader.setControllerFactory(context::getBean);
			rootNode = loader.load();

			MainController mainController = context.getBean(MainController.class);
			mainController.dispatchPostInitialize();
		} catch (BeanCreationException e){
			notifyPreloader(new PreloaderProgressMessage("error", "OwlPlug is already running"));
			throw e;
		}
		catch (Exception e) {
			notifyPreloader(new PreloaderProgressMessage("error", "OwlPlug could not be started"));
			throw e;
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		double width = 1000;
		double height = 620;

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
		System.setProperty("javafx.preloader", "com.dropsnorz.owlplug.OwlPlugPreloader");
		launch(App.class, args);

	}
}
