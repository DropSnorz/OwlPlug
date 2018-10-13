package com.dropsnorz.owlplug.store.ui;

import com.dropsnorz.owlplug.core.components.ApplicationDefaults;
import com.dropsnorz.owlplug.core.model.PluginStage;
import com.dropsnorz.owlplug.core.utils.PlatformUtils;
import com.dropsnorz.owlplug.store.controllers.StoreController;
import com.dropsnorz.owlplug.store.model.StoreProduct;
import java.util.Random;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class StoreProductBlocView extends AnchorPane {

	private StoreController parentController;

	/**
	 * Creates a new store product bloc view instance.
	 * @param applicationDefaults - OwlPlug application defaults
	 * @param storeProduct - related store product
	 * @param image - product image
	 * @param parentController - parent store controller
	 */
	public StoreProductBlocView(ApplicationDefaults applicationDefaults, StoreProduct storeProduct, 
			Image image, StoreController parentController) {
		super();
		this.parentController = parentController;

		VBox content = new VBox();
		this.getChildren().add(content);
		AnchorPane.setBottomAnchor(content, 0.0);
		AnchorPane.setTopAnchor(content, 0.0);
		AnchorPane.setLeftAnchor(content, 0.0);
		AnchorPane.setRightAnchor(content, 0.0);

		HBox header = new HBox();
		header.setSpacing(5);
		header.getStyleClass().add("panel-transparent-dark");
		if (storeProduct.getType() != null) {
			Image typeIcon = applicationDefaults.getProductTypeIcon(storeProduct);
			if (typeIcon != null) {
				ImageView typeImageView = new ImageView(typeIcon);
				typeImageView.setFitHeight(16);
				typeImageView.setFitWidth(16);
				header.getChildren().add(typeImageView);
			}
		}
		header.getChildren().add(new Label(storeProduct.getName()));
		header.setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
		header.setAlignment(Pos.BOTTOM_LEFT);
		content.getChildren().add(header);

		content.setAlignment(Pos.BOTTOM_LEFT);
		this.setPrefHeight(getRandomSize());
		this.setPrefWidth(getRandomSize());

		if (storeProduct.getStage() != null && storeProduct.getStage() != PluginStage.RELEASE) {
			System.out.println("Here");
			this.getChildren().add(createPluginStageFlag(storeProduct));
		}

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


	private Node createPluginStageFlag(StoreProduct storeProduct) {
		
		Polygon polygonFlag = new Polygon();
		polygonFlag.getPoints().addAll(new Double[]{
				0.0, 0.0,
				40.0, 0.0,
				0.0, 40.0 });
		
		Label iconLabel = new Label("");
		iconLabel.setStyle("-fx-font-weight: bold;");
		iconLabel.setPadding(new Insets(5,0,0,7));
		
		switch (storeProduct.getStage()) {
			case BETA: 
				polygonFlag.setFill(Color.rgb(231, 76, 60));
				iconLabel.setText("B");
				break;
			case DEMO:
				polygonFlag.setFill(Color.rgb(155, 89, 182));
				iconLabel.setText("D");
				break;
			default:
				polygonFlag.setFill(Color.TRANSPARENT);
				iconLabel.setText("");
		}
		
		Group group = new Group();
		group.getChildren().add(polygonFlag);
		group.getChildren().add(iconLabel);
		
		return group;
	}

}
