package com.dropsnorz.owlplug.store.controllers;

import com.dropsnorz.owlplug.core.components.ImageCache;
import com.dropsnorz.owlplug.core.model.PluginType;
import com.dropsnorz.owlplug.core.ui.SideBar;
import com.dropsnorz.owlplug.core.utils.PlatformUtils;
import com.dropsnorz.owlplug.store.model.ProductBundle;
import com.dropsnorz.owlplug.store.model.ProductTag;
import com.dropsnorz.owlplug.store.model.StoreProduct;
import com.dropsnorz.owlplug.store.ui.ProductBundlesView;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
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
	@FXML
	private Label creatorLabel;
	@FXML
	private Label typeLabel;
	@FXML
	private FlowPane tagContainer;
	@FXML
	private Label descriptionLabel;
	@FXML
	private Pane bundlesContainer;
	
	private ProductBundlesView bundlesView;

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
		
		bundlesView = new ProductBundlesView();
		bundlesContainer.getChildren().add(bundlesView);

	}

	public void setProduct(StoreProduct product) {
		
		// Active install buttons
		this.installButton.setDisable(false);

		
		// Bind product properties to controls
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
			this.installButton.setDisable(true);
		});

		this.creatorLabel.setText(product.getCreator());

		if (product.getType() == PluginType.EFFECT) {
			this.typeLabel.setText("Instrument (VSTi)");
		} else if (product.getType() == PluginType.INSTRUMENT) {
			this.typeLabel.setText("Effect (VST)");
		}

		tagContainer.getChildren().clear();
		for (ProductTag tag : product.getTags()) {
			Node chip =	new FakeChip(tag.getName());
			chip.getStyleClass().add("jfx-chip");
			tagContainer.getChildren().add(chip);

		}

		this.descriptionLabel.setText(product.getDescription());
		
		bundlesView.clear();
		for (ProductBundle bundle : product.getBundles()) {
			bundlesView.addProductBundle(bundle, e -> storeController.installBundle(bundle));
		}

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

	private class FakeChip extends HBox {

		public FakeChip(String text) {

			Label label = new Label(text);
			label.setWrapText(true);
			label.setMaxWidth(100);
			getChildren().add(label);
			this.getStyleClass().add("fake-chip");
		}

	}

}
