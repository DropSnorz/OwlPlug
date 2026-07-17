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
import com.owlplug.controls.MasonryPane;
import com.owlplug.controls.Popup;
import com.owlplug.controls.Rippler;
import com.owlplug.core.components.ImageCache;
import com.owlplug.core.components.LazyViewRegistry;
import com.owlplug.core.controllers.BaseController;
import com.owlplug.core.controllers.MainController;
import com.owlplug.core.utils.Async;
import com.owlplug.core.utils.FX;
import com.owlplug.explore.components.ExploreTaskFactory;
import com.owlplug.explore.controllers.dialogs.InstallStepDialogController;
import com.owlplug.explore.events.RemoteSourceUpdatedEvent;
import com.owlplug.explore.events.SourceSyncEvent;
import com.owlplug.explore.model.PackageBundle;
import com.owlplug.explore.model.RemotePackage;
import com.owlplug.explore.model.search.ExploreFilterCriteria;
import com.owlplug.explore.model.search.ExploreFilterCriteriaType;
import com.owlplug.explore.services.ExploreService;
import com.owlplug.explore.ui.ExploreChipView;
import com.owlplug.explore.ui.PackageBlocViewBuilder;
import com.owlplug.explore.ui.PackageListRowCellFactory;
import com.owlplug.plugin.model.PluginFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;

@Controller
public class ExploreController extends BaseController {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private static final int PARTITION_SIZE = 20;

  /** Number of packages fetched from the database per remote call, used to bound query size. */
  private static final int DB_PAGE_SIZE = 100;

  @Autowired
  private ExploreService exploreService;
  @Autowired
  private ImageCache imageCache;
  @Autowired
  private LazyViewRegistry viewRegistry;
  @Autowired
  private PackageInfoController packageInfoController;
  @Autowired
  @Lazy
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
  private TabPane displaySwitchTabPane;
  @FXML
  private Tab displayGridTab;
  @FXML
  private Tab displayListTab;
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
  private StackPane listWrapper;
  @FXML
  private ListView<RemotePackage> packageListView;
  @FXML
  private HBox listLazyLoadBar;
  @FXML
  private Hyperlink listLazyLoadLink;
  @FXML
  private Pane exploreChipViewContainer;

  private final HashMap<String, CheckBox> targetFilterCheckBoxes = new HashMap<>();
  private final HashMap<String, CheckBox> formatsFilterCheckBoxes = new HashMap<>();

  private ExploreChipView exploreChipView;
  private PackageBlocViewBuilder packageBlocViewBuilder = null;

  /**
   * Loaded packages from remote sources are displayed by partitions (like pagination).
   * When the user scrolls the entire partition, the next one is appended in the UI.
   */
  private Iterable<List<RemotePackage>> loadedPackagePartitions;
  private List<RemotePackage> loadedRemotePackages = new ArrayList<>();

  /** Total number of packages matching the current search, as reported by the database. */
  private long totalRemotePackages = 0;

  /** Criteria of the currently displayed search, reused when fetching further database pages. */
  private List<ExploreFilterCriteria> currentCriteriaList = new ArrayList<>();

  /** Next database page to fetch when the user scrolls past all currently loaded packages. */
  private int nextDbPage = 0;

  /** Guards against firing overlapping "load next page" database calls. */
  private boolean fetchingNextDbPage = false;

  /**
   * Drops results from a search or "load next page" call superseded by a more recent one
   * (e.g. rapid filter changes), so a slow, stale database call can never overwrite fresher data.
   */
  private final Async.Sequence searchSequence = new Async.Sequence();

  /** Counter of loaded partitions on UI — masonry view. */
  private int displayedPartitions = 0;

