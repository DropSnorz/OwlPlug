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
import com.owlplug.core.utils.PlatformUtils;
import com.owlplug.core.utils.StringUtils;
import com.owlplug.explore.controllers.ExploreController;
import com.owlplug.explore.model.PackageBundle;
import com.owlplug.explore.model.PackageTag;
import com.owlplug.explore.model.RemotePackage;
import com.owlplug.plugin.model.PluginStage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import atlantafx.base.layout.InputGroup;

public class PackageListRowView extends HBox {

  private static final int THUMB_WIDTH = 80;
  private static final int THUMB_HEIGHT = 56;

  public PackageListRowView(ApplicationDefaults applicationDefaults, RemotePackage remotePackage,
                            Image image, ExploreController parentController) {
    super();
    this.getStyleClass().add("package-list-row");
    this.setAlignment(Pos.CENTER_LEFT);
    this.setSpacing(12);
    this.setPadding(new Insets(8, 12, 8, 12));

    // Thumbnail
    StackPane thumb = buildThumbnail(image, remotePackage);
    this.getChildren().add(thumb);

    // Name + creator + tags
    VBox meta = buildMeta(applicationDefaults, remotePackage, parentController);
    HBox.setHgrow(meta, Priority.ALWAYS);
    this.getChildren().add(meta);

    // Right side: type + formats + source badge + install
    HBox rightSection = buildRightSection(applicationDefaults, remotePackage, parentController);
    this.getChildren().add(rightSection);

    // Context menu
    buildContextMenu(remotePackage, parentController);
  }

  private StackPane buildThumbnail(Image image, RemotePackage remotePackage) {
    StackPane thumb = new StackPane();
    thumb.setPrefSize(THUMB_WIDTH, THUMB_HEIGHT);
    thumb.setMinSize(THUMB_WIDTH, THUMB_HEIGHT);
    thumb.setMaxSize(THUMB_WIDTH, THUMB_HEIGHT);
    thumb.getStyleClass().add("package-list-thumb");

    ImageView imageView = new ImageView();
    imageView.setFitWidth(THUMB_WIDTH);
    imageView.setFitHeight(THUMB_HEIGHT);
    imageView.setPreserveRatio(true);
    imageView.setSmooth(true);
    if (image != null) {
      imageView.setImage(image);
    }
    thumb.getChildren().add(imageView);

    if (remotePackage.getStage() != null && remotePackage.getStage() != PluginStage.RELEASE) {
      Label stageBadge = new Label(remotePackage.getStage().name());
      stageBadge.getStyleClass().add("package-stage-badge");
      if (remotePackage.getStage() == PluginStage.BETA) {
        stageBadge.getStyleClass().add("package-stage-beta");
      } else if (remotePackage.getStage() == PluginStage.DEMO) {
        stageBadge.getStyleClass().add("package-stage-demo");
      }
      StackPane.setAlignment(stageBadge, Pos.BOTTOM_LEFT);
      thumb.getChildren().add(stageBadge);
    }

    return thumb;
  }

  private VBox buildMeta(ApplicationDefaults applicationDefaults, RemotePackage remotePackage,
                         ExploreController parentController) {
    VBox meta = new VBox(3);
    meta.setAlignment(Pos.CENTER_LEFT);

    // Name + source badge
    HBox nameRow = new HBox(6);
    nameRow.setAlignment(Pos.CENTER_LEFT);
    Label nameLabel = new Label(remotePackage.getName());
    nameLabel.getStyleClass().add("package-list-name");
    nameRow.getChildren().add(nameLabel);
    nameRow.getChildren().add(new PackageSourceBadgeView(remotePackage.getRemoteSource(), applicationDefaults, true));

    // Creator
    HBox subRow = new HBox(6);
    subRow.setAlignment(Pos.CENTER_LEFT);
    if (remotePackage.getCreator() != null && !remotePackage.getCreator().isBlank()) {
      Label creatorLabel = new Label(remotePackage.getCreator());
      creatorLabel.getStyleClass().add("package-list-creator");
      subRow.getChildren().add(creatorLabel);
    }

    meta.getChildren().addAll(nameRow, subRow);

    // Tags row
    if (remotePackage.getTags() != null && !remotePackage.getTags().isEmpty()) {
      HBox tagsRow = new HBox(4);
      tagsRow.setAlignment(Pos.CENTER_LEFT);
      int shown = 0;
      for (PackageTag tag : remotePackage.getTags()) {
        if (shown >= 5) break;
        Label tagChip = new Label(tag.getName());
        tagChip.getStyleClass().add("package-list-tag");
        tagChip.setOnMouseClicked(e -> parentController.addSearchChip(tag.getName()));
        tagsRow.getChildren().add(tagChip);
        shown++;
      }
      meta.getChildren().add(tagsRow);
    }

    return meta;
  }

