package com.dropsnorz.owlplug.store.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dropsnorz.owlplug.core.components.ImageCache;
import com.dropsnorz.owlplug.store.model.StaticStoreProduct;
import com.dropsnorz.owlplug.store.service.StoreService;
import com.dropsnorz.owlplug.store.ui.StoreProductBlocView;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXMasonryPane;
import com.jfoenix.controls.JFXMasonryPane.LayoutMode;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXScrollPane;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;

@Controller
public class StoreController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private StoreService storeService;
	@Autowired
	private ImageCache imageCache;
	@FXML
	private JFXButton syncStoreButton;
	@FXML
	private JFXMasonryPane masonryPane;
	@FXML
	private ScrollPane scrollPane;

	public void initialize() {

		syncStoreButton.setOnAction(e -> {
			storeService.syncStores();
		});

		refreshView();

	}

	public void refreshView() {
		this.masonryPane.getChildren().clear();

		//Force the pane to recompute layout for new nodes. Should be replaced by clear clearLayout()
		// in the next JFoenix releases
		this.masonryPane.setLayoutMode(LayoutMode.MASONRY);

		Platform.runLater(() ->{
			for(StaticStoreProduct product : storeService.getStoreProducts()) {
				Image image = imageCache.get(product.getIconUrl());
				JFXRippler rippler = new JFXRippler(new StoreProductBlocView(product, image));			
				masonryPane.getChildren().add(rippler);
			}
			Platform.runLater(() ->scrollPane.requestLayout());
		});

	}

}
