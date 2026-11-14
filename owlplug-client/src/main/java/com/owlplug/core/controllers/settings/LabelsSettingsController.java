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

package com.owlplug.core.controllers.settings;

import com.owlplug.core.components.ApplicationDefaults.Prefs;
import com.owlplug.core.controllers.BaseController;
import com.owlplug.core.events.PreferencesChangedEvent;
import com.owlplug.core.model.Label;
import com.owlplug.core.services.LabelService;
import com.owlplug.core.ui.LabelBadgeView;
import com.owlplug.core.utils.ColorUtils;
import com.owlplug.plugin.model.PluginFootprint;
import com.owlplug.project.model.ProjectFootprint;
import java.util.List;
import java.util.function.Function;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;

@Controller
public class LabelsSettingsController extends BaseController {

  private static final int MAX_NAME_LENGTH = 40;

  @Autowired
  private LabelService labelService;
  @Autowired
  private ApplicationEventPublisher publisher;

  @FXML
  private VBox labelsContainer;
  @FXML
  private Button addLabelButton;
  @FXML
  private CheckBox showInListsCheckbox;
  @FXML
  private ListView<PluginFootprint> orphanPluginsListView;
  @FXML
  private ListView<ProjectFootprint> orphanProjectsListView;
  @FXML
  private Button deleteSelectedPluginsButton;
  @FXML
  private Button deleteAllPluginsButton;
  @FXML
  private Button deleteSelectedProjectsButton;
  @FXML
  private Button deleteAllProjectsButton;

  @FXML
  public void initialize() {
    addLabelButton.setGraphic(new FontIcon("mdi2p-plus"));
    addLabelButton.setOnAction(e -> addLabel());

    showInListsCheckbox.selectedProperty().addListener((obs, o, n) -> {
      if (getPreferences().getBoolean(Prefs.Labels.SHOW_IN_LISTS, true) != n) {
        getPreferences().putBoolean(Prefs.Labels.SHOW_IN_LISTS, n);
        publisher.publishEvent(new PreferencesChangedEvent());
      }
    });

    setupOrphanList(orphanPluginsListView, PluginFootprint::getPath, PluginFootprint::getLabels);
    setupOrphanList(orphanProjectsListView, ProjectFootprint::getPath, ProjectFootprint::getLabels);

    deleteSelectedPluginsButton.setOnAction(e -> deleteOrphanPlugins(
        List.copyOf(orphanPluginsListView.getSelectionModel().getSelectedItems())));
    deleteAllPluginsButton.setOnAction(e -> deleteOrphanPlugins(
        List.copyOf(orphanPluginsListView.getItems())));
    deleteSelectedProjectsButton.setOnAction(e -> deleteOrphanProjects(
        List.copyOf(orphanProjectsListView.getSelectionModel().getSelectedItems())));
    deleteAllProjectsButton.setOnAction(e -> deleteOrphanProjects(
        List.copyOf(orphanProjectsListView.getItems())));
  }

  public void refresh() {
    showInListsCheckbox.setSelected(getPreferences().getBoolean(Prefs.Labels.SHOW_IN_LISTS, true));
    refreshLabels();
    refreshOrphans();
  }

  private void refreshLabels() {
    labelsContainer.getChildren().setAll(
        labelService.findAll().stream().map(this::buildLabelRow).toList());
  }

  private void refreshOrphans() {
    orphanPluginsListView.getItems().setAll(labelService.findOrphanPluginFootprints());
    orphanProjectsListView.getItems().setAll(labelService.findOrphanProjectFootprints());
  }

  private HBox buildLabelRow(Label label) {
    ColorPicker colorPicker = new ColorPicker(ColorUtils.parse(label.getColor()));
    colorPicker.getCustomColors().setAll(
        LabelService.PALETTE.stream().map(Color::web).toList());
    colorPicker.setOnAction(e -> {
      label.setColor(ColorUtils.toHex(colorPicker.getValue()));
      labelService.save(label);
    });

    TextField nameField = new TextField(label.getName());
    nameField.setPrefWidth(240);
    Runnable commitName = () -> {
      String name = nameField.getText() == null ? "" : nameField.getText().trim();
      if (name.equals(label.getName())) {
        return;
      }
      boolean valid = !name.isEmpty() && name.length() <= MAX_NAME_LENGTH && !labelService.nameExists(name);
      if (valid) {
        label.setName(name);
        labelService.save(label);
      } else {
        nameField.setText(label.getName());
      }
    };
    nameField.setOnAction(e -> commitName.run());
    nameField.focusedProperty().addListener((obs, o, focused) -> {
      if (!focused) {
        commitName.run();
      }
    });

    Button deleteButton = new Button(null, new FontIcon("mdi2d-delete-outline"));    deleteButton.setOnAction(e -> {
      labelService.delete(label);
      refreshLabels();
    });

    // Live preview, updated while typing or picking a color (before the change is saved)
    HBox preview = new HBox();
    preview.setAlignment(Pos.CENTER_LEFT);
    preview.setPadding(new Insets(0, 0, 0, 8));
    Runnable updatePreview = () -> {
      String text = nameField.getText() == null || nameField.getText().isBlank()
          ? label.getName() : nameField.getText().trim();
      preview.getChildren().setAll(new LabelBadgeView(new Label(text, ColorUtils.toHex(colorPicker.getValue()))));
    };
    nameField.textProperty().addListener((obs, o, n) -> updatePreview.run());
    colorPicker.valueProperty().addListener((obs, o, n) -> updatePreview.run());
    updatePreview.run();

    // Actions stay next to the editable fields, the preview comes last
    HBox row = new HBox(8, colorPicker, nameField, deleteButton, preview);
    row.setAlignment(Pos.CENTER_LEFT);
    return row;
  }

  private void addLabel() {
    String name = "New label";
    int i = 2;
    while (labelService.nameExists(name)) {
      name = "New label " + i++;
    }
    int colorIndex = labelsContainer.getChildren().size() % LabelService.PALETTE.size();
    labelService.save(new Label(name, LabelService.PALETTE.get(colorIndex)));
    refreshLabels();
  }

  private <T> void setupOrphanList(ListView<T> listView, Function<T, String> pathGetter,
                                   Function<T, java.util.Set<Label>> labelsGetter) {
    listView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    listView.setCellFactory(lv -> new ListCell<>() {
      @Override
      protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
          setText(null);
          setGraphic(null);
          return;
        }
        javafx.scene.control.Label path = new javafx.scene.control.Label(pathGetter.apply(item));
        HBox.setHgrow(path, Priority.ALWAYS);
        HBox box = new HBox(8, path);
        box.setAlignment(Pos.CENTER_LEFT);
        box.getChildren().addAll(LabelBadgeView.of(labelsGetter.apply(item)));
        setGraphic(box);
      }
    });
    javafx.scene.control.Label placeholder = new javafx.scene.control.Label("No orphaned data.");
    placeholder.getStyleClass().add("label-disabled");
    listView.setPlaceholder(placeholder);
  }

  private void deleteOrphanPlugins(List<PluginFootprint> footprints) {
    labelService.deletePluginFootprints(footprints);
    refreshOrphans();
  }

  private void deleteOrphanProjects(List<ProjectFootprint> footprints) {
    labelService.deleteProjectFootprints(footprints);
    refreshOrphans();
  }

}
