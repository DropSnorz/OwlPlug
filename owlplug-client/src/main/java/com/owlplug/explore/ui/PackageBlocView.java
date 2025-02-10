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
 
package com.owlplug.explore.ui;

import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.model.PluginStage;
import com.owlplug.core.utils.PlatformUtils;
import com.owlplug.explore.controllers.ExploreController;
import com.owlplug.explore.model.PackageBundle;
import com.owlplug.explore.model.RemotePackage;
import java.util.Random;
import javafx.geometry.Dimension2D;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class PackageBlocView extends AnchorPane {

  private ExploreController parentController;

  /**
   * Creates a new package bloc view instance.
   * 
   * @param applicationDefaults - OwlPlug application defaults
   * @param remotePackage        - related package
   * @param image               - package image
   * @param parentController    - parent explore controller
   */
  public PackageBlocView(ApplicationDefaults applicationDefaults, RemotePackage remotePackage, Image image,
                         ExploreController parentController) {
    super();
    this.parentController = parentController;

    BorderPane content = new BorderPane();
    this.getChildren().add(content);
    AnchorPane.setBottomAnchor(content, 0.0);
    AnchorPane.setTopAnchor(content, 0.0);
    AnchorPane.setLeftAnchor(content, 0.0);
    AnchorPane.setRightAnchor(content, 0.0);

    // Header section
    HBox header = new HBox();
    Pane growingPane = new Pane();
    HBox.setHgrow(growingPane, Priority.ALWAYS);
    header.getChildren().add(growingPane);
    Node headerContent = new PackageSourceBadgeView(remotePackage.getRemoteSource(), applicationDefaults);
    header.getChildren().add(headerContent);

    content.setTop(header);

    // Footer section
    HBox footer = new HBox();
    footer.setSpacing(5);
    footer.getStyleClass().add("package-bloc-title");
    if (remotePackage.getType() != null) {
      Image typeIcon = applicationDefaults.getPackageTypeIcon(remotePackage);
      if (typeIcon != null) {
        ImageView typeImageView = new ImageView(typeIcon);
        typeImageView.setFitHeight(16);
        typeImageView.setFitWidth(16);
        footer.getChildren().add(typeImageView);
      }
    }
    footer.getChildren().add(new Label(remotePackage.getName()));
    footer.setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
    footer.setAlignment(Pos.BOTTOM_LEFT);
    content.setBottom(footer);

    if (remotePackage.getStage() != null && remotePackage.getStage() != PluginStage.RELEASE) {
      this.getChildren().add(createPluginStageFlag(remotePackage));
    }

    if (image != null) {
      BackgroundImage bgImg = new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
          BackgroundPosition.CENTER,
          new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true));

      this.setBackground(new Background(bgImg));
    }

    this.setEffect(new InnerShadow(11, Color.BLACK));

    TextFlow textFlow = new TextFlow();
    textFlow.getChildren().add(new Label("Install"));
    Text remoteSourceText = new Text(" (Auto)");
    remoteSourceText.getStyleClass().add("text-disabled");
    textFlow.getChildren().add(remoteSourceText);
    CustomMenuItem installMenuItem = new CustomMenuItem(textFlow);
    installMenuItem.setOnAction(e -> {
      this.parentController.installProduct(remotePackage);
    });

    ContextMenu contextMenu = new ContextMenu();
    contextMenu.getItems().add(installMenuItem);

    Menu bundlesListMenuItem = new Menu("Other Packages");
    contextMenu.getItems().add(bundlesListMenuItem);

    for (PackageBundle bundle : remotePackage.getBundles()) {
      TextFlow bundleTextFlow = new TextFlow();
      bundleTextFlow.getChildren().add(new Label("Install"));
      Text bundleSource = new Text(" (" + bundle.getName() + ")");
      bundleSource.getStyleClass().add("text-disabled");
      bundleTextFlow.getChildren().add(bundleSource);
      CustomMenuItem bundleMenuItem = new CustomMenuItem(bundleTextFlow);
      bundleMenuItem.setOnAction(e -> {
        this.parentController.installBundle(bundle);
      });
      bundlesListMenuItem.getItems().add(bundleMenuItem);

    }

    MenuItem pluginPageMenuItem = new MenuItem("Browse plugin page...");
    pluginPageMenuItem.setOnAction(e -> {
      PlatformUtils.openDefaultBrowser(remotePackage.getPageUrl());
    });

    contextMenu.getItems().add(new SeparatorMenuItem());
    contextMenu.getItems().add(pluginPageMenuItem);
    this.setOnContextMenuRequested(e -> {
      contextMenu.show(this, e.getScreenX(), e.getScreenY());
    });

    Dimension2D size = generateSize();
    this.setPrefHeight(size.getHeight());
    this.setPrefWidth(size.getWidth());
  }

  private Dimension2D generateSize() {

    Random rand = new Random();
    float randomPoint = rand.nextFloat();

    if (randomPoint < 0.1) {
      return new Dimension2D(260, 260);
    } else if (randomPoint < 0.30) {
      return new Dimension2D(260, 130);
    } else {
      return new Dimension2D(130, 130);
    }

  }

  private Node createPluginStageFlag(RemotePackage remotePackage) {

    Polygon polygonFlag = new Polygon();
    polygonFlag.getPoints().addAll(new Double[] { 0.0, 0.0, 40.0, 0.0, 0.0, 40.0 });

    Label iconLabel = new Label("");
    iconLabel.setStyle("-fx-font-weight: bold;");
    iconLabel.setPadding(new Insets(5, 0, 0, 7));

    switch (remotePackage.getStage()) {
    case BETA:
      polygonFlag.setFill(Color.rgb(231, 76, 60));
      iconLabel.setText("B");
      break;
    case DEMO:
      polygonFlag.setFill(Color.rgb(155, 89, 182));
      iconLabel.setText("D");
      break;
    default:
      polygonFlag.setFill(Color.TRANSPARENT);
      iconLabel.setText("");
    }

    Group group = new Group();
    group.getChildren().add(polygonFlag);
    group.getChildren().add(iconLabel);

    return group;
  }

}
