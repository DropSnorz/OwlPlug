package com.dropsnorz.owlplug.core.services;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

  public void error(String title, String message) {
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(title);
    alert.setContentText(message);
    alert.showAndWait();
  }

  public void info(String title, String message) {
    Alert alert = new Alert(AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(title);
    alert.setContentText(message);
    alert.showAndWait();
  }

}
