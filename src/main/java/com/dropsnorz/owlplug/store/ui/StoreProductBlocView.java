package com.dropsnorz.owlplug.store.ui;

import java.util.Random;

import com.dropsnorz.owlplug.store.model.StoreProduct;
import com.jfoenix.controls.JFXRippler;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.effect.InnerShadow;
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
import javafx.scene.paint.Color;

public class StoreProductBlocView extends VBox {
	
	public StoreProductBlocView(StoreProduct storeProduct, Image image) {
		super();
		
		VBox header = new VBox();
		header.getStyleClass().add("panel-transparent-dark");
		header.getChildren().add(new Label(storeProduct.getName()));
		header.setPrefSize(USE_COMPUTED_SIZE, USE_COMPUTED_SIZE);
		header.setAlignment(Pos.BOTTOM_LEFT);
		this.getChildren().add(header);
		
		
		this.setAlignment(Pos.BOTTOM_LEFT);
		this.setPrefHeight(getRandomSize() );
		this.setPrefWidth(getRandomSize() );

		this.getChildren().add(new Pane());
						
		BackgroundImage bgImg = new BackgroundImage(image, 
			    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
			    BackgroundPosition.CENTER, 
			    new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true));
		
		this.setBackground(new Background(bgImg));
		this.setEffect(new InnerShadow(11, Color.BLACK));
		
	}
	
	
	private int getRandomSize() {
		
		Random r = new Random();
		return ((r.nextInt(180 - 140) + 140) / 10) * 10;
	}

}
