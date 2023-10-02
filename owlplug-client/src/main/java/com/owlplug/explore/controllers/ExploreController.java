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
 
package com.owlplug.explore.controllers;

import com.google.common.collect.Iterables;
import com.owlplug.controls.Dialog;
import com.owlplug.controls.DialogLayout;
import com.owlplug.controls.MasonryPane;
import com.owlplug.controls.Popup;
import com.owlplug.controls.Rippler;
import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.components.ImageCache;
import com.owlplug.core.components.LazyViewRegistry;
import com.owlplug.core.controllers.BaseController;
import com.owlplug.core.controllers.MainController;
import com.owlplug.core.utils.FileUtils;
import com.owlplug.explore.components.ExploreTaskFactory;
import com.owlplug.explore.model.PackageBundle;
import com.owlplug.explore.model.RemotePackage;
import com.owlplug.explore.model.search.ExploreFilterCriteriaType;
import com.owlplug.explore.model.search.StoreFilterCriteria;
import com.owlplug.explore.services.ExploreService;
import com.owlplug.explore.ui.ExploreChipView;
import com.owlplug.explore.ui.PackageBlocViewBuilder;
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
import javafx.scene.control.Button;
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
public class ExploreController extends BaseController {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private static final int PARTITION_SIZE = 20;

  @Autowired
  private ExploreService exploreService;
  @Autowired
  private ImageCache imageCache;
  @Autowired
  private LazyViewRegistry viewRegistry;
  @Autowired
  private PackageInfoController packageInfoController;
  @Autowired
  private MainController mainController;
  @Autowired
  private ExploreTaskFactory exploreTaskFactory;

  @FXML
  private Button sourcesButton;
  @FXML
  private Button platformFilterButton;
  @FXML
  private Button syncSourcesButton;
  @FXML
  private Label resultCounter;
  @FXML
  private VBox masonryWrapper;
  @FXML
  private MasonryPane masonryPane;
  @FXML
  private ScrollPane scrollPane;
  @FXML
  private HBox lazyLoadBar;
  @FXML
  private Hyperlink lazyLoadLink;
  @FXML
  private Pane exploreChipViewContainer;
  
  private HashMap<String, CheckBox> targetFilterCheckBoxes = new HashMap<>();
  

  private ExploreChipView exploreChipView;
  private PackageBlocViewBuilder packageBlocViewBuilder = null;

  /**
   * Loaded products from store are displayed by partitions (like pagination).
   * When the user scrolls the entire partition, the next one is appended in the
   * UI.
   */
  private Iterable<List<RemotePackage>> loadedPackagePartitions;
  private Iterable<RemotePackage> loadedRemotePackages = new ArrayList<>();

  /**
   * Counter of loaded partitions on UI.
   */
  private int displayedPartitions = 0;

  /**
   * FXML initialize.
   */
  public void initialize() {

    packageBlocViewBuilder = new PackageBlocViewBuilder(this.getApplicationDefaults(), imageCache, this);

    sourcesButton.setOnAction(e -> {
      mainController.setLeftDrawer(viewRegistry.get(LazyViewRegistry.SOURCE_MENU_VIEW));
      mainController.getLeftDrawer().open();

    });
    
    targetFilterCheckBoxes.put("win32", new CheckBox("Windows 32 bits"));
    targetFilterCheckBoxes.put("win64", new CheckBox("Windows 64 bits"));
    targetFilterCheckBoxes.put("osx", new CheckBox("MacOS (OSX)"));
    targetFilterCheckBoxes.put("linux32", new CheckBox("Linux 32 bits"));
    targetFilterCheckBoxes.put("linux64", new CheckBox("Linux 64 bits"));
    for (Entry<String, CheckBox> entry : targetFilterCheckBoxes.entrySet()) {
      entry.getValue().setSelected(false);
      entry.getValue().setOnAction(e -> {
        performPackageSearch();
      });
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
      Popup popup = new Popup(vbx);
      popup.show(platformFilterButton, Popup.PopupVPosition.TOP, Popup.PopupHPosition.RIGHT);
    });

