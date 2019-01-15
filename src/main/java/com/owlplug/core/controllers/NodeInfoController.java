package com.owlplug.core.controllers;

import com.owlplug.core.model.Plugin;
import com.owlplug.core.model.PluginDirectory;
import com.owlplug.core.model.PluginRepository;
import javafx.fxml.FXML;
import javafx.scene.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class NodeInfoController {

  @Autowired
  private PluginInfoController pluginInfoController;
  @Autowired
  private DirectoryInfoController directoryInfoController;
  @Autowired
  private RepositoryInfoController repositoryInfoController;

  @FXML
  private Node pluginInfoView;
  @FXML
  private Node directoryInfoView;
  @FXML
  private Node repositoryInfoView;

  /**
   * FXML initialize.
   */
  public void initialize() {

    pluginInfoView.setVisible(false);
    directoryInfoView.setVisible(false);
    repositoryInfoView.setVisible(false);
  }

  public void setNode(Object node) {

    pluginInfoView.setVisible(false);
    directoryInfoView.setVisible(false);
    repositoryInfoView.setVisible(false);

    if (node instanceof Plugin) {
      pluginInfoController.setPlugin((Plugin) node);
      pluginInfoView.setVisible(true);
    }
    if (node instanceof PluginDirectory) {
      directoryInfoController.setPluginDirectory((PluginDirectory) node);
      directoryInfoView.setVisible(true);
    }
    if (node instanceof PluginRepository) {

      repositoryInfoController.setPluginRepository((PluginRepository) node);
      repositoryInfoView.setVisible(true);
    }
  }

}
