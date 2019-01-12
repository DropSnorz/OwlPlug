package com.dropsnorz.owlplug.store.controllers;

import com.dropsnorz.owlplug.core.components.ApplicationDefaults;
import com.dropsnorz.owlplug.core.components.ImageCache;
import com.dropsnorz.owlplug.core.components.LazyViewRegistry;
import com.dropsnorz.owlplug.core.components.TaskFactory;
import com.dropsnorz.owlplug.core.controllers.MainController;
import com.dropsnorz.owlplug.core.controllers.dialogs.DialogController;
import com.dropsnorz.owlplug.core.utils.FileUtils;
import com.dropsnorz.owlplug.store.model.ProductBundle;
import com.dropsnorz.owlplug.store.model.StoreProduct;
import com.dropsnorz.owlplug.store.model.search.StoreFilterCriteria;
import com.dropsnorz.owlplug.store.service.StoreService;
import com.dropsnorz.owlplug.store.ui.StoreChipView;
import com.dropsnorz.owlplug.store.ui.StoreProductBlocViewBuilder;
import com.google.common.collect.Iterables;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXMasonryPane;
import com.jfoenix.controls.JFXRippler;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
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
  private ProductInfoController productInfoController;
  @Autowired
  private MainController mainController;
  @Autowired
  private DialogController dialogController;
  @Autowired
  private TaskFactory taskFactory;

  @FXML
  private JFXButton storesButton;
  @FXML
  private JFXButton syncStoreButton;
  @FXML
  private Label resultCounter;
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
  @FXML
  private Pane storeChipViewContainer;

  private StoreChipView storeChipView;
  private StoreProductBlocViewBuilder storeProductBlocViewBuilder = null;

  /**
   * Loaded products from store are displayed by partitions (like pagination).
   * When the user scrolls the entire partition, the next one is appended in the
   * UI.
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

    storeProductBlocViewBuilder = new StoreProductBlocViewBuilder(applicationDefaults, imageCache, this);

    storesButton.setOnAction(e -> {
      mainController.setLeftDrawer(viewRegistry.get(LazyViewRegistry.STORE_MENU_VIEW));
      mainController.getLeftDrawer().open();

    });

    syncStoreButton.setOnAction(e -> {
      storeService.syncStores();
    });

    storeChipView = new StoreChipView(applicationDefaults);
    HBox.setHgrow(storeChipView, Priority.ALWAYS);
    storeChipViewContainer.getChildren().add(storeChipView);

    storeChipView.getChips().addListener((ListChangeListener) change -> {
      final List<StoreFilterCriteria> criteriaList = storeChipView.getChips();
      log.debug("Hi there");

      Task<Iterable<StoreProduct>> task = new Task<Iterable<StoreProduct>>() {
        @Override
        protected Iterable<StoreProduct> call() throws Exception {
          return storeService.getStoreProducts(criteriaList);
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

    masonryPane.setHSpacing(5);
    masonryPane.setVSpacing(5);

    masonryPane.setCellHeight(130);
    masonryPane.setCellWidth(130);

  }

  /**
   * Refresh Store View.
   */
  public synchronized void refreshView() {
    Iterable<StoreProduct> newProducts = storeService.getStoreProducts(storeChipView.getChips());
    refreshView(newProducts);

  }

  /**
   * Display store products list.
   * 
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
        rippler.setOnMouseClicked(e -> {
          if (e.getButton().equals(MouseButton.PRIMARY)) {
            selectProduct(product);
          }
        });
        masonryPane.getChildren().add(rippler);
      }
      displayedPartitions += 1;

      if (Iterables.size(loadedProductPartitions) == displayedPartitions) {
        lazyLoadBar.setVisible(false);
      } else {
        lazyLoadBar.setVisible(true);

      }

      resultCounter.setText(this.masonryPane.getChildren().size() + " / " + Iterables.size(this.loadedStoreProducts));

      Platform.runLater(() -> {
        masonryPane.requestLayout();
        scrollPane.requestLayout();
      });
    }

  }

  /**
   * Returns true if the given product list is different from the previously
   * loaded product list.
   * 
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
   * Displays full product informations.
   * 
   * @param product - product
   */
  public void selectProduct(StoreProduct product) {
    productInfoController.setProduct(product);
    productInfoController.show();
  }

  /**
   * Trigger bundle installation sequence.
   * 
   * @param bundle Bundle to install
   */
  public void installBundle(ProductBundle bundle) {

    File selectedDirectory = null;

    if (prefs.getBoolean(ApplicationDefaults.STORE_DIRECTORY_ENABLED_KEY, false)) {
      // Store install target is already defined
      String storeDirectoryPath = prefs.get(ApplicationDefaults.STORE_DIRECTORY_KEY, null);
      if (storeDirectoryPath != null) {
        selectedDirectory = new File(prefs.get(ApplicationDefaults.STORE_DIRECTORY_KEY, ""));
      }
    } else {
      // Open dialog chooser to define store installation target
      DirectoryChooser directoryChooser = new DirectoryChooser();
      if (prefs.get(ApplicationDefaults.VST_DIRECTORY_KEY, null) != null) {
        File initialDirectory = new File(prefs.get(ApplicationDefaults.VST_DIRECTORY_KEY, ""));
        if (initialDirectory.isDirectory()) {
          directoryChooser.setInitialDirectory(new File(prefs.get(ApplicationDefaults.VST_DIRECTORY_KEY, "")));
        }
      }
      // Open directory chooser on top of the current windows
      Window mainWindow = masonryPane.getScene().getWindow();
      selectedDirectory = directoryChooser.showDialog(mainWindow);
    }

    if (prefs.getBoolean(ApplicationDefaults.STORE_SUBDIRECTORY_ENABLED, true)) {
      // If the plugin is wrapped into a subdirectory, checks for already existing
      // directory
      File subSelectedDirectory = new File(selectedDirectory,
          FileUtils.sanitizeFileName(bundle.getProduct().getName()));
      // If directory exists, asks the user for overwrite permission
      if (subSelectedDirectory.exists()) {
        JFXDialog dialog = dialogController.newDialog();

        JFXDialogLayout layout = new JFXDialogLayout();

        layout.setHeading(new Label("Remove plugin"));
        layout.setBody(new Label("A previous installation of " + bundle.getProduct().getName()
            + " exists. Do you want to overwrite it ? \nOnly files in conflict will be replaced."));

        JFXButton cancelButton = new JFXButton("No, do nothing");
        cancelButton.setOnAction(cancelEvent -> {
          dialog.close();
        });

        JFXButton overwriteButton = new JFXButton("Yes, overwrite.");
        overwriteButton.setOnAction(removeEvent -> {
          dialog.close();
          storeService.install(bundle, subSelectedDirectory);

        });
        overwriteButton.getStyleClass().add("button-danger");

        layout.setActions(overwriteButton, cancelButton);
        dialog.setContent(layout);
        dialog.show();
      } else {
        // If a plugin will be installed in a new directory we can trigger installation
        // task
        storeService.install(bundle, subSelectedDirectory);
      }
    } else if (selectedDirectory != null) {
      // If a target directory has been previously found, start install tasks
      storeService.install(bundle, selectedDirectory);
    } else {
      log.error("Invalid product installation directory");
    }

  }

  /**
   * Trigger product installation. The best bundle will be selected based on the
   * current user platform.
   * 
   * @param product Product to install
   */
  public void installProduct(StoreProduct product) {

    ProductBundle bundle = storeService.findBestBundle(product);
    if (bundle != null) {
      installBundle(bundle);
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
