package com.dropsnorz.owlplug.store.ui;

import com.dropsnorz.owlplug.core.utils.PlatformUtils;
import com.dropsnorz.owlplug.store.controllers.StoreController;
import com.dropsnorz.owlplug.store.model.StoreProduct;
import java.util.Random;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class StoreProductBlocView extends VBox {

	private StoreController parentController;

	public StoreProductBlocView(StoreProduct storeProduct, Image image, StoreController parentController) {
		super();
		this.parentController = parentController;

		VBox header = new VBox();
		header.getStyleClass().add("panel-transparent-dark");
		header.getChildren().add(new Label(storeProduct.getName()));
		header.setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
		header.setAlignment(Pos.BOTTOM_LEFT);
		this.getChildren().add(header);

		this.setAlignment(Pos.BOTTOM_LEFT);
		this.setPrefHeight(getRandomSize());
		this.setPrefWidth(getRandomSize());
		this.getChildren().add(new Pane());

		BackgroundImage bgImg = new BackgroundImage(image, 
				BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
				BackgroundPosition.CENTER, 
				new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true));

		this.setBackground(new Background(bgImg));
		this.setEffect(new InnerShadow(11, Color.BLACK));

		// Create ContextMenu
		ContextMenu contextMenu = new ContextMenu();
		MenuItem installMenuItem = new MenuItem("Install");
		installMenuItem.setOnAction(e -> {
			this.parentController.installProduct(storeProduct);
		});
		MenuItem pluginPageMenuItem = new MenuItem("Browse plugin page...");
		pluginPageMenuItem.setOnAction(e -> {
			PlatformUtils.openDefaultBrowser(storeProduct.getPageUrl());
		});

		contextMenu.getItems().add(installMenuItem);
		contextMenu.getItems().add(pluginPageMenuItem);
		this.setOnContextMenuRequested(e -> {
			contextMenu.show(this, e.getScreenX(), e.getScreenY());
		});
	}


	private int getRandomSize() {
		Random r = new Random();
		return ((r.nextInt(180 - 140) + 140) / 10) * 10;
	}

}
