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

import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.Styles;
import com.owlplug.controls.Dialog;
import com.owlplug.controls.DialogLayout;
import com.owlplug.core.components.ApplicationDefaults.Prefs;
import com.owlplug.core.controllers.BaseController;
import com.owlplug.core.utils.FX;
import com.owlplug.explore.controllers.NewSourceDialogController;
import com.owlplug.explore.events.RemoteSourceUpdatedEvent;
import com.owlplug.explore.model.RemoteSource;
import com.owlplug.explore.services.ExploreService;
import com.owlplug.plugin.ui.PluginFormatBadgeView;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

@Controller
public class InstallationSettingsController extends BaseController {

  @Autowired
  private ExploreService exploreService;
  @Autowired
  private NewSourceDialogController newSourceDialogController;

  @FXML
  private ListView<RemoteSource> sourceListView;
  @FXML
  private Button addSourceButton;
  @FXML
  private CheckBox storeDirectoryCheckBox;
  @FXML
  private HBox storeDirectoryRow;
  @FXML
  private TextField storeDirectoryTextField;
  @FXML
  private CheckBox storeByCreatorCheckBox;
  @FXML
  private CheckBox storeSubDirectoryCheckBox;
  @FXML
  private Label warningSubDirectory;
  @FXML
  private VBox pathPreviewContainer;

  private final ObservableList<RemoteSource> sourceItems = FXCollections.observableArrayList();

  private Label vst2PathLabel;
  private Label vst3PathLabel;
  private Label auPathLabel;
  private Label lv2PathLabel;

  @FXML
  public void initialize() {
    Label sourcePlaceholder = new Label("No remote sources configured.");
    sourcePlaceholder.getStyleClass().add("label-disabled");
    sourceListView.setPlaceholder(sourcePlaceholder);
    sourceListView.setItems(sourceItems);
    sourceListView.setCellFactory(lv -> new ListCell<>() {
      @Override
      protected void updateItem(RemoteSource remoteSource, boolean empty) {
        super.updateItem(remoteSource, empty);
        setText(null);
        setGraphic(empty || remoteSource == null ? null : buildSourceRow(remoteSource));
      }
    });

    FontIcon addSourceIcon = new FontIcon("mdi2p-plus");
    addSourceIcon.setIconSize(14);
    addSourceButton.setGraphic(addSourceIcon);
    addSourceButton.setOnAction(e -> {
      newSourceDialogController.show();
      newSourceDialogController.startCreateSequence();
    });

    refreshSources();

    storeDirectoryRow.visibleProperty().bind(storeDirectoryCheckBox.selectedProperty());
    storeDirectoryRow.managedProperty().bind(storeDirectoryCheckBox.selectedProperty());
    warningSubDirectory.managedProperty().bind(warningSubDirectory.visibleProperty());

    storeDirectoryCheckBox.selectedProperty().addListener((obs, o, n) -> {
      getPreferences().putBoolean(Prefs.Explore.STORE_DIRECTORY_ENABLED, n);
      refreshPathPreview();
    });
    storeByCreatorCheckBox.selectedProperty().addListener((obs, o, n) -> {
      getPreferences().putBoolean(Prefs.Explore.STORE_BY_CREATOR_ENABLED, n);
      refreshPathPreview();
    });
    storeSubDirectoryCheckBox.selectedProperty().addListener((obs, o, n) -> {
      getPreferences().putBoolean(Prefs.Explore.STORE_SUBDIRECTORY_ENABLED, n);
      warningSubDirectory.setVisible(!n);
      refreshPathPreview();
    });
    storeDirectoryTextField.textProperty().addListener((obs, o, n) -> {
      getPreferences().put(Prefs.Explore.STORE_DIRECTORY, n);
      refreshPathPreview();
    });

    vst2PathLabel = previewLabel();
    vst3PathLabel = previewLabel();
    auPathLabel   = previewLabel();
    lv2PathLabel  = previewLabel();

    pathPreviewContainer.getChildren().addAll(
        previewRow("vst2", vst2PathLabel),
        previewRow("vst3", vst3PathLabel),
        previewRow("au",   auPathLabel),
        previewRow("lv2",  lv2PathLabel));
  }

  public void refresh() {
    boolean storeSubDir = getPreferences().getBoolean(Prefs.Explore.STORE_SUBDIRECTORY_ENABLED, true);
    storeSubDirectoryCheckBox.setSelected(storeSubDir);
    warningSubDirectory.setVisible(!storeSubDir);
    storeDirectoryCheckBox.setSelected(
        getPreferences().getBoolean(Prefs.Explore.STORE_DIRECTORY_ENABLED, false));
    storeByCreatorCheckBox.setSelected(
        getPreferences().getBoolean(Prefs.Explore.STORE_BY_CREATOR_ENABLED, false));
    storeDirectoryTextField.setText(
        getPreferences().get(Prefs.Explore.STORE_DIRECTORY, ""));
    refreshPathPreview();
    refreshSources();
  }

