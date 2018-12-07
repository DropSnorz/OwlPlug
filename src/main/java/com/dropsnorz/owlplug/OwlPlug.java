package com.dropsnorz.owlplug;

import com.dropsnorz.owlplug.core.components.ApplicationDefaults;
import com.dropsnorz.owlplug.core.controllers.MainController;
import java.util.prefs.Preferences;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.sf.ehcache.CacheManager;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class OwlPlug extends Application {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());


	private ConfigurableApplicationContext context;
	private Parent rootNode;
	
	/**
	 * JavaFX Application initialization method.
	 * It boostraps Spring boot application context and binds it to FXMLLoader controller factory.
	 */
	@Override
	public void init() throws Exception {

		try {
			SpringApplicationBuilder builder = new SpringApplicationBuilder(OwlPlug.class);
			builder.headless(false);
			context = builder.run(getParameters().getRaw().toArray(new String[0]));

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
			loader.setControllerFactory(context::getBean);
			rootNode = loader.load();

			MainController mainController = context.getBean(MainController.class);
			mainController.dispatchPostInitialize();
		} catch (BeanCreationException e) {
			if (e.getRootCause() instanceof HibernateException) {
				log.error("OwlPlug is maybe already running", e);
				notifyPreloader(new PreloaderProgressMessage("error", "OwlPlug is maybe already running"));
			} else {
				log.error("Error during application context initialization", e);
				notifyPreloader(new PreloaderProgressMessage("error", "Error during application context initialization"));
			}

			throw e;
		} catch (Exception e) {
			log.error("OwlPlug could not be started", e);
			notifyPreloader(new PreloaderProgressMessage("error", "OwlPlug could not be started"));
			throw e;
		}
	}

	/**
	 * The main JavaFX applications entry point. 
	 * The start method is called after the {@link #init} method method has returned
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		double width = 1020;
		double height = 650;
		
		Scene scene = new Scene(rootNode, width, height);
		String css = OwlPlug.class.getResource("/owlplug.css").toExternalForm();
		scene.getStylesheets().add(css);
		
		primaryStage.getIcons().add(ApplicationDefaults.owlplugLogo);
		primaryStage.setTitle(ApplicationDefaults.APPLICATION_NAME);

		primaryStage.setScene(scene);
		primaryStage.setHeight(height);
		primaryStage.setWidth(width);
		primaryStage.setMinHeight(height);
		primaryStage.setMinWidth(width);
		primaryStage.centerOnScreen();

		primaryStage.show();

	}

	/**
	 * Initialize Application preferences.
	 * @return
	 */
	@Bean
	public Preferences getPreference() {
		return 	Preferences.userRoot().node("com.dropsnorz.owlplug.user");

	}

	/**
	 * Initialize spring boot embedded Tomcat server.
	 * Used to catch loopback requests from google drive OAuth API.
	 * @return 
	 */
	@Bean 
	public ServletWebServerFactory servletWebServerFactory() {
		return new TomcatServletWebServerFactory();
	}
	
	/**
	 * Initialize EhCache CacheManager instance {@see CacheManager}.
	 * @return The ChacheManager instance
	 */
	@Bean 
	public CacheManager getCacheManager() {
		return CacheManager.create();
	}

	/**
	 * Called by JavaFx platform on closure request.
	 * Post execution cleaning operation should be operated here.
	 */
	@Override
	public void stop() throws Exception {
		context.close();
	}

	/**
     * Main method called on JAR execution.
     * It bootstraps JavaFx Application and it's preloader {@see com.dropsnorz.owlplug.OwlPlugPreloader}
     *
     * @param args The command line arguments given on JAR execution. Usually empty.
     */
	public static void main(String[] args) {
		System.setProperty("javafx.preloader", "com.dropsnorz.owlplug.OwlPlugPreloader");
		launch(OwlPlug.class, args);

	}
}