    syncSourcesButton.setOnAction(e -> {
      this.getAnalyticsService().pageView("/app/store/actions/syncStores");
      exploreTaskFactory.createSourceSyncTask().schedule();
    });

    exploreChipView = new ExploreChipView(this.getApplicationDefaults(), this.exploreService.getDistinctCreators());
    HBox.setHgrow(exploreChipView, Priority.ALWAYS);
    exploreChipViewContainer.getChildren().add(exploreChipView);

    exploreChipView.getChips().addListener((ListChangeListener<StoreFilterCriteria>) change -> {
      performPackageSearch();
    });

    scrollPane.vvalueProperty().addListener(new ChangeListener<Number>() {
      @Override
      public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        if (newValue.doubleValue() == 1) {
          displayNewPackagePartition();
        }
      }
    });

    lazyLoadLink.setOnAction(e -> {
      displayNewPackagePartition();
    });
    lazyLoadBar.setVisible(false);

    exploreTaskFactory.addSyncSourcesListener(() -> refreshView());
    refreshView();

    masonryPane.setHSpacing(5);
    masonryPane.setVSpacing(5);

    masonryPane.setCellHeight(130);
    masonryPane.setCellWidth(130);

  }
  
  private void performPackageSearch() {
    final List<StoreFilterCriteria> criteriaChipList = exploreChipView.getChips();
    List<StoreFilterCriteria> criteriaList = new ArrayList<>(criteriaChipList);
    
    for (Entry<String, CheckBox> entry : targetFilterCheckBoxes.entrySet()) {
      if (entry.getValue().isSelected()) {
        criteriaList.add(new StoreFilterCriteria(entry.getKey(), ExploreFilterCriteriaType.PLATFORM));
      }
    }

    Task<Iterable<RemotePackage>> task = new Task<Iterable<RemotePackage>>() {
      @Override
      protected Iterable<RemotePackage> call() throws Exception {
        return exploreService.getRemotePackages(criteriaList);
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
    Iterable<RemotePackage> remotePackages = exploreService.getRemotePackages(exploreChipView.getChips());
    refreshView(remotePackages);

  }

  /**
   * Display store products list.
   * 
   * @param remotePackages - Store product list
   */
  public synchronized void refreshView(Iterable<RemotePackage> remotePackages) {

    if (shouldRefreshPackages(remotePackages)) {
      this.masonryPane.getChildren().clear();
      this.masonryPane.requestLayout();

      loadedRemotePackages = remotePackages;
      loadedPackagePartitions = Iterables.partition(loadedRemotePackages, PARTITION_SIZE);
      displayedPartitions = 0;
      displayNewPackagePartition();
    }
  }

  private void displayNewPackagePartition() {

    if (Iterables.size(loadedPackagePartitions) > displayedPartitions) {
      for (RemotePackage remotePackage : Iterables.get(loadedPackagePartitions, displayedPartitions)) {
        Rippler rippler = new Rippler(packageBlocViewBuilder.build(remotePackage));
        rippler.setOnMouseClicked(e -> {
          if (e.getButton().equals(MouseButton.PRIMARY)) {
            selectPackage(remotePackage);
          }
        });
        masonryPane.getChildren().add(rippler);
      }
      displayedPartitions += 1;

      if (Iterables.size(loadedPackagePartitions) == displayedPartitions) {
        lazyLoadBar.setVisible(false);
      } else {
        lazyLoadBar.setVisible(true);

      }

      Platform.runLater(() -> {
        masonryPane.requestLayout();
        scrollPane.requestLayout();
      });
    }
    
    resultCounter.setText(this.masonryPane.getChildren().size() + " / " + Iterables.size(this.loadedRemotePackages));

  }

  /**
   * Returns true if the given package list is different from the previously
   * loaded package list.
   * 
   * @param newPackages - the new package list
   * @return
   */
  private boolean shouldRefreshPackages(Iterable<RemotePackage> newPackages) {

    if (Iterables.size(newPackages) != Iterables.size(loadedRemotePackages)) {
      return true;
    }

    for (int i = 0; i < Iterables.size(newPackages); i++) {
      if (!Iterables.get(newPackages, i).getId().equals(Iterables.get(loadedRemotePackages, i).getId())) {
        return true;
      }
    }

    return false;
  }

  /**
   * Displays full package information.
   * 
   * @param remotePackage - package
   */
  public void selectPackage(RemotePackage remotePackage) {
    packageInfoController.setPackage(remotePackage);
    packageInfoController.show();
  }

  /**
   * Trigger bundle installation sequence.
   * 
   * @param bundle Bundle to install
   */
  public boolean installBundle(PackageBundle bundle) {
    
    this.getAnalyticsService().pageView("app/store/action/install", 
        bundle.getRemotePackage().getRemoteSource().getName(),
        bundle.getRemotePackage().getName(),
        bundle.getName());


    File selectedDirectory = null;
    String baseDirectoryPath = exploreService.getBundleInstallFolder(bundle);

    // A custom root directory to store plugin is defined and the base directory for
    // the bundle type is defined or not blank.
    if (this.getPreferences().getBoolean(ApplicationDefaults.STORE_DIRECTORY_ENABLED_KEY, false) &&
      baseDirectoryPath != null && !baseDirectoryPath.isBlank()) {
      // Store install target is already defined

      String relativeDirectoryPath  = this.getPreferences().get(ApplicationDefaults.STORE_DIRECTORY_KEY, "");
      Boolean shouldGroupByCreator = this.getPreferences().getBoolean(ApplicationDefaults.STORE_BY_CREATOR_ENABLED_KEY, false);

      //if the enduser wishes to group plugins by their creator,
      //then we need to include the subdirectory as well.
      if (shouldGroupByCreator) {
        String creator = FileUtils.sanitizeFileName(bundle.getRemotePackage().getCreator());
        relativeDirectoryPath = relativeDirectoryPath + File.separator + creator;
      }

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
          FileUtils.sanitizeFileName(bundle.getRemotePackage().getName()));
      // If directory exists, asks the user for overwrite permission
      if (subSelectedDirectory.exists()) {
        Dialog dialog = this.getDialogManager().newDialog();

        DialogLayout layout = new DialogLayout();

        layout.setHeading(new Label("Remove plugin"));
        layout.setBody(new Label("A previous installation of " + bundle.getRemotePackage().getName()
            + " exists. Do you want to overwrite it ? \nOnly files in conflict will be replaced."));

        Button cancelButton = new Button("No, do nothing");
        cancelButton.setOnAction(cancelEvent -> {
          dialog.close();
        });

        Button overwriteButton = new Button("Yes, overwrite");
        overwriteButton.setOnAction(removeEvent -> {
          dialog.close();
          exploreTaskFactory.createBundleInstallTask(bundle, subSelectedDirectory).schedule();

        });
        overwriteButton.getStyleClass().add("button-danger");

        layout.setActions(overwriteButton, cancelButton);
        dialog.setContent(layout);
        dialog.show();
        return false;
      } else {
        // Plugin can be installed in the subdirectory (no conflicts)
        exploreTaskFactory.createBundleInstallTask(bundle, subSelectedDirectory).schedule();
        return true;
      }
    }
    // If a target directory has been previously found, start install tasks
    exploreTaskFactory.createBundleInstallTask(bundle, selectedDirectory).schedule();
    return true;

  }

  /**
   * Trigger package installation. The best bundle will be selected based on the
   * current user platform.
   * 
   * @param remotePackage Package to install
   */
  public boolean installProduct(RemotePackage remotePackage) {

    PackageBundle bundle = exploreService.findBestBundle(remotePackage);
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
