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
import com.owlplug.core.utils.PlatformUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class DonateDialogController extends AbstractDialogController {

  @Autowired
  private LazyViewRegistry lazyViewRegistry;
  @FXML
  private Button donateButton;
  @FXML
  private Button roadmapButton;
  @FXML
  private Button featureRequestButton;
  @FXML
  private Button aboutButton;
  @FXML
  private Button cancelButton;

  DonateDialogController() {
    super(550, 480);
    this.setOverlayClose(false);
  }

  /**
   * FXML initialize.
   */
  public void initialize() {

    donateButton.setOnAction(e -> {
      PlatformUtils.openDefaultBrowser(this.getApplicationDefaults().getEnvProperty("owlplug.donate.url"));
      this.close();
      this.getDialogManager()
              .newSimpleInfoDialog("Thank  you !", "Thank you so much for contributing to OwlPlug development.\nYour donation will help me to release new versions, stay tuned !")
              .show();
    });

    roadmapButton.setOnAction(e -> {
      PlatformUtils.openDefaultBrowser(this.getApplicationDefaults().getEnvProperty("owlplug.roadmap.url"));
    });

    featureRequestButton.setOnAction(e -> {
      PlatformUtils.openDefaultBrowser(this.getApplicationDefaults().getEnvProperty("owlplug.github.issues.url"));
    });

    aboutButton.setOnAction(e -> {
      PlatformUtils.openDefaultBrowser(this.getApplicationDefaults().getEnvProperty("owlplug.about.url"));
    });

    cancelButton.setOnAction(e -> {
      this.close();
    });

  }

  @Override
  protected DialogLayout getLayout() {
    DialogLayout dialogLayout = new DialogLayout();

    Label title = new Label("Owlplug is free !");
    title.getStyleClass().add("heading-3");

    dialogLayout.setHeading(title);

    dialogLayout.setBody(lazyViewRegistry.get(LazyViewRegistry.DONATE_VIEW));

    return dialogLayout;
  }

}
