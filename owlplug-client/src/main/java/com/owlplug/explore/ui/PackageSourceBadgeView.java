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
import com.owlplug.explore.model.RemoteSource;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class PackageSourceBadgeView extends HBox {

  public PackageSourceBadgeView(RemoteSource remoteSource, ApplicationDefaults applicationDefaults) {
    super();

    if(remoteSource.getUrl() != null &&
      remoteSource.getUrl().startsWith("https://owlplug.github.io/owlplug-registry/registry")) {

      this.setSpacing(5);
      this.getStyleClass().add("package-bloc-header");
      this.setAlignment(Pos.CENTER);

      Label headerLabel = new Label();
      headerLabel.setGraphic(new ImageView(applicationDefaults.verifiedImage));
      this.getChildren().add(headerLabel);

      headerLabel.setTooltip(new Tooltip("Official OwlPlug Registry\nPackages content and integrity are verified"));

      ImageView owlplugLogo = new ImageView(applicationDefaults.owlplugLogoSmall);
      owlplugLogo.setPreserveRatio(true);
      owlplugLogo.setFitHeight(16);
      this.getChildren().add(owlplugLogo);

    }

  }

}
