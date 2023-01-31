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
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class PackageSourceBadgeView extends HBox {

  private boolean isRoundBadge;

  public PackageSourceBadgeView(RemoteSource remoteSource, ApplicationDefaults applicationDefaults) {
    this(remoteSource, applicationDefaults, false);

  }

  public PackageSourceBadgeView(RemoteSource remoteSource, ApplicationDefaults applicationDefaults,
                                boolean isRoundBadge) {
    super();
    this.isRoundBadge = isRoundBadge;

    if (remoteSource.getUrl() != null
            && remoteSource.getUrl().startsWith("https://registry.owlplug.com/registry")) {
      String tooltipText = "Official OwlPlug Registry\nPackages content and integrity are verified";
      createContent(applicationDefaults.verifiedSourceImage, applicationDefaults.owlplugLogoSmall, tooltipText);
    }
    if (remoteSource.getUrl() != null
            && remoteSource.getUrl().startsWith("https://owlplug.github.io/owlplug-studiorack-registry/registry")) {
      String tooltipText = "Studiorack Registry";
      createContent(applicationDefaults.suggestedSourceImage, applicationDefaults.studiorackLogoSmall, tooltipText);
    }

  }

  private void createContent(Image checkIcon, Image logoIcon, String tooltip) {
    this.setSpacing(5);
    this.getStyleClass().add("package-source-badge");
    if (isRoundBadge) {
      this.getStyleClass().add("badge");
      this.setEffect(new DropShadow());
    }
    this.setAlignment(Pos.CENTER);
    Label headerLabel = new Label();
    headerLabel.setGraphic(new ImageView(checkIcon));
    this.getChildren().add(headerLabel);

    headerLabel.setTooltip(new Tooltip(tooltip));

    ImageView owlplugLogo = new ImageView(logoIcon);
    owlplugLogo.setPreserveRatio(true);
    owlplugLogo.setFitHeight(16);
    this.getChildren().add(owlplugLogo);

  }

}
