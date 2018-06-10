package com.dropsnorz.owlplug.core.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

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
import javafx.scene.Parent;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

@Controller
public class MainController {


	@Autowired
	private LazyViewRegistry viewRegistry;
	@Autowired
	private AccountController accountController;
	@Autowired
	private UserAccountDAO userAccountDAO;
	@Autowired
	private AuthentificationService authentificationService;
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

		accountComboBox.setButtonCell(new AccountCellFactory().call(null));
		accountComboBox.setCellFactory(new AccountCellFactory(authentificationService,true));
		
		accountComboBox.setSkin(new JFXComboBoxListViewSkin<AccountItem>(accountComboBox) {
	        @Override
	        protected boolean isHideOnClickEnabled() {
	            return false;
	        }
	    });


		refreshAccounts();



	}

	public void refreshAccounts() {

		ArrayList<UserAccount> accounts = new ArrayList<UserAccount>();

		for(UserAccount account : userAccountDAO.findAll()) {
			accounts.add(account);
		}

		accountComboBox.getItems().setAll(accounts);
		accountComboBox.getItems().add(new AccountMenuItem(" + New Account"));



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
