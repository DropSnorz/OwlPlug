/* OwlPlug
 * Copyright (C) 2021 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 
 * as published by the Free Software Foundation.
 *
 * OwlPlug is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OwlPlug.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package com.owlplug.store.controllers;

import com.google.common.collect.Iterables;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXMasonryPane;
import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXPopup.PopupHPosition;
import com.jfoenix.controls.JFXPopup.PopupVPosition;
import com.jfoenix.controls.JFXRippler;
import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.components.ImageCache;
import com.owlplug.core.components.LazyViewRegistry;
import com.owlplug.core.controllers.BaseController;
import com.owlplug.core.controllers.MainController;
import com.owlplug.core.model.platform.RuntimePlatform;
import com.owlplug.core.utils.FileUtils;
import com.owlplug.store.components.StoreTaskFactory;
import com.owlplug.store.model.ProductBundle;
import com.owlplug.store.model.StoreProduct;
import com.owlplug.store.model.search.StoreFilterCriteria;
import com.owlplug.store.model.search.StoreFilterCriteriaType;
import com.owlplug.store.services.StoreService;
import com.owlplug.store.ui.StoreChipView;
import com.owlplug.store.ui.StoreProductBlocViewBuilder;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
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
public class StoreController extends BaseController {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private static final int PARTITION_SIZE = 20;

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
  private StoreTaskFactory storeTaskFactory;

  @FXML
  private JFXButton storesButton;
  @FXML
  private JFXButton platformFilterButton;
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
  
  private HashMap<String, CheckBox> targetFilterCheckBoxes = new HashMap<>();
  

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

    storeProductBlocViewBuilder = new StoreProductBlocViewBuilder(this.getApplicationDefaults(), imageCache, this);

    storesButton.setOnAction(e -> {
      mainController.setLeftDrawer(viewRegistry.get(LazyViewRegistry.STORE_MENU_VIEW));
      mainController.getLeftDrawer().open();

    });
    
    targetFilterCheckBoxes.put("win32", new JFXCheckBox("Windows 32 bits"));
    targetFilterCheckBoxes.put("win64", new JFXCheckBox("Windows 64 bits"));
    targetFilterCheckBoxes.put("osx", new JFXCheckBox("OSX (any)"));
    for (Entry<String, CheckBox> entry : targetFilterCheckBoxes.entrySet()) {
      entry.getValue().setDisable(true);
      entry.getValue().setSelected(false);
      entry.getValue().setOnAction(e -> {
        performProductSearch();
      });
    }
    
    RuntimePlatform env = this.getApplicationDefaults().getRuntimePlatform();
    for (String tag : env.getCompatiblePlatformsTags()) {
      if (targetFilterCheckBoxes.containsKey(tag)) {
        targetFilterCheckBoxes.get(tag).setDisable(false);
        targetFilterCheckBoxes.get(tag).setSelected(false);
      }
    }
    
    VBox vbx = new VBox();
    vbx.setSpacing(5);
    vbx.setPadding(new Insets(5,10,5,10));
    Label popupLabel = new Label("Target environment contains");
    popupLabel.getStyleClass().add("label-disabled");
    vbx.getChildren().add(popupLabel);
    for (Entry<String, CheckBox> entry : targetFilterCheckBoxes.entrySet()) {
      vbx.getChildren().add(entry.getValue());
    }

    platformFilterButton.setOnAction(e -> {
      JFXPopup popup = new JFXPopup(vbx);
      popup.show(platformFilterButton, PopupVPosition.TOP, PopupHPosition.RIGHT);
    });

    syncStoreButton.setOnAction(e -> {
      this.getAnalyticsService().pageView("/app/store/actions/syncStores");
      storeTaskFactory.createStoreSyncTask().schedule();
    });

    storeChipView = new StoreChipView(this.getApplicationDefaults(), this.storeService.getDistinctCreators());
    HBox.setHgrow(storeChipView, Priority.ALWAYS);
    storeChipViewContainer.getChildren().add(storeChipView);

    storeChipView.getChips().addListener((ListChangeListener) change -> {
      performProductSearch();
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

    storeTaskFactory.addSyncStoresListener(() -> refreshView());
    refreshView();

    masonryPane.setHSpacing(5);
    masonryPane.setVSpacing(5);

    masonryPane.setCellHeight(130);
    masonryPane.setCellWidth(130);

  }
  
  private void performProductSearch() {
    final List<StoreFilterCriteria> criteriaChipList = storeChipView.getChips();
    List<StoreFilterCriteria> criteriaList = new ArrayList<>(criteriaChipList);
    
    for (Entry<String, CheckBox> entry : targetFilterCheckBoxes.entrySet()) {
      if (entry.getValue().isSelected()) {
        criteriaList.add(new StoreFilterCriteria(entry.getKey(), StoreFilterCriteriaType.PLATFORM));
      }
    }

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
    
    this.getAnalyticsService().pageView("/app/store/action/search");

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

      Platform.runLater(() -> {
        masonryPane.requestLayout();
        scrollPane.requestLayout();
      });
    }
    
    resultCounter.setText(this.masonryPane.getChildren().size() + " / " + Iterables.size(this.loadedStoreProducts));

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
  public boolean installBundle(ProductBundle bundle) {
    
    this.getAnalyticsService().pageView("app/store/action/install", 
        bundle.getProduct().getStore().getName(), 
        bundle.getProduct().getName(), 
        bundle.getName());

    
    String baseDirectoryPath = storeService.getBundleInstallFolder(bundle);
    String relativeDirectoryPath  = this.getPreferences().get(ApplicationDefaults.STORE_DIRECTORY_KEY, "");

    Boolean shouldGroupByCreator = this.getPreferences().getBoolean(ApplicationDefaults.STORE_BY_CREATOR_ENABLED_KEY, false);

    //if the enduser wishes to group plugins by their creator,
    //then we need to include the subdirectory as well.
    if(shouldGroupByCreator) {
        String creator = FileUtils.sanitizeFileName(bundle.getProduct().getCreator());
        relativeDirectoryPath = relativeDirectoryPath + File.separator + creator;
    }

    File selectedDirectory = null;

    // A custom root directory to store plugin is defined
    if (this.getPreferences().getBoolean(ApplicationDefaults.STORE_DIRECTORY_ENABLED_KEY, false)) {
      // Store install target is already defined

      selectedDirectory = new File(baseDirectoryPath, relativeDirectoryPath);
      
      // A plugin root directory is not defined
    } else {
      // Open dialog chooser to define store installation target
      DirectoryChooser directoryChooser = new DirectoryChooser();
      // Open the VST directory
      File initialDirectory = new File(baseDirectoryPath);
      if (initialDirectory.isDirectory()) {
        directoryChooser.setInitialDirectory(initialDirectory);
      }
      // Open directory chooser on top of the current windows
      Window mainWindow = masonryPane.getScene().getWindow();
      selectedDirectory = directoryChooser.showDialog(mainWindow);
    }

    
    
    // If any install target directory can be found, abort install
    if (selectedDirectory == null 
        || (selectedDirectory.exists() && !selectedDirectory.isDirectory())) {
      log.error("Install directory can't be found: " + selectedDirectory);
      return false;
    }

    // Plugin should be wrapped in a subdirectory
    if (this.getPreferences().getBoolean(ApplicationDefaults.STORE_SUBDIRECTORY_ENABLED, true)) {
      // If the plugin is wrapped into a subdirectory, checks for already existing
      // directory
      File subSelectedDirectory = new File(selectedDirectory,
          FileUtils.sanitizeFileName(bundle.getProduct().getName()));
      // If directory exists, asks the user for overwrite permission
      if (subSelectedDirectory.exists()) {
        JFXDialog dialog = this.getDialogController().newDialog();

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
          storeTaskFactory.createBundleInstallTask(bundle, subSelectedDirectory).schedule();

        });
        overwriteButton.getStyleClass().add("button-danger");

        layout.setActions(overwriteButton, cancelButton);
        dialog.setContent(layout);
        dialog.show();
        return false;
      } else {
        // Plugin can be installed in the subdirectory (no conflicts)
        storeTaskFactory.createBundleInstallTask(bundle, subSelectedDirectory).schedule();
        return true;
      }
    }
    // If a target directory has been previously found, start install tasks
    storeTaskFactory.createBundleInstallTask(bundle, selectedDirectory).schedule();;
    return true;

  }

  /**
   * Trigger product installation. The best bundle will be selected based on the
   * current user platform.
   * 
   * @param product Product to install
   */
  public boolean installProduct(StoreProduct product) {

    ProductBundle bundle = storeService.findBestBundle(product);
    if (bundle != null) {
      return installBundle(bundle);
    }

    return false;
  }

  /**
   * Requests masonry and scroll pane layout.
   */
  public void requestLayout() {
    masonryPane.requestLayout();
    scrollPane.requestLayout();
  }

}