  private void refreshSources() {
    List<RemoteSource> sources = new ArrayList<>();
    exploreService.getRemoteSources().forEach(sources::add);
    sourceItems.setAll(sources);
  }

  private HBox buildSourceRow(RemoteSource remoteSource) {
    VBox infoBox = new VBox(2);
    Label nameLabel = new Label(remoteSource.getName());
    Label urlLabel = new Label(formatUrl(remoteSource.getDisplayUrl()));
    urlLabel.getStyleClass().add("label-disabled");
    infoBox.getChildren().addAll(nameLabel, urlLabel);
    HBox.setHgrow(infoBox, Priority.ALWAYS);

    ToggleSwitch enabledToggle = new ToggleSwitch();
    enabledToggle.setSelected(remoteSource.isEnabled());
    Tooltip.install(enabledToggle, new Tooltip("Enable or disable this source"));
    enabledToggle.selectedProperty().addListener((obs, old, selected) ->
        exploreService.enableSource(remoteSource, selected));

    Button deleteButton = new Button();
    deleteButton.getStyleClass().addAll(Styles.BUTTON_ICON, Styles.FLAT);
    deleteButton.setGraphic(new FontIcon("mdi2d-delete-outline"));
    Tooltip.install(deleteButton, new Tooltip("Remove source"));
    deleteButton.setOnAction(e -> confirmAndDeleteSource(remoteSource));

    HBox row = new HBox(12, infoBox, enabledToggle, deleteButton);
    row.setAlignment(Pos.CENTER_LEFT);
    row.setPadding(new Insets(4));
    return row;
  }

  private void confirmAndDeleteSource(RemoteSource remoteSource) {
    Dialog dialog = this.getDialogManager().newDialog();
    DialogLayout layout = new DialogLayout();
    layout.setHeading(new Label("Remove source"));
    layout.setBody(new Label("Do you really want to remove \"" + remoteSource.getName()
        + "\" ? Packages from this source will no longer be available in Explore."));

    Button cancelButton = new Button("Cancel");
    cancelButton.setOnAction(e -> dialog.close());

    Button removeButton = new Button("Remove");
    removeButton.getStyleClass().add("button-danger");
    removeButton.setOnAction(e -> {
      dialog.close();
      exploreService.delete(remoteSource);
      refreshSources();
    });

    layout.setActions(removeButton, cancelButton);
    dialog.setContent(layout);
    dialog.show();
  }

  private String formatUrl(String url) {
    if (url == null) {
      return null;
    }
    return url.replace("http://", "").replace("https://", "");
  }

  @EventListener
  private void handle(RemoteSourceUpdatedEvent event) {
    FX.run(this::refreshSources);
  }

  private void refreshPathPreview() {
    vst2PathLabel.setText(simulatePath(Prefs.Plugins.VST2_DIRECTORY));
    vst3PathLabel.setText(simulatePath(Prefs.Plugins.VST3_DIRECTORY));
    auPathLabel.setText(simulatePath(Prefs.Plugins.AU_DIRECTORY));
    lv2PathLabel.setText(simulatePath(Prefs.Plugins.LV2_DIRECTORY));
  }

  private String simulatePath(String formatDirKey) {
    String baseDir = getPreferences().get(formatDirKey, "");
    if (baseDir == null || baseDir.isBlank()) return "Format directory not configured";

    File path = new File(baseDir);
    if (storeDirectoryCheckBox.isSelected()) {
      String sub = storeDirectoryTextField.getText();
      if (sub != null && !sub.isBlank()) path = new File(path, sub);
    }
    if (storeByCreatorCheckBox.isSelected()) path = new File(path, "Acme Audio");
    if (storeSubDirectoryCheckBox.isSelected()) path = new File(path, "MyPlugin");
    return path.getAbsolutePath();
  }

  private Label previewLabel() {
    Label l = new Label();
    l.getStyleClass().add("label-disabled");
    l.setWrapText(true);
    HBox.setHgrow(l, Priority.ALWAYS);
    return l;
  }

  private HBox previewRow(String formatValue, Label pathLabel) {
    PluginFormatBadgeView badge = new PluginFormatBadgeView(
        formatValue, getApplicationDefaults(), PluginFormatBadgeView.DisplayMode.TEXT_ONLY);
    badge.setMinWidth(42);
    HBox row = new HBox(12, badge, pathLabel);
    row.setAlignment(Pos.CENTER_LEFT);
    return row;
  }

}