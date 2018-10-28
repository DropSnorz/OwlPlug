package com.dropsnorz.owlplug.store.controllers;

import com.dropsnorz.owlplug.core.controllers.MainController;
import com.dropsnorz.owlplug.store.dao.StoreDAO;
import com.dropsnorz.owlplug.store.model.Store;
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
	private NewStoreDialogController newStoreDialogController;
	@Autowired
	private StoreDAO pluginStoreDAO;
	
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
		for (Store pluginStore : pluginStoreDAO.findAll()) {
			storeListHolder.getChildren().add(new StoreMenuItem(pluginStore));
		}
	}
	
	
	private class StoreMenuItem extends HBox {
		
		StoreMenuItem(Store pluginStore) {
			this.getStyleClass().add("menu-item");
			this.setAlignment(Pos.CENTER_LEFT);
			String iconChar = "?";
			if (pluginStore.getName().length() > 0) {
				iconChar = pluginStore.getName().substring(0, 1);
			}
			Label iconLabel = new Label(iconChar);
			iconLabel.setFont(Font.font(iconLabel.getFont().getFamily(), FontWeight.BOLD, 32));
			this.getChildren().add(iconLabel);
			
			VBox namePane = new VBox();
			namePane.setAlignment(Pos.CENTER_LEFT);
			namePane.getChildren().add(new Label(pluginStore.getName()));
			Label url = new Label(pluginStore.getUrl());
			url.getStyleClass().add("label-disabled");
			namePane.getChildren().add(url);
			this.getChildren().add(namePane);
			
			HBox emptySpace = new HBox();
			HBox.setHgrow(emptySpace, Priority.ALWAYS);
			this.getChildren().add(emptySpace);
			
			Hyperlink deleteButton = new Hyperlink("X");
			deleteButton.getStyleClass().add("hyperlink-button");
			this.getChildren().add(deleteButton);

			deleteButton.setOnAction(e -> {
				pluginStoreDAO.delete(pluginStore);
				refreshView();
			});
		

		}
	}
	

}
