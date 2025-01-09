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

package com.owlplug.core.controllers.dialogs;

import com.owlplug.controls.DialogLayout;
import com.owlplug.core.components.LazyViewRegistry;
import com.owlplug.core.model.Plugin;
import com.owlplug.core.model.serializer.PluginCSVSerializer;
import com.owlplug.core.model.serializer.PluginJsonSerializer;
import com.owlplug.core.services.PluginService;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ExportDialogController extends AbstractDialogController {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private LazyViewRegistry lazyViewRegistry;
  @Autowired
  private PluginService pluginService;

  @FXML
  private ComboBox<String> exportComboBox;
  @FXML
  private TextArea exportTextArea;
  @FXML
  private Button closeButton;
  @FXML
  private Button saveAsButton;

  ExportDialogController() {
    super(650, 500);
  }

  public void initialize() {
    closeButton.setOnAction(e -> {
      this.close();
    });

    exportComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
      refreshView();
    });

    saveAsButton.setOnAction(event -> {

      FileChooser fileChooser = new FileChooser();
      fileChooser.setTitle("Save");

      String exportType = exportComboBox.getSelectionModel().getSelectedItem();
      if ("CSV".equals(exportType)) {
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV File", ".csv"));
      } else if ("JSON".equals(exportType)) {
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Json File", ".json"));
      }
      fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("All Files", "*.*"));

      File file = fileChooser.showSaveDialog(saveAsButton.getScene().getWindow());
      if (file != null) {
        try (FileWriter writer = new FileWriter(file)) {
          writer.write(exportTextArea.getText());
          writer.flush();
        } catch (IOException e) {
          log.error("Error during writing on file {}", file.getAbsolutePath(), e);
        }
      }
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
    if ("CSV".equals(exportType)) {
      PluginCSVSerializer serializer = new PluginCSVSerializer();
      output.append(serializer.getHeader());
      output.append(serializer.serialize(plugins));
    } else if ("JSON".equals(exportType)) {
      PluginJsonSerializer serializer = new PluginJsonSerializer();
      output.append(serializer.serialize(plugins));
    }

    exportTextArea.setText(output.toString());
  }

  @Override
  protected DialogLayout getLayout() {

    DialogLayout layout = new DialogLayout();
    Label title = new Label("Export Plugins");
    title.getStyleClass().add("heading-3");
    layout.setHeading(title);

    layout.setBody(lazyViewRegistry.get(LazyViewRegistry.EXPORT_VIEW));

    return layout;
  }
}
