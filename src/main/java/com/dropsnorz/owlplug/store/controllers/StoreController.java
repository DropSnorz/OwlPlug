package com.dropsnorz.owlplug.store.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dropsnorz.owlplug.store.model.StaticStoreProduct;
import com.dropsnorz.owlplug.store.service.StoreService;
import com.dropsnorz.owlplug.store.ui.StoreProductBlocView;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXMasonryPane;
import com.jfoenix.controls.JFXRippler;

import javafx.fxml.FXML;

@Controller
public class StoreController {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private StoreService storeService;
	
	@FXML
	private JFXButton syncStoreButton;
	@FXML
	private JFXMasonryPane masonryPane;
	
	public void initialize() {
		
		syncStoreButton.setOnAction(e -> {
			storeService.syncStores();
		});
		
		refreshView();
		
	}
	
	public void refreshView() {
		
		for(StaticStoreProduct product : storeService.getStoreProducts()) {
			log.debug("Init bloc view");
			
			JFXRippler rippler = new JFXRippler(new StoreProductBlocView(product));			
			masonryPane.getChildren().add(rippler);

		}
	}

}
