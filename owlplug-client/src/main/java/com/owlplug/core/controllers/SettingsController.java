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

package com.owlplug.core.controllers;

import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.controllers.settings.ApplicationSettingsController;
import com.owlplug.core.controllers.settings.InstallationSettingsController;
import com.owlplug.core.controllers.settings.PluginScanSettingsController;
import com.owlplug.core.controllers.settings.ProjectSettingsController;
import com.owlplug.core.events.PreferencesChangedEvent;
import com.owlplug.core.ui.SlidingLabel;
import com.owlplug.core.utils.FX;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

@Controller
public class SettingsController extends BaseController {

  @FXML
  private VBox navContainer;
  @FXML
  private StackPane contentArea;
  @FXML
  private Label versionLabel;
  @FXML
  private TextFlow contributorsFlow;

  // Section root nodes injected via fx:include fx:id convention
  @FXML
  private ScrollPane pluginScanSettings;
  @FXML
  private ScrollPane installationSettings;
  @FXML
  private ScrollPane projectSettings;
  @FXML
  private ScrollPane applicationSettings;

  // Section controllers injected via {fx:id}Controller convention
  @FXML
  private PluginScanSettingsController pluginScanSettingsController;
  @FXML
  private InstallationSettingsController installationSettingsController;
  @FXML
  private ProjectSettingsController projectSettingsController;
  @FXML
  private ApplicationSettingsController applicationSettingsController;

  private final ToggleGroup navToggleGroup = new ToggleGroup();

  public static int SCAN_SECTION_INDEX = 0;
  public static int INSTALLATION_SECTION_INDEX = 1;
  public static int PROJECTS_SECTION_INDEX = 2;
  public static int APPLICATION_SECTION_INDEX = 3;

  @FXML
  public void initialize() {
    hideSection(pluginScanSettings);
    hideSection(installationSettings);
    hideSection(projectSettings);
    hideSection(applicationSettings);

    addNavButton("Plugin Scan",  "mdi2m-magnify", pluginScanSettings);
    addNavButton("Installation", "mdi2d-download-outline", installationSettings);
    addNavButton("Projects",     "mdi2f-folder-music-outline", projectSettings);
    addNavButton("Application",  "mdi2c-cog-outline", applicationSettings);

    // Prevent deselecting all buttons
    navToggleGroup.selectedToggleProperty().addListener((obs, prev, next) -> {
      if (next == null && prev != null) {
        prev.setSelected(true);
      }
    });

    navToggleGroup.getToggles().get(0).setSelected(true);

    versionLabel.setText("OwlPlug " + getApplicationDefaults().getVersion());
    contributorsFlow.getChildren().add(new SlidingLabel(ApplicationDefaults.getContributors()));

    refreshView();
  }

  private void addNavButton(String label, String iconCode, Node section) {
    FontIcon icon = new FontIcon(iconCode);
    icon.setIconSize(15);

    ToggleButton btn = new ToggleButton(label, icon);
    btn.setToggleGroup(navToggleGroup);
    btn.setMaxWidth(Double.MAX_VALUE);
    btn.setAlignment(Pos.CENTER_LEFT);
    btn.getStyleClass().add("settings-nav-button");
    btn.setGraphicTextGap(10);

    btn.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
      if (isSelected) {
        showSection(section);
      } else {
        hideSection(section);
      }
    });

    navContainer.getChildren().add(btn);
  }

  private void showSection(Node section) {
    section.setVisible(true);
    section.setManaged(true);
  }

  private void hideSection(Node section) {
    section.setVisible(false);
    section.setManaged(false);
  }

  public void navigateToSection(int index) {
    navToggleGroup.getToggles().get(index).setSelected(true);
  }

  public void refreshView() {
    pluginScanSettingsController.refresh();
    installationSettingsController.refresh();
    projectSettingsController.refresh();
    applicationSettingsController.refresh();
  }

  @EventListener
  private void handle(PreferencesChangedEvent event) {
    FX.run(this::refreshView);
  }

}