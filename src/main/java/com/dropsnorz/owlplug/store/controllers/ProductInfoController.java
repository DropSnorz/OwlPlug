package com.dropsnorz.owlplug.store.controllers;

import com.dropsnorz.owlplug.core.components.ImageCache;
import com.dropsnorz.owlplug.core.ui.SideBar;
import com.dropsnorz.owlplug.core.utils.PlatformUtils;
import com.dropsnorz.owlplug.store.model.StoreProduct;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class ProductInfoController {

	@Autowired
	private StoreController storeController;
	@Autowired
	private ImageCache imageCache;

	@FXML
	private Pane productInfoContainer;
	@FXML
	private Pane productInfoContent;
	@FXML
	private Pane topInfoPane;
	@FXML
	private JFXButton closeButton;
	@FXML
	private Label productNameLabel;
	@FXML
	private Label productStoreLabel;
	@FXML
	private JFXButton browsePageButton;
	@FXML
	private JFXButton installButton;


	private SideBar sidebar;

	/**
	 * FXML initialize.
	 */
	public void initialize() {

		// Wrap info content inside a proxy sidebar
		productInfoContainer.getChildren().remove(productInfoContent);
		sidebar = new SideBar(400, productInfoContent);
		sidebar.collapse();
		productInfoContainer.getChildren().add(sidebar);

		closeButton.setOnAction(e -> {
			sidebar.collapse();
		});

	}

	public void setProduct(StoreProduct product) {

		this.productNameLabel.setText(product.getName());
		Image screenshot = imageCache.get(product.getScreenshotUrl());

		if (screenshot != null) {
			BackgroundImage bgImg = new BackgroundImage(screenshot, 
					BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
					BackgroundPosition.CENTER, 
					new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true));
			topInfoPane.setBackground(new Background(bgImg));
		}

		this.productStoreLabel.setText(product.getStore().getName());

		browsePageButton.setOnAction(e -> {
			PlatformUtils.openDefaultBrowser(product.getPageUrl());
		});
		installButton.setOnAction(e -> {
			storeController.installProduct(product);
		});
	}

	public void show() {
		if (sidebar.isCollapsed()) {
			sidebar.expand();
		}
	}

	public void hide() {
		sidebar.collapse();
	}

	public void toggleVisibility() {
		sidebar.toggle();
	}

}