  private HBox buildRightSection(ApplicationDefaults applicationDefaults, RemotePackage remotePackage,
                                  ExploreController parentController) {
    HBox right = new HBox(12);
    right.setAlignment(Pos.CENTER_RIGHT);

    // Stacked info: type, formats, source
    VBox infoStack = new VBox(4);
    infoStack.setAlignment(Pos.CENTER_RIGHT);

    // Line 1: type icon + name + format chips
    HBox typeAndFormats = new HBox(6);
    typeAndFormats.setAlignment(Pos.CENTER_RIGHT);

    if (remotePackage.getType() != null) {
      Image typeIcon = applicationDefaults.getPackageTypeIcon(remotePackage);
      if (typeIcon != null) {
        ImageView typeImg = new ImageView(typeIcon);
        typeImg.setFitHeight(16);
        typeImg.setFitWidth(16);
        typeAndFormats.getChildren().add(typeImg);
      }
      Label typeLabel = new Label(remotePackage.getType().name());
      typeLabel.getStyleClass().add("label-disabled");
      typeAndFormats.getChildren().add(typeLabel);
    }

    if (remotePackage.getBundles() != null && !remotePackage.getBundles().isEmpty()) {
      java.util.Set<String> seenFormats = new java.util.LinkedHashSet<>();
      for (PackageBundle bundle : remotePackage.getBundles()) {
        if (bundle.getFormats() != null) {
          seenFormats.addAll(bundle.getFormats());
        }
      }
      for (String fmt : seenFormats) {
        Label fmtLabel = new Label(fmt.toUpperCase());
        fmtLabel.getStyleClass().add("package-list-format");
        Tooltip.install(fmtLabel, new Tooltip(fmt));
        typeAndFormats.getChildren().add(fmtLabel);
      }
    }

    if (!typeAndFormats.getChildren().isEmpty()) {
      infoStack.getChildren().add(typeAndFormats);
    }

    // Install button group stacked under type+formats
    InputGroup inputGroup = new InputGroup();
    Label versionLabel = new Label("v" + remotePackage.getVersion());
    versionLabel.setDisable(true);
    versionLabel.setMaxWidth(USE_COMPUTED_SIZE);
    inputGroup.getChildren().add(versionLabel);
    Button installButton = new Button("Install");
    installButton.getStyleClass().add("button-primary");
    installButton.setOnAction(e -> parentController.installPackage(remotePackage));
    inputGroup.getChildren().add(installButton);
    HBox installRow = new HBox(inputGroup);
    installRow.setAlignment(Pos.CENTER_RIGHT);
    infoStack.getChildren().add(installRow);

    right.getChildren().add(infoStack);

    return right;
  }

  private void buildContextMenu(RemotePackage remotePackage, ExploreController parentController) {
    TextFlow textFlow = new TextFlow();
    textFlow.getChildren().add(new Label("Install"));
    Text autoText = new Text(" (Auto)");
    autoText.getStyleClass().add("text-disabled");
    textFlow.getChildren().add(autoText);
    CustomMenuItem installMenuItem = new CustomMenuItem(textFlow);
    installMenuItem.setOnAction(e -> parentController.installPackage(remotePackage));

    ContextMenu contextMenu = new ContextMenu();
    contextMenu.getItems().add(installMenuItem);

    Menu bundlesMenu = new Menu("Other Packages");
    for (PackageBundle bundle : remotePackage.getBundles()) {
      TextFlow bundleTextFlow = new TextFlow();
      bundleTextFlow.getChildren().add(new Label("Install"));
      Text bundleSource = new Text(" (" + StringUtils.truncate(bundle.getName(), 50, "...") + ")");
      bundleSource.getStyleClass().add("text-disabled");
      bundleTextFlow.getChildren().add(bundleSource);
      CustomMenuItem bundleMenuItem = new CustomMenuItem(bundleTextFlow);
      bundleMenuItem.setOnAction(e -> parentController.installBundle(bundle));
      bundlesMenu.getItems().add(bundleMenuItem);
    }
    contextMenu.getItems().add(bundlesMenu);

    MenuItem pageMenuItem = new MenuItem("Browse plugin page...");
    pageMenuItem.setOnAction(e -> PlatformUtils.openDefaultBrowser(remotePackage.getPageUrl()));
    contextMenu.getItems().add(new SeparatorMenuItem());
    contextMenu.getItems().add(pageMenuItem);

    this.setOnContextMenuRequested(e -> contextMenu.show(this, e.getScreenX(), e.getScreenY()));
  }

}