  /** Backing items of the virtualized list view — kept in sync with {@link #loadedRemotePackages}. */
  private final ObservableList<RemotePackage> listViewItems = FXCollections.observableArrayList();

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
      checkbox.setOnAction(e -> performPackageSearch());
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
    targetFilterCheckBoxes.put("win-arm64", new CheckBox("Windows arm64"));
    targetFilterCheckBoxes.put("win-arm64ec", new CheckBox("Windows arm64 E.C."));
    targetFilterCheckBoxes.put("mac-x64", new CheckBox("MacOS x64"));
    targetFilterCheckBoxes.put("mac-arm64", new CheckBox("MacOS arm64"));
    targetFilterCheckBoxes.put("linux-x32", new CheckBox("Linux x32 / amd32"));
    targetFilterCheckBoxes.put("linux-x64", new CheckBox("Linux x64 / amd64"));
    targetFilterCheckBoxes.put("linux-arm32", new CheckBox("Linux arm32"));
    targetFilterCheckBoxes.put("linux-arm64", new CheckBox("Linux arm64"));
    for (Entry<String, CheckBox> entry : targetFilterCheckBoxes.entrySet()) {
      Set<String> preselected = this.getApplicationDefaults().getRuntimePlatform().getCompatiblePlatformsTags();
      entry.getValue().setSelected(preselected.contains(entry.getKey()));
      entry.getValue().setOnAction(e -> performPackageSearch());
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

    // Grid view scroll → load next partition
    scrollPane.vvalueProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue.doubleValue() == 1) {
        displayNewPackagePartition();
      }
    });
    lazyLoadLink.setOnAction(e -> displayNewPackagePartition());
    lazyLoadBar.setVisible(false);

    // List view — virtualized ListView backed by listViewItems
    packageListView.setItems(listViewItems);
    packageListView.setCellFactory(new PackageListRowCellFactory(this.getApplicationDefaults(), imageCache, this));
    packageListView.getSelectionModel().selectedItemProperty().addListener((observable, oldPkg, newPkg) -> {
      if (newPkg != null) {
        selectPackage(newPkg);
      }
    });
    // Load next database page when the user scrolls the ListView to its bottom.
    packageListView.skinProperty().addListener((observable, oldSkin, newSkin) -> {
      if (newSkin != null) {
        FX.run(() -> {
          ScrollBar bar = (ScrollBar) packageListView.lookup(".scroll-bar:vertical");
          if (bar != null) {
            bar.valueProperty().addListener((o, oldValue, newValue) -> {
              if (bar.getMax() > 0 && newValue.doubleValue() >= bar.getMax()) {
                loadMoreListItems();
              }
            });
          }
        });
      }
    });
    listLazyLoadLink.setOnAction(e -> loadMoreListItems());
    listLazyLoadBar.setVisible(false);
    listLazyLoadBar.setManaged(false);

    displaySwitchTabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
      boolean listActive = newTab.equals(displayListTab);
      scrollPane.setVisible(!listActive);
      scrollPane.setManaged(!listActive);
      listWrapper.setVisible(listActive);
      listWrapper.setManaged(listActive);
      if (!listActive) {
        FX.run(() -> {
          masonryPane.requestLayout();
          scrollPane.requestLayout();
        });
      }
    });
    displaySwitchTabPane.getSelectionModel().select(displayListTab);

    performPackageSearch();

    masonryPane.setHSpacing(5);
    masonryPane.setVSpacing(5);
    masonryPane.setCellHeight(130);
    masonryPane.setCellWidth(130);
  }

  private synchronized void performPackageSearch() {
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

    currentCriteriaList = criteriaList;
    // A new search supersedes any "load next page" call from the previous search.
    fetchingNextDbPage = false;

    searchSequence.supply(() -> exploreService.getRemotePackages(criteriaList, PageRequest.of(0, DB_PAGE_SIZE)))
        .thenAccept(page -> FX.run(() -> {
          nextDbPage = 1;
          displayPackages(page);
        }));
  }

  /**
   * Display the first page of a remote source package search result.
   *
   * @param packagePage - First page of remote packages
   */
  public synchronized void displayPackages(Page<RemotePackage> packagePage) {
    if (shouldRefreshPackages(packagePage)) {
      this.masonryPane.getChildren().clear();
      this.masonryPane.requestLayout();

      loadedRemotePackages = new ArrayList<>(packagePage.getContent());
      totalRemotePackages = packagePage.getTotalElements();
      loadedPackagePartitions = Iterables.partition(loadedRemotePackages, PARTITION_SIZE);
      displayedPartitions = 0;
      displayNewPackagePartition();
      syncListViewItems();
    }
  }

  /**
   * Fetches the next database page in the background and appends it to {@link #loadedRemotePackages}
   * once available, then runs the given continuation to resume displaying partitions.
   */
  private void fetchNextDbPage(Runnable onLoaded) {
    if (fetchingNextDbPage) {
      return;
    }
    fetchingNextDbPage = true;
    final int pageToFetch = nextDbPage;
    final List<ExploreFilterCriteria> criteriaList = currentCriteriaList;

    searchSequence.supply(() -> exploreService.getRemotePackages(criteriaList, PageRequest.of(pageToFetch, DB_PAGE_SIZE)))
        .thenAccept(page -> FX.run(() -> {
          fetchingNextDbPage = false;
          loadedRemotePackages.addAll(page.getContent());
          totalRemotePackages = page.getTotalElements();
          nextDbPage += 1;
          onLoaded.run();
        }))
        .exceptionally(ex -> {
          FX.run(() -> fetchingNextDbPage = false);
          return null;
        });
  }

  private boolean hasMoreRemotePackages() {
    return loadedRemotePackages.size() < totalRemotePackages;
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

      boolean allLoaded = Iterables.size(loadedPackagePartitions) == displayedPartitions && !hasMoreRemotePackages();
      lazyLoadBar.setVisible(!allLoaded);

      FX.run(() -> {
        masonryPane.requestLayout();
        scrollPane.requestLayout();
      });
    } else if (hasMoreRemotePackages()) {
      fetchNextDbPage(this::displayNewPackagePartition);
      return;
    }

    refreshResultCounter();
  }

  /**
   * Syncs the virtualized list view's items with {@link #loadedRemotePackages}. Unlike the masonry
   * view, ListView virtualizes cells regardless of item count, so the full list is synced in one call
   * instead of being revealed in {@link #PARTITION_SIZE}-sized chunks.
   */
  private void syncListViewItems() {
    listViewItems.setAll(loadedRemotePackages);
    boolean hasMore = hasMoreRemotePackages();
    listLazyLoadBar.setVisible(hasMore);
    listLazyLoadBar.setManaged(hasMore);
    refreshResultCounter();
  }

  private void loadMoreListItems() {
    if (hasMoreRemotePackages()) {
      fetchNextDbPage(this::syncListViewItems);
    }
  }

  private void refreshResultCounter() {
    boolean listActive = displaySwitchTabPane.getSelectionModel().getSelectedItem() == displayListTab;
    int displayedCount = listActive
        ? listViewItems.size()
        : masonryPane.getChildren().size();
    resultCounter.setText(displayedCount + " / " + totalRemotePackages);
  }

  /**
   * Returns true if the given first page result is different from the currently loaded search.
   */
  private boolean shouldRefreshPackages(Page<RemotePackage> newFirstPage) {
    if (newFirstPage.getTotalElements() != totalRemotePackages) {
      return true;
    }
    List<RemotePackage> newContent = newFirstPage.getContent();
    int compareSize = Math.min(newContent.size(), loadedRemotePackages.size());
    for (int i = 0; i < compareSize; i++) {
      if (!newContent.get(i).getId().equals(loadedRemotePackages.get(i).getId())) {
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
   * Trigger package installation. The best bundle will be selected based on the current user platform.
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

  @EventListener
  private void handle(SourceSyncEvent event) {
    FX.run(this::performPackageSearch);
  }

  @EventListener
  private void handle(RemoteSourceUpdatedEvent event) {
    FX.run(this::performPackageSearch);
  }

}