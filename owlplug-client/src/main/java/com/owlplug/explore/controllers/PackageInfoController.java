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
 
package com.owlplug.explore.controllers;

import com.jfoenix.controls.JFXButton;
import com.owlplug.core.components.ImageCache;
import com.owlplug.core.controllers.BaseController;
import com.owlplug.core.model.PluginType;
import com.owlplug.core.ui.SideBar;
import com.owlplug.core.utils.PlatformUtils;
import com.owlplug.explore.model.PackageBundle;
import com.owlplug.explore.model.PackageTag;
import com.owlplug.explore.model.RemotePackage;
import com.owlplug.explore.ui.PackageSourceBadgeView;
import com.owlplug.explore.ui.ProductBundlesView;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class PackageInfoController extends BaseController {

  @Autowired
  private ExploreController exploreController;
  @Autowired
  private ImageCache imageCache;

  @FXML
  private Pane packageInfoContainer;
  @FXML
  private Pane packageInfoContent;
  @FXML
  private Pane topInfoPane;
  @FXML
  private JFXButton closeButton;
  @FXML
  private Label nameLabel;
  @FXML
  private Label remoteSourceLabel;
  @FXML
  private JFXButton donateButton;
  @FXML
  private JFXButton browsePageButton;
  @FXML
  private JFXButton installButton;
  @FXML
  private Label creatorLabel;
  @FXML
  private Label licenseLabel;
  @FXML
  private Label versionLabel;
  @FXML
  private Label typeLabel;
  @FXML
  private FlowPane tagContainer;
  @FXML
  private Label descriptionLabel;
  @FXML
  private Pane bundlesContainer;
  @FXML
  private Pane headerContainer;

  private ProductBundlesView bundlesView;

  private SideBar sidebar;

  /**
   * FXML initialize.
   */
  public void initialize() {

    // Wrap info content inside a proxy sidebar
    packageInfoContainer.getChildren().remove(packageInfoContent);
    sidebar = new SideBar(400, packageInfoContent);
    sidebar.collapse();
    packageInfoContainer.getChildren().add(sidebar);

    closeButton.setOnAction(e -> {
      sidebar.collapse();
    });

    bundlesView = new ProductBundlesView();
    bundlesContainer.getChildren().add(bundlesView);

  }

  public void setPackage(RemotePackage remotePackage) {

    // Active install buttons
    this.installButton.setDisable(false);

    headerContainer.getChildren().clear();
    headerContainer.getChildren().add(new PackageSourceBadgeView(remotePackage.getRemoteSource(),
      this.getApplicationDefaults()));

    // Bind remotePackage properties to controls
    this.nameLabel.setText(remotePackage.getName());
    Image screenshot = imageCache.get(remotePackage.getScreenshotUrl());

    if (screenshot != null) {
      BackgroundImage bgImg = new BackgroundImage(screenshot, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
          BackgroundPosition.CENTER,
          new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true));
      topInfoPane.setBackground(new Background(bgImg));
    }
    topInfoPane.setEffect(new InnerShadow(25, Color.BLACK));

    this.remoteSourceLabel.setText(remotePackage.getRemoteSource().getName());

    browsePageButton.setOnAction(e -> {
      PlatformUtils.openDefaultBrowser(remotePackage.getPageUrl());
    });
    if (remotePackage.getDonateUrl() != null) {
      donateButton.setVisible(true);
      donateButton.setOnAction(e -> {
        PlatformUtils.openDefaultBrowser(remotePackage.getDonateUrl());
      });
    } else {
      donateButton.setVisible(false);
    }
    installButton.setOnAction(e -> {
      boolean installStarted = exploreController.installProduct(remotePackage);
      if (installStarted) {
        this.installButton.setDisable(true);
      }
    });

    this.creatorLabel.setText(remotePackage.getCreator());

    if(remotePackage.getLicense() != null ) {
      licenseLabel.setText(remotePackage.getLicense());
    } else {
      licenseLabel.setText("Unknown license");
    }
    if (remotePackage.getVersion() != null) {
      versionLabel.setVisible(true);
      versionLabel.setText(remotePackage.getVersion());
    } else {
      versionLabel.setVisible(false);
    }

    if (remotePackage.getType() == PluginType.INSTRUMENT) {
      this.typeLabel.setText("Instrument (VSTi)");
    } else if (remotePackage.getType() == PluginType.EFFECT) {
      this.typeLabel.setText("Effect (VST)");
    }

    tagContainer.getChildren().clear();
    for (PackageTag tag : remotePackage.getTags()) {
      Node chip = new FakeChip(tag.getName());
      chip.getStyleClass().add("jfx-chip");
      tagContainer.getChildren().add(chip);

    }

    this.descriptionLabel.setText(remotePackage.getDescription());

    bundlesView.clear();
    for (PackageBundle bundle : remotePackage.getBundles()) {
      bundlesView.addProductBundle(bundle, e -> exploreController.installBundle(bundle));
    }

  }

  public void show() {
    if (sidebar.isCollapsed()) {
      sidebar.expand();
    }
  }

  public void hide() {
    sidebar.collapse();
  }

  public void toggleVisibility() {
    sidebar.toggle();
  }

  private class FakeChip extends HBox {

    public FakeChip(String text) {

      Label label = new Label(text);
      label.setWrapText(true);
      label.setMaxWidth(100);
      getChildren().add(label);
      this.getStyleClass().add("chip-label");
    }

  }

}
