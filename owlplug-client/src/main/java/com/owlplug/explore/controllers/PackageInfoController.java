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

import com.owlplug.core.components.ImageCache;
import com.owlplug.core.controllers.BaseController;
import com.owlplug.plugin.model.PluginType;
import com.owlplug.core.ui.SideBar;
import com.owlplug.core.utils.PlatformUtils;
import com.owlplug.explore.model.PackageBundle;
import com.owlplug.explore.model.PackageTag;
import com.owlplug.explore.model.RemotePackage;
import com.owlplug.explore.ui.PackageSourceBadgeView;
import com.owlplug.explore.ui.PackageBundlesView;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
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
  private Pane screenshotBackgroundPane;
  @FXML
  private Button closeButton;
  @FXML
  private Label nameLabel;
  @FXML
  private Label remoteSourceLabel;
  @FXML
  private Button donateButton;
  @FXML
  private Button browsePageButton;
  @FXML
  private Button installButton;
  @FXML
  private Hyperlink creatorLink;
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

  private PackageBundlesView bundlesView;

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

    creatorLink.setOnAction(e -> {
      exploreController.addSearchChip(creatorLink.getText());
    });

    closeButton.setOnAction(e -> {
      sidebar.collapse();
    });

    bundlesView = new PackageBundlesView(this.getApplicationDefaults());
    bundlesContainer.getChildren().add(bundlesView);

  }

  public void setPackage(RemotePackage remotePackage) {
    configureHeader(remotePackage);
    configureBody(remotePackage);

  }

  private void configureHeader(RemotePackage remotePackage) {

    // Header badge configuration
    headerContainer.getChildren().clear();
    headerContainer.getChildren().add(new PackageSourceBadgeView(remotePackage.getRemoteSource(),
        this.getApplicationDefaults(), true));

    // Screenshot display
    Image screenshot = imageCache.get(remotePackage.getScreenshotUrl());
    if (screenshot != null) {
      BackgroundImage bgImg = new BackgroundImage(screenshot, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
          BackgroundPosition.CENTER,
          new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true));
      screenshotBackgroundPane.setBackground(new Background(bgImg));
    }
    screenshotBackgroundPane.setEffect(new InnerShadow(25, Color.BLACK));

    // Name and source display
    this.nameLabel.setText(remotePackage.getName());
    this.remoteSourceLabel.setText(remotePackage.getRemoteSource().getName());

    // Redirect button configuratio
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

    // Activate and configure install button
    this.installButton.setDisable(false);
    installButton.setOnAction(e -> {
      boolean installStarted = exploreController.installPackage(remotePackage);
      if (installStarted) {
        this.installButton.setDisable(true);
      }
    });
  }

  private void configureBody(RemotePackage remotePackage) {

    // General fields binding
    this.creatorLink.setText(remotePackage.getCreator());
    this.descriptionLabel.setText(remotePackage.getDescription());

    // License display
    if (remotePackage.getLicense() != null) {
      licenseLabel.setText(remotePackage.getLicense());
    } else {
      licenseLabel.setText("Unknown license");
    }

    // Version display
    if (remotePackage.getVersion() != null) {
      versionLabel.setVisible(true);
      versionLabel.setText(remotePackage.getVersion());
    } else {
      versionLabel.setVisible(false);
    }

    // Type display
    if (remotePackage.getType() == PluginType.INSTRUMENT) {
      this.typeLabel.setText("Instrument (VSTi)");
    } else if (remotePackage.getType() == PluginType.EFFECT) {
      this.typeLabel.setText("Effect (VST)");
    }

    // Tag display
    tagContainer.getChildren().clear();
    for (PackageTag tag : remotePackage.getTags()) {
      Node chip = new FakeChip(tag.getName());
      chip.getStyleClass().add("chip");
      chip.getStyleClass().add("fake-chip");
      chip.setOnMouseClicked(e -> {
        exploreController.addSearchChip(tag.getName());
      });
      tagContainer.getChildren().add(chip);
    }

    // Bundle list display
    bundlesView.clear();
    for (PackageBundle bundle : remotePackage.getBundles()) {
      bundlesView.addPackageBundle(bundle, e -> exploreController.installBundle(bundle));
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
