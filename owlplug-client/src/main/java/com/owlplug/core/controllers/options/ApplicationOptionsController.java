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

package com.owlplug.core.controllers.options;

import com.owlplug.controls.Dialog;
import com.owlplug.controls.DialogLayout;
import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.controllers.BaseController;
import com.owlplug.core.controllers.dialogs.DonateDialogController;
import com.owlplug.core.services.OptionsService;
import com.owlplug.core.utils.PlatformUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ApplicationOptionsController extends BaseController {

  @Autowired
  private OptionsService optionsService;
  @Autowired
  private DonateDialogController donateDialogController;

  @FXML
  private CheckBox telemetryCheckBox;
  @FXML
  private Hyperlink telemetryHyperlink;
  @FXML
  private Button clearCacheButton;
  @FXML
  private Button removeDataButton;
  @FXML
  private Hyperlink websiteLink;
  @FXML
  private Button contributeButton;
  @FXML
  private Button openLogsButton;

  @FXML
  public void initialize() {
    telemetryCheckBox.selectedProperty().addListener((obs, o, n) ->
        getPreferences().putBoolean(ApplicationDefaults.TELEMETRY_ENABLED_KEY, n));

    telemetryHyperlink.setOnAction(e -> PlatformUtils.openDefaultBrowser(
        getApplicationDefaults().getEnvProperty("owlplug.github.wiki.url") + "/Telemetry"));

    clearCacheButton.setOnAction(e -> optionsService.clearCache());

    removeDataButton.setOnAction(e -> {
      Dialog dialog = getDialogManager().newDialog();
      DialogLayout layout = new DialogLayout();
      layout.setHeading(new Label("Remove User Data"));
      layout.setBody(new Label(
          "Do you really want to remove all user data including accounts, "
          + "stores and custom settings?\n\nYou must restart OwlPlug for a complete reset."));
      Button cancelButton = new Button("Cancel");
      cancelButton.setOnAction(ce -> dialog.close());
      Button confirmButton = new Button("Remove Data");
      confirmButton.getStyleClass().add("button-danger");
      confirmButton.setOnAction(ce -> {
        dialog.close();
        optionsService.clearAllUserData();
      });
      layout.setActions(confirmButton, cancelButton);
      dialog.setContent(layout);
      dialog.show();
    });

    websiteLink.setOnAction(e -> PlatformUtils.openDefaultBrowser(websiteLink.getText()));
    contributeButton.setOnAction(e -> donateDialogController.show());
    openLogsButton.setOnAction(e -> PlatformUtils.openFromDesktop(ApplicationDefaults.getLogDirectory()));
  }

  public void refresh() {
    telemetryCheckBox.setSelected(
        getPreferences().getBoolean(ApplicationDefaults.TELEMETRY_ENABLED_KEY, true));
  }

}