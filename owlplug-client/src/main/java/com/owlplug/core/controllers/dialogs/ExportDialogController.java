package com.owlplug.core.controllers.dialogs;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.owlplug.core.components.LazyViewRegistry;
import com.owlplug.core.model.Plugin;
import com.owlplug.core.model.serializer.PluginCSVSerializer;
import com.owlplug.core.model.serializer.PluginJsonSerializer;
import com.owlplug.core.services.PluginService;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ExportDialogController extends AbstractDialogController {

  @Autowired
  private LazyViewRegistry lazyViewRegistry;
  @Autowired
  private PluginService pluginService;

  @FXML
  private JFXComboBox<String> exportComboBox;
  @FXML
  private TextArea exportTextArea;
  @FXML
  private JFXButton closeButton;

  public void initialize() {
    closeButton.setOnAction(e -> {
      this.close();
    });

    exportComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
      refreshView();
    });
  }

  @Override
  public void onDialogShow() {
    refreshView();

  }

  private void refreshView() {
    Iterable<Plugin> plugins = pluginService.getAllPlugins();

    String exportType = exportComboBox.getSelectionModel().getSelectedItem();

    StringBuilder output = new StringBuilder();
    if("CSV".equals(exportType)) {
      output.append(PluginCSVSerializer.getHeader());
      output.append(PluginCSVSerializer.serialize(plugins));
    } else if ("JSON".equals(exportType)) {
      output.append(PluginJsonSerializer.serialize(plugins));
    }

    exportTextArea.setText(output.toString());
  }

  @Override
  protected Node getBody() {
    return lazyViewRegistry.get(LazyViewRegistry.EXPORT_VIEW);

  }

  @Override
  protected Node getHeading() {
    Label title = new Label("Export Plugins");
    title.getStyleClass().add("heading-3");
    return title;
  }
}
