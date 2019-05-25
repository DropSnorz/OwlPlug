package com.owlplug.core.controllers.dialogs;

import com.jfoenix.controls.JFXAutoCompletePopup;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.components.CoreTaskFactory;
import com.owlplug.core.components.LazyViewRegistry;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.prefs.Preferences;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
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
  @Autowired
  private Preferences preferences;

  @FXML
  private JFXTextField linkSourceParentTextField; 
  @FXML
  private JFXTextField linkSourceNameTextField;
  @FXML
  private JFXTextField linkTargetTextField;
  @FXML
  private JFXButton sourceDirectoryButton;
  @FXML
  private JFXButton targetDirectoryButton;
  @FXML
  private JFXButton createButton;
  @FXML
  private JFXButton cancelButton;
  @FXML
  private Label errorLabel;

  NewLinkController() {
    super(650, 250);
  }

  /**
   * FXML initialize.
   */
  public void initialize() {
    
    this.errorLabel.setVisible(false);

    cancelButton.setOnAction(e -> {
      this.close();
    });

    createButton.setOnAction(e -> {
      String sourcePath = Paths.get(linkSourceParentTextField.getText(), linkSourceNameTextField.getText()).toString();
      String targetPath = linkTargetTextField.getText();
      if (checkSymlinkCreation(sourcePath, targetPath)) {
        if (createSymlink(sourcePath, targetPath)) {
          coreTaskFactory.createPluginSyncTask(new File(sourcePath).getParent()).schedule();
          this.close();
        }
      }
    });
    
    sourceDirectoryButton.setOnAction(e -> {
      DirectoryChooser directoryChooser = new DirectoryChooser();
      Window mainWindow = sourceDirectoryButton.getScene().getWindow();
      File selectedDirectory = directoryChooser.showDialog(mainWindow);
      if (selectedDirectory != null) {
        linkSourceParentTextField.setText(selectedDirectory.getAbsolutePath());
      }
    });
    
    targetDirectoryButton.setOnAction(e -> {
      DirectoryChooser directoryChooser = new DirectoryChooser();
      Window mainWindow = sourceDirectoryButton.getScene().getWindow();
      File selectedDirectory = directoryChooser.showDialog(mainWindow);
      if (selectedDirectory != null) {
        linkTargetTextField.setText(selectedDirectory.getAbsolutePath());
      }
    });
    
    JFXAutoCompletePopup<String> autoComplete = new JFXAutoCompletePopup<>();
    if (preferences.getBoolean(ApplicationDefaults.VST3_DISCOVERY_ENABLED_KEY, false)) {
      autoComplete.getSuggestions().add(preferences.get(ApplicationDefaults.VST3_DIRECTORY_KEY,""));
    }
    if (preferences.getBoolean(ApplicationDefaults.VST2_DISCOVERY_ENABLED_KEY, false)) {
      autoComplete.getSuggestions().add(preferences.get(ApplicationDefaults.VST_DIRECTORY_KEY,""));
    }
    autoComplete.setSelectionHandler(e -> {
      linkSourceParentTextField.setText(e.getObject().toString());
    });
    
    linkSourceParentTextField.setOnKeyPressed(e -> {
      autoComplete.show(linkSourceParentTextField);
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
    if (Files.isSymbolicLink(Paths.get(sourcePath))) {
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
