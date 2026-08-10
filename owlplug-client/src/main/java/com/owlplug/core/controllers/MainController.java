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

package com.owlplug.core.controllers;

import atlantafx.base.theme.Styles;
import com.owlplug.controls.Drawer;
import com.owlplug.controls.transitions.AnimatedTabListener;
import com.owlplug.core.components.ApplicationDefaults.Prefs;
import com.owlplug.core.components.ApplicationMonitor;
import com.owlplug.core.components.ImageCache;
import com.owlplug.core.components.LazyViewRegistry;
import com.owlplug.core.components.TaskRunner;
import com.owlplug.core.controllers.dialogs.CrashRecoveryDialogController;
import com.owlplug.core.controllers.dialogs.WelcomeDialogController;
import com.owlplug.core.events.PreferencesChangedEvent;
import com.owlplug.core.services.AppUpdateService;
import com.owlplug.core.utils.FX;
import com.owlplug.core.utils.PlatformUtils;
import com.owlplug.explore.components.ExploreTaskFactory;
import com.owlplug.explore.controllers.ExploreController;
import com.owlplug.plugin.services.PluginService;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;

@Controller
public class MainController extends BaseController {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private ApplicationEventPublisher publisher;
  @Autowired
  private LazyViewRegistry viewRegistry;
  @Autowired
  private CrashRecoveryDialogController crashRecoveryDialogController;
  @Autowired
  private ExploreTaskFactory exploreTaskFactory;
  @Autowired
  private WelcomeDialogController welcomeDialogController;
  @Autowired
  private ExploreController exploreController;
  @Autowired
  private AppUpdateService appUpdateService;
  @Autowired
  private PluginService pluginService;
  @Autowired
  private ImageCache imageCache;
  @Autowired
  private TaskRunner taskRunner;
  @Autowired
  private ApplicationMonitor applicationMonitor;

  @FXML
  private StackPane rootPane;
  @FXML
  private TabPane tabPaneHeader;
  @FXML
  private TabPane tabPaneContent;
  @FXML
  private Drawer leftDrawer;
  @FXML
  private Pane updatePane;
  @FXML
  private Label updateLabel;
  @FXML
  private Button downloadUpdateButton;

  public static int HOME_TAB_INDEX = 0;
  public static int PLUGINS_TAB_INDEX = 1;
  public static int EXPLORE_TAB_INDEX = 2;
  public static int PROJECTS_TAB_INDEX = 3;
  public static int SETTINGS_TAB_INDEX = 4;

  /**
   * FXML initialize method.
   */
  @FXML
  public void initialize() {

    viewRegistry.preload();
    this.getDialogManager().setDialogContainer(this.getRootPane());

    tabPaneHeader.getStyleClass().add(Styles.TABS_BORDER_TOP);
    this.tabPaneHeader.getSelectionModel().selectedIndexProperty().addListener((options, oldValue, newValue) -> {
      tabPaneContent.getSelectionModel().select(newValue.intValue());
      leftDrawer.close();

      // Force the store masonry pane to render correctly when the user select the
      // store tab.
      if (newValue.intValue() == 2) {
        exploreController.requestLayout();
      }
    });


    downloadUpdateButton.setOnAction(e -> {
      PlatformUtils.openDefaultBrowser(this.getApplicationDefaults().getDownloadUrl());
    });

    updatePane.setVisible(false);
    Executor executor = Executors.newVirtualThreadPerTaskExecutor();
    CompletableFuture
            .supplyAsync(() -> appUpdateService.getAvailableUpdateVersion(), executor)
            .thenAccept(availableVersion -> {
              FX.run(() -> {
                if (availableVersion != null) {
                  updateLabel.setText("New version available (v" + availableVersion + ")");
                  updatePane.setVisible(true);
                }
              });
            });

    tabPaneContent.getSelectionModel()
            .selectedItemProperty()
            .addListener(new AnimatedTabListener());
  }

  /**
   * Used to notify the MainController that Application is fully loaded. Must be
   * called once in the application lifecycle.
   */
  public void dispatchPostInitialize() {

    boolean firstLaunch = this.getPreferences().getBoolean(Prefs.App.FIRST_LAUNCH, true);
    if (!this.applicationMonitor.isPreviousExecutionSafelyTerminated()) {
      log.info("Previous execution not terminated safely, opening crash recovery dialog");
      crashRecoveryDialogController.show();
    } else if (firstLaunch) {
      welcomeDialogController.show();
      exploreTaskFactory.createSourceSyncTask().schedule();
      this.getPreferences().putBoolean(Prefs.App.FIRST_LAUNCH, false);
      publisher.publishEvent(new PreferencesChangedEvent());
    }

    this.getTelemetryService().event("/Startup", p -> {
      p.put("osName", System.getProperty("os.name"));
    });

    // Startup plugin sync only triggered if configured and previous application
    // instance safely terminated
    if (this.applicationMonitor.isPreviousExecutionSafelyTerminated()
            && this.getPreferences().getBoolean(Prefs.App.SYNC_PLUGINS_ON_STARTUP, false)) {
      log.info("Starting auto plugin sync");
      pluginService.scanPlugins(false);
    }

  }

  public void navigateToMainTab(int index) {
    this.tabPaneHeader.getSelectionModel().select(index);
  }


  @PreDestroy
  private void destroy() {
    this.taskRunner.close();
  }

  public StackPane getRootPane() {
    return rootPane;
  }

  public Drawer getLeftDrawer() {
    return leftDrawer;
  }

  /**
   * Set the left drawer content.
   *
   * @param node the content
   */
  public void setLeftDrawer(Parent node) {

    if (node != null) {
      leftDrawer.setSidePane(node);
    }
  }

}
