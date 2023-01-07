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

import com.jfoenix.controls.JFXToggleButton;
import com.owlplug.core.controllers.BaseController;
import com.owlplug.core.controllers.MainController;
import com.owlplug.explore.model.RemoteSource;
import com.owlplug.explore.services.ExploreService;
import com.owlplug.explore.ui.PackageSourceBadgeView;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class SourceMenuController extends BaseController {

  @Autowired
  private MainController mainController;
  @Autowired
  private ExploreController exploreController;
  @Autowired
  private NewSourceDialogController newSourceDialogController;
  @Autowired
  private ExploreService exploreService;

  @FXML
  private VBox sourceListHolder;
  @FXML
  private Pane newSourceMenuItem;

  /**
   * FXML initialize.
   */
  public void initialize() {

    newSourceMenuItem.setOnMouseClicked(e -> {
      newSourceDialogController.show();
      newSourceDialogController.startCreateSequence();
      mainController.getLeftDrawer().close();
    });

    refreshView();
  }

  /**
   * Updates the menu with saved plugin stores.
   */
  public void refreshView() {
    sourceListHolder.getChildren().clear();
    for (RemoteSource pluginRemoteSource : exploreService.getRemoteSources()) {
      sourceListHolder.getChildren().add(new StoreMenuItem(pluginRemoteSource));
    }
  }

  private class StoreMenuItem extends HBox {

    StoreMenuItem(RemoteSource pluginRemoteSource) {
      this.getStyleClass().add("menu-item");
      this.setAlignment(Pos.CENTER_LEFT);

      // Info Area
      VBox infoPane = new VBox();
      infoPane.setAlignment(Pos.CENTER_LEFT);
      infoPane.setSpacing(5);

      HBox namePane = new HBox();
      namePane.setAlignment(Pos.CENTER_LEFT);
      VBox.setVgrow(namePane, Priority.NEVER);

      PackageSourceBadgeView badge = new PackageSourceBadgeView(pluginRemoteSource, getApplicationDefaults(), true);
      badge.setMaxHeight(28);
      HBox.setMargin(badge, new Insets(0,5,0,0));
      namePane.getChildren().add(badge);

      namePane.getChildren().add(new Label(pluginRemoteSource.getName()));
      JFXToggleButton activeToggleButton = new JFXToggleButton();
      activeToggleButton.setSize(5);
      activeToggleButton.setSelected(pluginRemoteSource.isEnabled());
      activeToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
        exploreService.enableSource(pluginRemoteSource, newValue);
        exploreController.refreshView();

      });
      namePane.getChildren().add(activeToggleButton);
      infoPane.getChildren().add(namePane);


      HBox storeMetadata = new HBox();
      storeMetadata.setSpacing(5);
      storeMetadata.setAlignment(Pos.CENTER_LEFT);
      if (pluginRemoteSource.getType() != null) {
        Label typeLabel = new Label(pluginRemoteSource.getType().getShortLabel());
        typeLabel.setTooltip(new Tooltip(pluginRemoteSource.getType().getLabel()));
        typeLabel.getStyleClass().add("chip-label");
        storeMetadata.getChildren().add(typeLabel);
      }

      Label url = new Label(formatUrl(pluginRemoteSource.getDisplayUrl()));
      url.getStyleClass().add("label-disabled");
      storeMetadata.getChildren().add(url);

      infoPane.getChildren().add(storeMetadata);
      this.getChildren().add(infoPane);

      // Empty growing area
      HBox emptySpace = new HBox();
      HBox.setHgrow(emptySpace, Priority.ALWAYS);
      this.getChildren().add(emptySpace);

      // Delete button area
      Hyperlink deleteButton = new Hyperlink("X");
      deleteButton.getStyleClass().add("hyperlink-button");
      this.getChildren().add(deleteButton);

      deleteButton.setOnAction(e -> {
        exploreService.delete(pluginRemoteSource);
        refreshView();
      });

    }

    private String formatUrl(String url) {
      if (url == null) {
        return null;
      }
      return url.replace("http://", "").replaceAll("https://", "");
    }
  }

}
