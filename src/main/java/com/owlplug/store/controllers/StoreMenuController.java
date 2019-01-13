package com.owlplug.store.controllers;

import com.jfoenix.controls.JFXToggleButton;
import com.owlplug.core.controllers.MainController;
import com.owlplug.store.dao.StoreDAO;
import com.owlplug.store.model.Store;
import com.owlplug.store.service.StoreService;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class StoreMenuController {

  @Autowired
  private MainController mainController;
  @Autowired
  private StoreController storeController;
  @Autowired
  private NewStoreDialogController newStoreDialogController;
  @Autowired
  private StoreService storeService;

  @FXML
  private VBox storeListHolder;
  @FXML
  private Pane newStoreMenuItem;

  /**
   * FXML initialize.
   */
  public void initialize() {

    newStoreMenuItem.setOnMouseClicked(e -> {
      newStoreDialogController.show();
      newStoreDialogController.startCreateSequence();
      mainController.getLeftDrawer().close();
    });

    refreshView();
  }

  /**
   * Updates the menu with saved plugin stores.
   */
  public void refreshView() {
    storeListHolder.getChildren().clear();
    for (Store pluginStore : storeService.getStores()) {
      storeListHolder.getChildren().add(new StoreMenuItem(pluginStore));
    }
  }

  private class StoreMenuItem extends HBox {

    StoreMenuItem(Store pluginStore) {
      this.getStyleClass().add("menu-item");
      this.setAlignment(Pos.CENTER_LEFT);

      // Icon Area
      String iconChar = "?";
      if (pluginStore.getName().length() > 0) {
        iconChar = pluginStore.getName().substring(0, 1);
      }
      Label iconLabel = new Label(iconChar);
      iconLabel.setFont(Font.font(iconLabel.getFont().getFamily(), FontWeight.BOLD, 32));
      this.getChildren().add(iconLabel);

      // Info Area
      VBox infoPane = new VBox();
      infoPane.setAlignment(Pos.CENTER_LEFT);

      HBox namePane = new HBox();
      namePane.setAlignment(Pos.BASELINE_LEFT);
      namePane.getChildren().add(new Label(pluginStore.getName()));
      JFXToggleButton activeToggleButton = new JFXToggleButton();
      activeToggleButton.setSize(5);
      activeToggleButton.setSelected(pluginStore.isEnabled());
      activeToggleButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
        storeService.enableStore(pluginStore, newValue);
        storeController.refreshView();

      });
      namePane.getChildren().add(activeToggleButton);
      infoPane.getChildren().add(namePane);
      Label url = new Label(formatUrl(pluginStore.getUrl()));
      url.getStyleClass().add("label-disabled");
      infoPane.getChildren().add(url);
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
        storeService.delete(pluginStore);
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
