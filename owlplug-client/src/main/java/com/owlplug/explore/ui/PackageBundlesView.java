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
import com.owlplug.core.model.PluginFormat;
import com.owlplug.core.utils.FileUtils;
import com.owlplug.explore.model.PackageBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;

public class PackageBundlesView extends VBox {

  private ApplicationDefaults applicationDefaults;

  private static final Paint FILL = new LinearGradient(0, 0, 6, 0, false, CycleMethod.REPEAT, new Stop(0, Color.GRAY),
      new Stop(0.5, Color.GRAY), new Stop(0.5, Color.TRANSPARENT));

  // create background for regions
  private static final Background DOT_BACKGROUND = new Background(
      new BackgroundFill(FILL, CornerRadii.EMPTY, Insets.EMPTY));

  public PackageBundlesView(ApplicationDefaults applicationDefaults) {
    super();
    this.applicationDefaults = applicationDefaults;
    this.setSpacing(5);

  }

  public void clear() {
    this.getChildren().clear();
  }

  public void addPackageBundle(PackageBundle bundle, EventHandler<ActionEvent> installHandler) {

    HBox hbox = new HBox(5);
    hbox.setAlignment(Pos.CENTER_LEFT);
    hbox.setFillHeight(false);

    HBox formatsContainer = new HBox(2);
    for (String formatValue : bundle.getFormats()) {
      PluginFormat format = PluginFormat.fromBundleString(formatValue);
      if (format != null) {
        ImageView iv = new ImageView(this.applicationDefaults.getPluginFormatIcon(format));
        Label labelIv = new Label();
        labelIv.setGraphic(iv);
        labelIv.setTooltip(new Tooltip(format.toString()));
        formatsContainer.getChildren().add(labelIv);
      }
    }
    hbox.getChildren().add(formatsContainer);

    Label bundleName = new Label(bundle.getName());
    bundleName.setWrapText(true);
    hbox.getChildren().add(bundleName);
    HBox.setHgrow(bundleName, Priority.ALWAYS);
    Region filler = new Region();
    filler.setPrefHeight(1);
    filler.setBackground(DOT_BACKGROUND);
    HBox.setHgrow(filler, Priority.SOMETIMES);
    hbox.getChildren().add(filler);
    Button installButton = new Button("Install");
    installButton.getStyleClass().add("button-info");
    installButton.setOnAction(installHandler);
    installButton.setMinWidth(USE_PREF_SIZE);
    hbox.getChildren().add(installButton);

    if (bundle.getFileSize() != 0) {
      String sizeText = FileUtils.humanReadableByteCount(bundle.getFileSize(), true);
      final Label sizeLabel = new Label("(" + sizeText + ")");
      sizeLabel.getStyleClass().add("label-disabled");
      sizeLabel.setMinWidth(USE_PREF_SIZE);
      hbox.getChildren().add(sizeLabel);
    }

    this.getChildren().add(hbox);

  }

}
