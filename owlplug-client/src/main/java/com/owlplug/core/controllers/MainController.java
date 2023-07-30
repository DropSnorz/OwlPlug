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

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.skins.JFXComboBoxListViewSkin;
import com.owlplug.auth.controllers.AccountController;
import com.owlplug.auth.model.UserAccount;
import com.owlplug.auth.services.AuthenticationService;
import com.owlplug.auth.ui.AccountCellFactory;
import com.owlplug.auth.ui.AccountItem;
import com.owlplug.auth.ui.AccountMenuItem;
import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.components.ApplicationMonitor;
import com.owlplug.core.components.ImageCache;
import com.owlplug.core.components.LazyViewRegistry;
import com.owlplug.core.components.TaskRunner;
import com.owlplug.core.controllers.dialogs.CrashRecoveryDialogController;
import com.owlplug.core.controllers.dialogs.WelcomeDialogController;
import com.owlplug.core.services.PluginService;
import com.owlplug.core.services.UpdateService;
import com.owlplug.core.utils.PlatformUtils;
import com.owlplug.explore.controllers.ExploreController;
import com.owlplug.explore.services.ExploreService;
import jakarta.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Optional;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class MainController extends BaseController {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private LazyViewRegistry viewRegistry;
  @Autowired
  private AccountController accountController;
  @Autowired
  private CrashRecoveryDialogController crashRecoveryDialogController;
  @Autowired
  private WelcomeDialogController welcomeDialogController;
  @Autowired
  private OptionsController optionsController;
  @Autowired
  private ExploreController exploreController;
  @Autowired
  private AuthenticationService authenticationService;
  @Autowired
  private UpdateService updateService;
  @Autowired
  private PluginService pluginService;
  @Autowired
  private ExploreService exploreService;
  @Autowired
  private ImageCache imageCache;
  @Autowired
  private TaskRunner taskRunner;
  @Autowired
  private ApplicationMonitor applicationMonitor;
  @FXML
  private StackPane rootPane;
  @FXML
  private BorderPane mainPane;
  @FXML
  private JFXTabPane tabPaneHeader;
  @FXML
  private JFXTabPane tabPaneContent;
  @FXML
  private VBox contentPanePlaceholder;
  @FXML
  private JFXDrawer leftDrawer;
  @FXML
  private JFXComboBox<AccountItem> accountComboBox;
  @FXML
  private Pane updatePane;
  @FXML
  private JFXButton downloadUpdateButton;

  /**
   * FXML initialize method.
   */
  @FXML
  public void initialize() {
    
    viewRegistry.preload();

    this.tabPaneHeader.getSelectionModel().selectedIndexProperty().addListener((options, oldValue, newValue) -> {
      tabPaneContent.getSelectionModel().select(newValue.intValue());
      leftDrawer.close();

      // Force the store masonry pane to render correctly when the user select the
      // store tab.
      if (newValue.intValue() == 2) {
        exploreController.requestLayout();
      }
    });

    accountComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
      if (newValue instanceof AccountMenuItem) {
        accountController.show();
        // Delay comboBox selector change
        Platform.runLater(() -> accountComboBox.setValue(oldValue));

      }
      if (newValue instanceof UserAccount) {
        UserAccount userAccount = (UserAccount) newValue;
        this.getPreferences().putLong(ApplicationDefaults.SELECTED_ACCOUNT_KEY, userAccount.getId());

      }
      accountComboBox.hide();
    });

    accountComboBox.setButtonCell(new AccountCellFactory(imageCache, Pos.CENTER_RIGHT).call(null));
    accountComboBox.setCellFactory(new AccountCellFactory(authenticationService, imageCache, true));

    JFXComboBoxListViewSkin<AccountItem> accountCBSkin = new JFXComboBoxListViewSkin<AccountItem>(accountComboBox);
    accountCBSkin.setHideOnClick(false);
    accountComboBox.setSkin(accountCBSkin);

    refreshAccounts();
    
    downloadUpdateButton.setOnAction(e -> {
      PlatformUtils.openDefaultBrowser(this.getApplicationDefaults().getUpdateDownloadUrl());
    });
    
    updatePane.setVisible(false);
    
    Task<Boolean> retrieveUpdateStatusTask = new Task<Boolean>() {
        @Override
        protected Boolean call() throws Exception {
          return updateService.isUpToDate();
        }
    };
    
    retrieveUpdateStatusTask.setOnSucceeded(e -> {
      if (!retrieveUpdateStatusTask.getValue()) {
        updatePane.setVisible(true);
      }
    });

    new Thread(retrieveUpdateStatusTask).start();

  }

  /**
   * Used to notify the MainController that Application is fully loaded. Must be
   * called once in the application lifecycle.
   */
  public void dispatchPostInitialize() {

    if (!this.applicationMonitor.isPreviousExecutionSafelyTerminated()) {
      crashRecoveryDialogController.show();
    } else if (this.getPreferences().getBoolean(ApplicationDefaults.FIRST_LAUNCH_KEY, true)) {
      welcomeDialogController.show();
      exploreService.syncSources();
    }
    this.getPreferences().putBoolean(ApplicationDefaults.FIRST_LAUNCH_KEY, false);
    optionsController.refreshView();
    
    this.getAnalyticsService().pageView("/app/core/startup");

    // Startup plugin sync only triggered if configured and previous application
    // instance safely terminated
    if (this.applicationMonitor.isPreviousExecutionSafelyTerminated()
            && this.getPreferences().getBoolean(ApplicationDefaults.SYNC_PLUGINS_STARTUP_KEY, false)) {
      log.info("Starting auto plugin sync");
      pluginService.syncPlugins();
    }

  }

  /**
   * Refresh Account comboBox.
   */
  public void refreshAccounts() {

    ArrayList<UserAccount> accounts = new ArrayList<UserAccount>();

    for (UserAccount account : authenticationService.getAccounts()) {
      accounts.add(account);
    }

    accountComboBox.hide();
    accountComboBox.getItems().clear();
    accountComboBox.getItems().setAll(accounts);
    accountComboBox.getItems().add(new AccountMenuItem(" + New Account"));

    long selectedAccountId = this.getPreferences().getLong(ApplicationDefaults.SELECTED_ACCOUNT_KEY, -1);
    if (selectedAccountId != -1) {
      Optional<UserAccount> selectedAccount = authenticationService.getUserAccountById(selectedAccountId);
      if (selectedAccount.isPresent()) {

        // Bug workaround. The only way to pre-select the account is to find its
        // index in the list
        // If not, the selected cell is not rendered correctly
        accountComboBox.getItems().stream().filter(account -> account.getId().equals(selectedAccount.get().getId()))
            .findAny().ifPresent(accountComboBox.getSelectionModel()::select);
      } else {
        accountComboBox.setValue(null);
      }
    } else {
      accountComboBox.setValue(null);

    }

  }
  
  @PreDestroy
  private void destroy() {
    this.taskRunner.close();
  }

  public StackPane getRootPane() {
    return rootPane;
  }

  public JFXDrawer getLeftDrawer() {
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
