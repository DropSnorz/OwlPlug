package com.dropsnorz.owlplug.store.controllers;

import com.dropsnorz.owlplug.store.dao.PluginStoreDAO;
import com.dropsnorz.owlplug.store.model.PluginStore;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class StoreMenuController {

	@Autowired
	private PluginStoreDAO pluginStoreDAO;
	
	@FXML
	private VBox storeListHolder;
	
	/**
	 * FXML initialize.
	 */
	public void initialize() {
		refreshView();
	}
	
	private void refreshView() {
		storeListHolder.getChildren().clear();
		for (PluginStore pluginStore : pluginStoreDAO.findAll()) {
			storeListHolder.getChildren().add(new StoreMenuItem(pluginStore));
		}
	}
	
	
	private class StoreMenuItem extends HBox {
		
		StoreMenuItem(PluginStore pluginStore) {
			this.getStyleClass().add("menu-item");
			this.setAlignment(Pos.CENTER_LEFT);
			String iconChar = "?";
			if (pluginStore.getName().length() > 0) {
				iconChar = pluginStore.getName().substring(0, 1);
			}
			Label iconLabel = new Label(iconChar);
			iconLabel.setFont(Font.font(iconLabel.getFont().getFamily(), FontWeight.BOLD, 32));
			this.getChildren().add(iconLabel);
			this.getChildren().add(new Label(pluginStore.getName()));
		}
	}
	

}
