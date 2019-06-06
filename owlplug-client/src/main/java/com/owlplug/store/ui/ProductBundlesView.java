/* OwlPlug
 * Copyright (C) 2019 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OwlPlug is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OwlPlug.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package com.owlplug.store.ui;

import com.jfoenix.controls.JFXButton;
import com.owlplug.core.utils.FileUtils;
import com.owlplug.store.model.ProductBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
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

public class ProductBundlesView extends VBox {

  private static final Paint FILL = new LinearGradient(0, 0, 6, 0, false, CycleMethod.REPEAT, new Stop(0, Color.GRAY),
      new Stop(0.5, Color.GRAY), new Stop(0.5, Color.TRANSPARENT));

  // create background for regions
  private static final Background DOT_BACKGROUND = new Background(
      new BackgroundFill(FILL, CornerRadii.EMPTY, Insets.EMPTY));

  public ProductBundlesView() {
    super();

    this.setSpacing(5);

  }

  public void clear() {
    this.getChildren().clear();
  }

  public void addProductBundle(ProductBundle bundle, EventHandler<ActionEvent> installHandler) {

    HBox hbox = new HBox(5);
    hbox.setAlignment(Pos.CENTER_LEFT);
    hbox.setFillHeight(false);

    Label bundleName = new Label(bundle.getName());
    hbox.getChildren().add(bundleName);
    Region filler = new Region();
    filler.setPrefHeight(1);
    filler.setBackground(DOT_BACKGROUND);
    HBox.setHgrow(filler, Priority.ALWAYS);
    hbox.getChildren().add(filler);
    JFXButton installButton = new JFXButton("Install");
    installButton.getStyleClass().add("button-info");
    installButton.setOnAction(installHandler);
    hbox.getChildren().add(installButton);

    if (bundle.getFileSize() != 0) {
      String sizeText = FileUtils.humanReadableByteCount(bundle.getFileSize(), true);
      final Label sizeLabel = new Label("(" + sizeText + ")");
      sizeLabel.getStyleClass().add("label-disabled");
      hbox.getChildren().add(sizeLabel);
    }

    this.getChildren().add(hbox);

  }

}
