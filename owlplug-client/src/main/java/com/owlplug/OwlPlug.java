/* OwlPlug
 * Copyright (C) 2019 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OwlPlug is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OwlPlug.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.owlplug;

import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.controllers.MainController;
import java.beans.PropertyVetoException;
import java.io.File;
import java.time.Duration;
import java.util.prefs.Preferences;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javax.sql.DataSource;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@SpringBootApplication
public class OwlPlug extends Application {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private ConfigurableApplicationContext context;
  private Parent rootNode;
  
  @Autowired
  private Environment environment;
  
  
  /**
   * JavaFX Application initialization method. It boostraps Spring boot
   * application context and binds it to FXMLLoader controller factory.
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
   * The main JavaFX applications entry point. The start method is called after
   * the {@link #init} method method has returned
   */
  @Override
  public void start(Stage primaryStage) throws Exception {
    double width = 1020;
    double height = 700;

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
  

  @Bean
  @DependsOn("workspaceDirectoryInitializer")
  public DataSource datasource() throws PropertyVetoException {
      final DriverManagerDataSource dataSource = new DriverManagerDataSource();
      dataSource.setDriverClassName(environment.getProperty("spring.datasource.driver-class-name"));
      dataSource.setUrl(environment.getProperty("spring.datasource.url"));
      dataSource.setUsername(environment.getProperty("spring.datasource.username"));
      dataSource.setPassword(environment.getProperty("spring.datasource.password"));
      return dataSource;
  }

  /**
   * Initialize Application preferences.
   * 
   * @return
   */
  @Bean
  public Preferences getPreference() {
    return Preferences.userRoot().node("com.owlplug.user");

  }

  /**
   * Initialize EhCache CacheManager instance {@see CacheManager}.
   * 
   * @return The ChacheManager instance
   */
  @Bean
  public CacheManager getCacheManager() {

    CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
        .with(CacheManagerBuilder.persistence(ApplicationDefaults.getUserDataDirectory() + File.separator + "cache"))
        .withCache("image-cache", CacheConfigurationBuilder
            .newCacheConfigurationBuilder(String.class, byte[].class,
                ResourcePoolsBuilder.newResourcePoolsBuilder().heap(100, MemoryUnit.MB).disk(700, MemoryUnit.MB, true))
            .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofDays(10))))
        .build();
    cacheManager.init();

    return cacheManager;
  }

  /**
   * Called by JavaFx platform on closure request. Post execution cleaning
   * operation should be operated here.
   */
  @Override
  public void stop() throws Exception {
    context.close();
  }

  /**
   * Main method called on JAR execution. It bootstraps JavaFx Application and
   * it's preloader {@see com.owlplug.OwlPlugPreloader}
   *
   * @param args The command line arguments given on JAR execution. Usually empty.
   */
  public static void main(String[] args) {
    System.setProperty("javafx.preloader", "com.owlplug.OwlPlugPreloader");
    launch(OwlPlug.class, args);

  }
}
