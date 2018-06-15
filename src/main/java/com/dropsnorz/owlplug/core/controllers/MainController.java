package com.dropsnorz.owlplug.core.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

import com.dropsnorz.owlplug.ApplicationDefaults;
import com.dropsnorz.owlplug.auth.controllers.AccountController;
import com.dropsnorz.owlplug.auth.dao.UserAccountDAO;
import com.dropsnorz.owlplug.auth.model.UserAccount;
import com.dropsnorz.owlplug.auth.services.AuthentificationService;
import com.dropsnorz.owlplug.auth.ui.AccountCellFactory;
import com.dropsnorz.owlplug.auth.ui.AccountItem;
import com.dropsnorz.owlplug.auth.ui.AccountMenuItem;
import com.dropsnorz.owlplug.core.components.LazyViewRegistry;
import com.dropsnorz.owlplug.core.controllers.dialogs.DialogController;
import com.dropsnorz.owlplug.core.model.Plugin;
import com.dropsnorz.owlplug.core.services.OptionsService;
import com.dropsnorz.owlplug.core.services.TaskFactory;
import com.dropsnorz.owlplug.core.services.TaskManager;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTreeView;
import com.jfoenix.skins.JFXComboBoxListViewSkin;
import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

@Controller
public class MainController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private LazyViewRegistry viewRegistry;
	@Autowired
	private AccountController accountController;
	@Autowired
	private UserAccountDAO userAccountDAO;
	@Autowired
	private AuthentificationService authentificationService;
	@Autowired
	private Preferences prefs;
	@Autowired
	private TaskManager taskManager;
	@Autowired
	private TaskFactory taskFactory;
	@FXML 
	StackPane rootPane;
	@FXML
	BorderPane mainPane;
	@FXML
	JFXTabPane tabPaneHeader;
	@FXML
	JFXTabPane tabPaneContent;
	@FXML
	JFXDrawer leftDrawer;
	@FXML
	JFXComboBox<AccountItem> accountComboBox;

	@FXML
	public void initialize() {  
		
		viewRegistry.preload();

		this.tabPaneHeader.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				tabPaneContent.getSelectionModel().select(newValue.intValue());
				leftDrawer.close();
			}
		});

		accountComboBox.getSelectionModel().selectedItemProperty().addListener( (options, oldValue, newValue) -> {
			if(newValue != null && newValue instanceof AccountMenuItem) {
				accountController.show();
				// Delay comboBox selector change
				Platform.runLater(() -> accountComboBox.setValue(oldValue));

			}
		}); 

		accountComboBox.setButtonCell(new AccountCellFactory(Pos.CENTER_RIGHT).call(null));
		accountComboBox.setCellFactory(new AccountCellFactory(authentificationService,true));
		accountComboBox.setSkin(new JFXComboBoxListViewSkin<AccountItem>(accountComboBox) {
	        @Override
	        protected boolean isHideOnClickEnabled() {
	            return false;
	        }
	    });

		refreshAccounts();

	}
	
	public void dispatchPostInitialize() {
		if(prefs.getBoolean(ApplicationDefaults.SYNC_PLUGINS_STARTUP_KEY, false)) {
			log.info("Starting auto plugin sync");
			
			taskFactory.run(taskFactory.createSyncPluginTask());
		}
	}

	public void refreshAccounts() {

		ArrayList<UserAccount> accounts = new ArrayList<UserAccount>();
		
		for(UserAccount account : userAccountDAO.findAll()) {
			accounts.add(account);
		}
		
		accountComboBox.hide();
		accountComboBox.getItems().clear();
		accountComboBox.getItems().setAll(accounts);
		accountComboBox.getItems().add(new AccountMenuItem(" + New Account"));
		accountComboBox.setVisibleRowCount(accountComboBox.getItems().size() + 1); // set new visibleRowCount value
		
		long selectedAccountId = prefs.getLong(ApplicationDefaults.SELECTED_ACCOUNT_KEY, -1);
		if(selectedAccountId != -1) {
			UserAccount selectedAccount = userAccountDAO.findById(selectedAccountId).get();
			
			//Bug workaround. The only way way to pre-select the account is to find it's index in the list
			// If not, the selected cell is not rendered correctly
			accountComboBox.getItems().stream()
			.filter(account -> account.getId().equals(selectedAccount.getId()))
			.findAny()
			.ifPresent(accountComboBox.getSelectionModel()::select);
		}
		else {
			accountComboBox.setValue(null);

		}
	}

	public StackPane getRootPane() {
		return rootPane;
	}

	public JFXDrawer getLeftDrawer() {
		return leftDrawer;
	}

	public void setLeftDrawer(Parent node) {

		if(node != null) {
			leftDrawer.setSidePane(node);
		}
	}



}
