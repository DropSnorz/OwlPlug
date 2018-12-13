package com.dropsnorz.owlplug.store.controllers;

import com.dropsnorz.owlplug.core.components.ApplicationDefaults;
import com.dropsnorz.owlplug.core.components.ImageCache;
import com.dropsnorz.owlplug.core.components.LazyViewRegistry;
import com.dropsnorz.owlplug.core.components.TaskFactory;
import com.dropsnorz.owlplug.core.controllers.MainController;
import com.dropsnorz.owlplug.store.model.StoreProduct;
import com.dropsnorz.owlplug.store.service.StoreService;
import com.dropsnorz.owlplug.store.ui.StoreProductBlocViewBuilder;
import com.google.common.collect.Iterables;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXMasonryPane;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXTextField;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.prefs.Preferences;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class StoreController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private static final int PARTITION_SIZE = 20;

	@Autowired
	private Preferences prefs;
	@Autowired
	private ApplicationDefaults applicationDefaults;
	@Autowired
	private StoreService storeService;
	@Autowired
	private ImageCache imageCache;
	@Autowired
	private LazyViewRegistry viewRegistry;
	@Autowired
	private MainController mainController;
	@Autowired
	private TaskFactory taskFactory;

	@FXML
	private JFXButton storesButton;
	@FXML
	private JFXTextField storeSearchTextField;
	@FXML
	private JFXButton syncStoreButton;
	@FXML
	private VBox masonryWrapper;
	@FXML
	private JFXMasonryPane masonryPane;
	@FXML
	private ScrollPane scrollPane;
	@FXML
	private HBox lazyLoadBar;
	@FXML
	private Hyperlink lazyLoadLink;


	private StoreProductBlocViewBuilder storeProductBlocViewBuilder = null;

	/**
	 * Loaded products from store are displayed by partitions (like pagination).
	 * When the user scrolls the entire partition, the next one is appended in the UI.
	 */
	private Iterable<List<StoreProduct>> loadedProductPartitions;

	private Iterable<StoreProduct> loadedStoreProducts = new ArrayList<>();

	/**
	 * Counter of loaded partitions on UI.
	 */
	private int displayedPartitions = 0;

	/**
	 * FXML initialize.
	 */
	public void initialize() {

		storeProductBlocViewBuilder =
				new StoreProductBlocViewBuilder(applicationDefaults, imageCache, this);

		storesButton.setOnAction(e -> {
			mainController.setLeftDrawer(viewRegistry.get(LazyViewRegistry.STORE_MENU_VIEW));
			mainController.getLeftDrawer().open();

		});	

		syncStoreButton.setOnAction(e -> {
			storeService.syncStores();
		});
		storeSearchTextField.textProperty().addListener((obs, oldValue, newValue) -> {

			final String query = storeSearchTextField.getText();
			Task<Iterable<StoreProduct>> task = new Task<Iterable<StoreProduct>>() {
				@Override
				protected Iterable<StoreProduct> call() throws Exception {
					return storeService.getStoreProducts(query);
				}
			};
			task.setOnSucceeded(e -> {
				refreshView(task.getValue());
			});
			new Thread(task).start();
		});

		scrollPane.vvalueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				if (newValue.doubleValue() == 1) {
					displayNewProductPartition();
				}
			}
		});

		lazyLoadLink.setOnAction(e -> {
			displayNewProductPartition();
		});
		lazyLoadBar.setVisible(false);
		
		taskFactory.addSyncStoresListener(() -> refreshView());
		refreshView();

	}

	/**
	 * Refresh Store View.
	 */
	public synchronized void refreshView() {
		Iterable<StoreProduct> newProducts = storeService.getStoreProducts(storeSearchTextField.getText());
		refreshView(newProducts);

	}

	/**
	 * Display store products list.
	 * @param newProducts - Store product list
	 */
	public synchronized void refreshView(Iterable<StoreProduct> newProducts) {

		if (shouldRefreshProducts(newProducts)) {
			this.masonryPane.getChildren().clear();
			this.masonryPane.requestLayout();

			loadedStoreProducts = newProducts;
			loadedProductPartitions = Iterables.partition(loadedStoreProducts, PARTITION_SIZE);	
			displayedPartitions = 0;
			displayNewProductPartition();
		}
	}

	private void displayNewProductPartition() {

		if (Iterables.size(loadedProductPartitions) > displayedPartitions) {

			for (StoreProduct product : Iterables.get(loadedProductPartitions, displayedPartitions)) {
				JFXRippler rippler = new JFXRippler(storeProductBlocViewBuilder.build(product));			
				masonryPane.getChildren().add(rippler);
			}
			displayedPartitions += 1;

			if (Iterables.size(loadedProductPartitions) == displayedPartitions) {
				lazyLoadBar.setVisible(false);
			} else {
				lazyLoadBar.setVisible(true);

			}

			Platform.runLater(() -> { 
				masonryPane.requestLayout();
				scrollPane.requestLayout();
			});
		}

	}

	/**
	 * Returns true if the given product list is different from the previously loaded product list.
	 * @param newProducts - the new product list
	 * @return
	 */
	private boolean shouldRefreshProducts(Iterable<StoreProduct> newProducts) {

		if (Iterables.size(newProducts) != Iterables.size(loadedStoreProducts)) {
			return true;
		}

		for (int i = 0; i < Iterables.size(newProducts); i++) {
			if (!Iterables.get(newProducts, i).getId().equals(Iterables.get(loadedStoreProducts, i).getId())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Trigger product installation.
	 * @param product Product to install
	 */
	public void installProduct(StoreProduct product) {

		File selectedDirectory = null;

		if (prefs.getBoolean(ApplicationDefaults.STORE_DIRECTORY_ENABLED_KEY, false)) {
			String storeDirectoryPath = prefs.get(ApplicationDefaults.STORE_DIRECTORY_KEY,null);
			if (storeDirectoryPath != null) {
				selectedDirectory = new File(prefs.get(ApplicationDefaults.STORE_DIRECTORY_KEY,""));
			}
		} else {
			DirectoryChooser directoryChooser = new DirectoryChooser();
			if (prefs.get(ApplicationDefaults.VST_DIRECTORY_KEY, null) != null) {
				File initialDirectory = new File(prefs.get(ApplicationDefaults.VST_DIRECTORY_KEY, ""));
				if (initialDirectory.isDirectory()) {
					directoryChooser.setInitialDirectory(new File(prefs.get(ApplicationDefaults.VST_DIRECTORY_KEY, "")));
				}
			}

			Window mainWindow = masonryPane.getScene().getWindow();
			selectedDirectory = directoryChooser.showDialog(mainWindow);
		}

		if (selectedDirectory != null) {
			storeService.install(product, selectedDirectory);
		} else {
			log.error("Invalid product installation directory");
		}

	}

	/**
	 * Requests masonry and scroll pane layout.
	 */
	public void requestLayout() {
		masonryPane.requestLayout();
		scrollPane.requestLayout();
	}

}
