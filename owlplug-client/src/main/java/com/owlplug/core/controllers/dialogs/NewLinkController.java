package com.owlplug.core.controllers.dialogs;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.owlplug.core.components.CoreTaskFactory;
import com.owlplug.core.components.LazyViewRegistry;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class NewLinkController extends AbstractDialogController {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private LazyViewRegistry lazyViewRegistry;
  
  @Autowired
  private CoreTaskFactory coreTaskFactory;

  @FXML
  private JFXTextField linkSourceTextField;
  @FXML
  private JFXTextField linkTargetTextField;
  @FXML
  private JFXButton createButton;
  @FXML
  private JFXButton cancelButton;

  NewLinkController() {
    super(650, 250);
  }

  /**
   * FXML initialize.
   */
  public void initialize() {

    cancelButton.setOnAction(e -> {
      this.close();
    });

    createButton.setOnAction(e -> {
      String sourcePath = linkSourceTextField.getText();
      String targetPath = linkTargetTextField.getText();
      if (checkSymlinkCreation(sourcePath, targetPath)) {
        if (createSymlink(sourcePath, targetPath)) {
          coreTaskFactory.createPluginSyncTask(new File(sourcePath).getParent()).schedule();
          this.close();
        }
      }
    });

  }

  
  private boolean createSymlink(String sourcePath, String targetPath) {
    Path link = Paths.get(sourcePath);
    Path target = Paths.get(targetPath);
    try {
      Files.createSymbolicLink(link, target);
      return true;
    } catch (IOException e) {
      log.error("Error creating Symlink", e);
      return false;
    }
  }

  private boolean checkSymlinkCreation(String sourcePath, String targetPath) {
    
    File sourceFile = new File(sourcePath);
    File targetFile = new File(targetPath);
    if (sourceFile.exists()) {
      return false;
    }
    if (targetFile.exists() && targetFile.isDirectory()) {
      return true;
    }
    return false;
  }

  
  @Override
  protected Node getBody() {
    return lazyViewRegistry.get(LazyViewRegistry.NEW_LINK_VIEW);
  }

  @Override
  protected Node getHeading() {
    Label title = new Label("Create a new Link");
    title.getStyleClass().add("heading-3");
    return title;
  }

}
