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

import com.jfoenix.assets.JFoenixResources;
import com.owlplug.core.components.ApplicationDefaults;
import javafx.application.Preloader;
import javafx.application.Preloader.StateChangeNotification.Type;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

public class OwlPlugPreloader extends Preloader {

  private Stage preloaderStage;

  @Override
  public void start(Stage primaryStage) throws Exception {
    this.preloaderStage = primaryStage;

    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Preloader.fxml"));
    Parent root = loader.load();

    Scene scene = new Scene(root);
    JMetro jMetro = new JMetro(Style.DARK);
    jMetro.setScene(scene);

    String fontsCss = JFoenixResources.load("css/jfoenix-fonts.css").toExternalForm();
    scene.getStylesheets().add(fontsCss);
    String owlplugCss = OwlPlugPreloader.class.getResource("/owlplug.css").toExternalForm();
    scene.getStylesheets().add(owlplugCss);

    primaryStage.getIcons().add(ApplicationDefaults.owlplugLogo);
    primaryStage.setTitle(ApplicationDefaults.APPLICATION_NAME);

    primaryStage.initStyle(StageStyle.UNDECORATED);
    primaryStage.setWidth(600);
    primaryStage.setHeight(300);
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

      if (ppm.getType().equals("error")) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error");
        alert.setContentText(ppm.getMessage());
        alert.showAndWait();
      }
    }
  }
}
