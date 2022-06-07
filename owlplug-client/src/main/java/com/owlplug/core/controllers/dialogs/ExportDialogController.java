package com.owlplug.core.controllers.dialogs;

import com.jfoenix.controls.JFXButton;
import com.owlplug.core.components.LazyViewRegistry;
import com.owlplug.core.model.Plugin;
import com.owlplug.core.model.serializer.PluginCSVSerializer;
import com.owlplug.core.services.PluginService;
import java.io.OutputStream;
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
  private TextArea exportTextArea;
  @FXML
  private JFXButton closeButton;

  public void initialize() {
    closeButton.setOnAction(e -> {
      this.close();
    });
  }

  @Override
  public void onDialogShow() {
    exportTextArea.setText(generateExport());

  }

  private String generateExport() {
    Iterable<Plugin> plugins = pluginService.getAllPlugins();

    StringBuilder output = new StringBuilder(PluginCSVSerializer.getHeader());
    output.append(PluginCSVSerializer.serialize(plugins));

    return output.toString();
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
