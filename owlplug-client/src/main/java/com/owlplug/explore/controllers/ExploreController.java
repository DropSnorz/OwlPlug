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
import com.owlplug.explore.controllers.dialogs.InstallStepDialogController;
import com.owlplug.explore.model.PackageBundle;
import com.owlplug.explore.model.RemotePackage;
import com.owlplug.explore.model.search.ExploreFilterCriteria;
import com.owlplug.explore.model.search.ExploreFilterCriteriaType;
import com.owlplug.explore.services.ExploreService;
import com.owlplug.explore.ui.ExploreChipView;
import com.owlplug.explore.ui.PackageBlocViewBuilder;
import com.owlplug.plugin.model.PluginFormat;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import javafx.application.Platform;
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
import javafx.scene.text.Text;
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
  private InstallStepDialogController installStepDialogController;
  @Autowired
  private ExploreTaskFactory exploreTaskFactory;

  @FXML
  private Button sourcesButton;
  @FXML
  private Button formatFilterButton;
  @FXML
  private Button platformFilterButton;
  @FXML
  private Button syncSourcesButton;
  @FXML
  private Text resultCounter;
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
  
  private final HashMap<String, CheckBox> targetFilterCheckBoxes = new HashMap<>();

  private final HashMap<String, CheckBox> formatsFilterCheckBoxes = new HashMap<>();
  

  private ExploreChipView exploreChipView;
  private PackageBlocViewBuilder packageBlocViewBuilder = null;

  /**
   * Loaded packages from remote sources are displayed by partitions (like pagination).
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

    for (PluginFormat format : PluginFormat.values()) {
      CheckBox checkbox = new CheckBox(format.getText());
      formatsFilterCheckBoxes.put(format.getText().toLowerCase(), checkbox);
      checkbox.setSelected(false);
      checkbox.setOnAction(e -> {
        performPackageSearch();
      });
    }

    VBox formatFilterVbox = new VBox();
    formatFilterVbox.setSpacing(5);
    formatFilterVbox.setPadding(new Insets(5,10,5,10));
    Label formatLabel = new Label("Plugin format");
    formatLabel.getStyleClass().add("label-disabled");
    formatFilterVbox.getChildren().add(formatLabel);
    for (Entry<String, CheckBox> entry : formatsFilterCheckBoxes.entrySet()) {
      formatFilterVbox.getChildren().add(entry.getValue());
    }

    formatFilterButton.setOnAction(e -> {
      Popup popup = new Popup(formatFilterVbox);
      popup.show(formatFilterButton, Popup.PopupVPosition.TOP, Popup.PopupHPosition.RIGHT);
    });

    
    targetFilterCheckBoxes.put("win-x32", new CheckBox("Windows x32"));
    targetFilterCheckBoxes.put("win-x64", new CheckBox("Windows x64"));
    targetFilterCheckBoxes.put("mac-x64", new CheckBox("MacOS x64"));
    targetFilterCheckBoxes.put("mac-arm64", new CheckBox("MacOS arm64"));
    targetFilterCheckBoxes.put("linux-x32", new CheckBox("Linux x32 / amd32"));
    targetFilterCheckBoxes.put("linux-x64", new CheckBox("Linux x64 / amd64"));
    targetFilterCheckBoxes.put("linux-arm32", new CheckBox("Linux arm32"));
    targetFilterCheckBoxes.put("linux-arm64", new CheckBox("Linux arm64"));
    for (Entry<String, CheckBox> entry : targetFilterCheckBoxes.entrySet()) {
      Set<String> preselected = this.getApplicationDefaults().getRuntimePlatform().getCompatiblePlatformsTags();
      entry.getValue().setSelected(preselected.contains(entry.getKey()));
      entry.getValue().setOnAction(e -> {
        performPackageSearch();
      });
    }
    
    VBox platformFilterVbox = new VBox();
    platformFilterVbox.setSpacing(5);
    platformFilterVbox.setPadding(new Insets(5,10,5,10));
    Label popupLabel = new Label("Target environment contains");
    popupLabel.getStyleClass().add("label-disabled");
    platformFilterVbox.getChildren().add(popupLabel);
    for (Entry<String, CheckBox> entry : targetFilterCheckBoxes.entrySet()
            .stream()
            .sorted(Entry.<String, CheckBox>comparingByKey().reversed())
            .toList()
    ) {
      platformFilterVbox.getChildren().add(entry.getValue());
    }

    platformFilterButton.setOnAction(e -> {
      Popup popup = new Popup(platformFilterVbox);
      popup.show(platformFilterButton, Popup.PopupVPosition.TOP, Popup.PopupHPosition.RIGHT);
    });

    syncSourcesButton.setOnAction(e -> {
      this.getTelemetryService().event("/Explore/SyncSources");
      exploreTaskFactory.createSourceSyncTask().schedule();
    });

    exploreChipView = new ExploreChipView(this.getApplicationDefaults(), this.exploreService.getDistinctCreators());
    HBox.setHgrow(exploreChipView, Priority.ALWAYS);
    exploreChipViewContainer.getChildren().add(exploreChipView);

    exploreChipView.getChips().addListener((ListChangeListener<ExploreFilterCriteria>) change -> {
      performPackageSearch();
    });

    scrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue.doubleValue() == 1) {
        displayNewPackagePartition();
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
    final List<ExploreFilterCriteria> criteriaChipList = exploreChipView.getChips();
    List<ExploreFilterCriteria> criteriaList = new ArrayList<>(criteriaChipList);

    List<String> targets = new ArrayList<>();
    for (Entry<String, CheckBox> entry : targetFilterCheckBoxes.entrySet()) {
      if (entry.getValue().isSelected()) {
        targets.add(entry.getKey());
      }
    }
    if (targets.size() > 0) {
      criteriaList.add(new ExploreFilterCriteria(targets, ExploreFilterCriteriaType.PLATFORM_LIST));
    }

    List<String> formats = new ArrayList<>();
    for (Entry<String, CheckBox> entry : formatsFilterCheckBoxes.entrySet()) {
      if (entry.getValue().isSelected()) {
        formats.add(entry.getKey());
      }
    }
    if (formats.size() > 0) {
      criteriaList.add(new ExploreFilterCriteria(formats, ExploreFilterCriteriaType.FORMAT_LIST));
    }

    Task<Iterable<RemotePackage>> task = new Task<>() {
      @Override
      protected Iterable<RemotePackage> call() throws Exception {
        return exploreService.getRemotePackages(criteriaList);
      }
    };
    task.setOnSucceeded(e -> {
      displayPackages(task.getValue());
    });
    new Thread(task).start();

  }

  /**
   * Refresh Store View.
   */
  public synchronized void refreshView() {
    performPackageSearch();

  }

  /**
   * Display remote source package list.
   * 
   * @param remotePackages - Remote package list
   */
  public synchronized void displayPackages(Iterable<RemotePackage> remotePackages) {

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

  public void addSearchChip(String chip) {
    if (chip != null && !chip.trim().isEmpty()) {
      exploreChipView.getChips().add(exploreChipView.getConverter().fromString(chip));
    }
  }

  /**
   * Trigger package installation. The best bundle will be selected based on the
   * current user platform.
   * 
   * @param remotePackage Package to install
   */
  public boolean installPackage(RemotePackage remotePackage) {
    PackageBundle bundle = exploreService.findBestBundle(remotePackage);
    return installBundle(bundle);
  }

  /**
   * Trigger bundle installation.
   *
   * @param bundle Package to install
   */
  public boolean installBundle(PackageBundle bundle) {
    if (bundle != null) {
      this.getTelemetryService().event("/Explore/Install", p -> {
        p.put("source", bundle.getRemotePackage().getRemoteSource().getName());
        p.put("package", bundle.getRemotePackage().getName());
        p.put("bundle", bundle.getName());
      });
      installStepDialogController.install(bundle);
      return true;
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
