package com.dropsnorz.owlplug.store.ui;

import java.util.Random;

import com.dropsnorz.owlplug.store.model.StoreProduct;
import com.jfoenix.controls.JFXRippler;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class StoreProductBlocView extends VBox {
	
	public StoreProductBlocView(StoreProduct storeProduct) {
		super();
		
		VBox header = new VBox();
		header.getStyleClass().add("panel-transparent");
		header.getChildren().add(new Label(storeProduct.getName()));
		header.setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
		header.setAlignment(Pos.BOTTOM_LEFT);
		this.getChildren().add(header);
		
		
		this.setAlignment(Pos.BOTTOM_LEFT);
		this.setPrefHeight(getRandomSize() );
		this.setPrefWidth(getRandomSize() );
		
		this.getChildren().add(new Pane());
		
		//this.getStyleClass().add("store-product-bloc");
		
		Image image = new Image(storeProduct.getIconUrl(), 400, 400, true, true, true);
		
		BackgroundImage bgImg = new BackgroundImage(image, 
			    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
			    BackgroundPosition.CENTER, 
			    new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true));
		
		
		this.setBackground(new Background(bgImg));
		
	}
	
	
	private int getRandomSize() {
		
		Random r = new Random();
		return (r.nextInt(((210 - 150) + 150)) / 10) * 10;
	}

}
