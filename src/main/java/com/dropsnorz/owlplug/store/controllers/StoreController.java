package com.dropsnorz.owlplug.store.controllers;

import com.dropsnorz.owlplug.ApplicationDefaults;
import com.dropsnorz.owlplug.core.components.ImageCache;
import com.dropsnorz.owlplug.store.model.StaticStoreProduct;
import com.dropsnorz.owlplug.store.model.StoreProduct;
import com.dropsnorz.owlplug.store.service.StoreService;
import com.dropsnorz.owlplug.store.ui.StoreProductBlocView;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXMasonryPane;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXTextField;
import java.io.File;
import java.util.prefs.Preferences;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class StoreController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private Preferences prefs;
	@Autowired
	private StoreService storeService;
	@Autowired
	private ImageCache imageCache;

	@FXML
	private JFXTextField storeSearchTextField;
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
		storeSearchTextField.textProperty().addListener((obs, oldValue, newValue)->{
			refreshView();
		});

		refreshView();
	}

	public synchronized void refreshView() {
		this.masonryPane.getChildren().clear();
		this.masonryPane.clearLayout();

		for (StaticStoreProduct product : storeService.getStoreProducts(storeSearchTextField.getText())) {
			Image image = imageCache.get(product.getIconUrl());
			JFXRippler rippler = new JFXRippler(new StoreProductBlocView(product, image, this));			
			masonryPane.getChildren().add(rippler);
		}
		Platform.runLater(() -> { 
			scrollPane.requestLayout();
		});
	}

	public void installProduct(StoreProduct product) {

		File selectedDirectory = null;

		if (prefs.getBoolean(ApplicationDefaults.STORE_DIRECTORY_ENABLED_KEY, false)) {
			String storeDirectoryPath = prefs.get(ApplicationDefaults.STORE_DIRECTORY_KEY,"");
			if (!"".equals(storeDirectoryPath)) {
				selectedDirectory = new File(prefs.get(ApplicationDefaults.STORE_DIRECTORY_KEY,""));
			}
		} else {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			if (prefs.get(ApplicationDefaults.VST_DIRECTORY_KEY, null) != null) {
				directoryChooser.setInitialDirectory(new File(prefs.get(ApplicationDefaults.VST_DIRECTORY_KEY, "")));
			}

			Window mainWindow = masonryPane.getScene().getWindow();
			selectedDirectory = directoryChooser.showDialog(mainWindow);
		}

		if (selectedDirectory != null && selectedDirectory.isDirectory()) {
			storeService.install(product, selectedDirectory);
		}
		else {
			log.error("Error: Specified install directory don't exists.");
		}
	}
}
