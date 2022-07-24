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

import com.owlplug.core.components.ImageCache;
import com.owlplug.core.model.Plugin;
import com.owlplug.core.model.PluginComponent;
import com.owlplug.core.services.PluginService;
import com.owlplug.core.ui.PluginStateView;
import java.util.ArrayList;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ComponentInfoController extends BaseController {

  @Autowired
  private PluginService pluginService;
  @Autowired
  private ImageCache imageCache;

  @FXML
  private Pane pluginScreenshotPane;
  @FXML
  private ImageView pluginFormatIcon;
  @FXML
  private Label pluginFormatLabel;
  @FXML
  private Label pluginTitleLabel;
  @FXML
  private Label pluginNameLabel;
  @FXML
  private Label pluginVersionLabel;
  @FXML
  private Label pluginIdentifierLabel;
  @FXML
  private Label pluginManufacturerLabel;
  @FXML
  private Label pluginCategoryLabel;
  @FXML
  private PluginStateView pluginStateView;
  @FXML
  private Label pluginReferenceLabel;
  private PluginComponent currentComponent = null;
  private ArrayList<String> knownPluginImages = new ArrayList<>();

  /**
   * FXML initialize method.
   */
  @FXML
  public void initialize() {
    pluginScreenshotPane.setEffect(new ColorAdjust(0, 0, -0.6, 0));

  }

  public void setComponent(PluginComponent component) {
    this.currentComponent = component;
    pluginFormatIcon.setImage(this.getApplicationDefaults().getPluginFormatIcon(component.getPlugin()));
    pluginFormatLabel.setText(component.getPlugin().getFormat().getText() + " Plugin Component");
    pluginTitleLabel.setText(component.getName());
    pluginNameLabel.setText(Optional.ofNullable(component.getDescriptiveName()).orElse(component.getName()));
    pluginVersionLabel.setText(Optional.ofNullable(component.getVersion()).orElse("Unknown"));
    pluginManufacturerLabel.setText(Optional.ofNullable(component.getManufacturerName()).orElse("Unknown"));
    pluginIdentifierLabel.setText(Optional.ofNullable(component.getUid()).orElse("Unknown"));
    pluginCategoryLabel.setText(Optional.ofNullable(component.getCategory()).orElse("Unknown"));
    pluginStateView.setPluginState(pluginService.getPluginState(component.getPlugin()));
    pluginReferenceLabel.setText(component.getIdentifier());

    setPluginImage();
  }

  private void setPluginImage() {
    Plugin currentPlugin = currentComponent.getPlugin();
    if (currentPlugin.getScreenshotUrl() == null || currentPlugin.getScreenshotUrl().isEmpty()) {
      String url = pluginService.resolveImageUrl(currentPlugin);
      currentPlugin.setScreenshotUrl(url);
      pluginService.save(currentPlugin);
    }

    String url = currentPlugin.getScreenshotUrl();
    if (knownPluginImages.contains(url) && !imageCache.contains(url)) {

      BackgroundImage bgImg = new BackgroundImage(this.getApplicationDefaults().pluginPlaceholderImage,
          BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
            new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true));
      pluginScreenshotPane.setBackground(new Background(bgImg));

    } else {

      this.knownPluginImages.add(url);
      Image screenshot = imageCache.get(url);
      if (screenshot != null) {
        BackgroundImage bgImg = new BackgroundImage(screenshot, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
            BackgroundPosition.CENTER,
              new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true));
        pluginScreenshotPane.setBackground(new Background(bgImg));
      }

    }
  }

}
