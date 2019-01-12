package com.owlplug.core.controllers.menus;

import com.owlplug.core.controllers.MainController;
import com.owlplug.core.controllers.dialogs.FileSystemRepositoryController;
import com.owlplug.core.controllers.dialogs.GoogleDriveRepositoryController;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class NewRepositoryController {

  @Autowired
  private MainController mainController;
  @Autowired
  private FileSystemRepositoryController fileSystemRepositoryController;
  @Autowired
  private GoogleDriveRepositoryController googleDriveRepositoryController;

  @FXML
  private HBox fileSystemMenuItem;
  @FXML
  private HBox googleDriveMenuItem;

  /**
   * FXML initialize method.
   */
  public void initialize() {

    fileSystemMenuItem.setOnMouseClicked(e -> {
      fileSystemRepositoryController.show();
      fileSystemRepositoryController.startCreateSequence();
      mainController.getLeftDrawer().close();
    });

    googleDriveMenuItem.setOnMouseClicked(e -> {
      googleDriveRepositoryController.show();
      googleDriveRepositoryController.startCreateSequence();
      mainController.getLeftDrawer().close();
    });
  }

}
