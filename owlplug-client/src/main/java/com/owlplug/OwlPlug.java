/* OwlPlug
 * Copyright (C) 2021 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
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

import com.owlplug.controls.OwlPlugControlsResources;
import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.components.telemetry.StartupFailureTelemetry;
import com.owlplug.core.controllers.MainController;
import com.owlplug.core.utils.FX;
import com.owlplug.theme.OwlPlugDarkTheme;
import com.zaxxer.hikari.HikariDataSource;
import java.nio.file.Paths;
import java.time.Duration;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javax.sql.DataSource;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
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

@SpringBootApplication
public class OwlPlug extends Application {

  private final Logger log = LoggerFactory.getLogger(this.getClass());
  
  @Autowired
  private Environment environment;

  private ConfigurableApplicationContext context;
  private Parent rootNode;

  /**
   * Main method called on JAR execution. It bootstraps JavaFx Application and
   * it's preloader {@see com.owlplug.OwlPlugPreloader}
   *
   * @param args The command line arguments given on JAR execution. Usually empty.
   */
  public static void boot(String[] args) {
    System.setProperty("javafx.preloader", "com.owlplug.OwlPlugPreloader");
    launch(OwlPlug.class, args);

  }

  /**
   * JavaFX Application initialization method. It boostraps Spring boot
   * application context and binds it to FXMLLoader controller factory.
   * Running from the JavaFX-Launcher thread.
   */
  @Override
  public void init() throws Exception {

    try {
      SpringApplicationBuilder builder = new SpringApplicationBuilder(OwlPlug.class);
      builder.headless(false);
      builder.listeners(new StartupFailureTelemetry());
      context = builder.run(getParameters().getRaw().toArray(new String[0]));

      FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainView.fxml"));
      loader.setControllerFactory(context::getBean);
      rootNode = loader.load();

      MainController mainController = context.getBean(MainController.class);

      FX.run(mainController::dispatchPostInitialize);

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
   * the {@link #init} method has returned
   */
  @Override
  public void start(Stage primaryStage) throws Exception {
    double width = 1100;
    double height = 820;

    Application.setUserAgentStylesheet(new OwlPlugDarkTheme().getUserAgentStylesheet());


    Scene scene = new Scene(rootNode, width, height);

    String owlplugControlsCss = OwlPlugControlsResources.load("/css/owlplug-controls.css").toExternalForm();
    String owlplugCss = OwlPlug.class.getResource("/owlplug.css").toExternalForm();

    scene.getStylesheets().add(owlplugControlsCss);
    scene.getStylesheets().add(owlplugCss);

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
  public DataSource datasource() {
    final HikariDataSource dataSource = new HikariDataSource();
    dataSource.setDriverClassName(environment.getProperty("spring.datasource.driver-class-name"));
    dataSource.setJdbcUrl(environment.getProperty("spring.datasource.url"));
    dataSource.setUsername(environment.getProperty("spring.datasource.username"));
    dataSource.setPassword(environment.getProperty("spring.datasource.password"));
    dataSource.setMaximumPoolSize(4);
    dataSource.setMinimumIdle(1);
    return dataSource;
  }

  /**
   * Initialize EhCache CacheManager instance {@see CacheManager}.
   * 
   * @return The CacheManager instance
   */
  @Bean
  public CacheManager getCacheManager() {
    CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
        .with(CacheManagerBuilder.persistence(
                Paths.get(ApplicationDefaults.getUserDataDirectory(),  "cache").toString())
        )
        // Disk tier: persists encoded image bytes (PNG/JPEG) across app restarts,
        // so images don't need to be re-downloaded across sessions.
        .withCache("image-disk-cache", CacheConfigurationBuilder
            .newCacheConfigurationBuilder(String.class, byte[].class,
                ResourcePoolsBuilder.newResourcePoolsBuilder().disk(700, MemoryUnit.MB, true))
            .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofDays(10))))
        // Memory tier: holds live, GPU-backed javafx.scene.image.Image instances.
        // Heap-only, bounded LRU, not persisted. Reusing the same Image/texture
        // instance per URL avoids creating/disposing native D3D textures on
        // every UI redraw (e.g. fast scrolling), which otherwise races the
        // JavaFX render thread and can crash the Prism D3D pipeline.
        .withCache("image-memory-cache", CacheConfigurationBuilder
            .newCacheConfigurationBuilder(String.class, Image.class,
                ResourcePoolsBuilder.newResourcePoolsBuilder().heap(100, EntryUnit.ENTRIES)))
        .build();
    cacheManager.init();

    return cacheManager;
  }

  /**
   * Called by JavaFx platform on closure request. Post execution cleaning
   * operation should be operated here.
   */
  @Override
  public void stop() {
    context.close();
  }

}
