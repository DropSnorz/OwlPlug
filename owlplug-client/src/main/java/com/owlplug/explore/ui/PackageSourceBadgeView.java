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
