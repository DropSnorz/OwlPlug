package com.dropsnorz.owlplug.core.controllers;

import com.dropsnorz.owlplug.auth.controllers.AccountController;
import com.dropsnorz.owlplug.auth.dao.UserAccountDAO;
import com.dropsnorz.owlplug.auth.model.UserAccount;
import com.dropsnorz.owlplug.auth.services.AuthenticationService;
import com.dropsnorz.owlplug.auth.ui.AccountCellFactory;
import com.dropsnorz.owlplug.auth.ui.AccountItem;
import com.dropsnorz.owlplug.auth.ui.AccountMenuItem;
import com.dropsnorz.owlplug.core.components.ApplicationDefaults;
import com.dropsnorz.owlplug.core.components.ImageCache;
import com.dropsnorz.owlplug.core.components.LazyViewRegistry;
import com.dropsnorz.owlplug.core.controllers.dialogs.WelcomeDialogController;
import com.dropsnorz.owlplug.core.services.PluginService;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.skins.JFXComboBoxListViewSkin;
import java.util.ArrayList;
import java.util.Optional;
import java.util.prefs.Preferences;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class MainController {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private LazyViewRegistry viewRegistry;
	@Autowired
	private AccountController accountController;
	@Autowired
	private WelcomeDialogController welcomeDialogController;
	@Autowired
	private OptionsController optionsController;
	@Autowired
	private UserAccountDAO userAccountDAO;
	@Autowired
	private AuthenticationService authentificationService;
	@Autowired
	private Preferences prefs;
	@Autowired
	private PluginService pluginService;
	@Autowired 
	private ImageCache imageCache;
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

	/**
	 * FXML initialize method.
	 */
	@FXML
	public void initialize() {  

		viewRegistry.preload();

		this.tabPaneHeader.getSelectionModel().selectedIndexProperty().addListener((options, oldValue, newValue) -> {
			tabPaneContent.getSelectionModel().select(newValue.intValue());
			leftDrawer.close();
		});

		accountComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
			if (newValue instanceof AccountMenuItem) {
				accountController.show();
				// Delay comboBox selector change
				Platform.runLater(() -> accountComboBox.setValue(oldValue));

			}

			if (newValue instanceof UserAccount) {
				UserAccount userAccount = (UserAccount) newValue;
				prefs.putLong(ApplicationDefaults.SELECTED_ACCOUNT_KEY, userAccount.getId());

			}
		}); 

		accountComboBox.setButtonCell(new AccountCellFactory(imageCache, Pos.CENTER_RIGHT).call(null));
		accountComboBox.setCellFactory(new AccountCellFactory(authentificationService,imageCache, true));
		accountComboBox.setSkin(new JFXComboBoxListViewSkin<AccountItem>(accountComboBox) {
			@Override
			protected boolean isHideOnClickEnabled() {
				return false;
			}
		});


		refreshAccounts();

	}

	/**
	 * Used to notify the MainController that Application is fully loaded.
	 * Must be called once in the application lifecycle.
	 */
	public void dispatchPostInitialize() {
		
		if (prefs.getBoolean(ApplicationDefaults.FIRST_LAUNCH_KEY, true)) {
			welcomeDialogController.show();
		}
		prefs.putBoolean(ApplicationDefaults.FIRST_LAUNCH_KEY, false);
		optionsController.refreshView();
		
		if (prefs.getBoolean(ApplicationDefaults.SYNC_PLUGINS_STARTUP_KEY, false)) {
			log.info("Starting auto plugin sync");
			pluginService.syncPlugins();
		}
		
				
	}

	/**
	 * Refresh Account comboBox.
	 */
	public void refreshAccounts() {

		ArrayList<UserAccount> accounts = new ArrayList<UserAccount>();

		for (UserAccount account : userAccountDAO.findAll()) {
			accounts.add(account);
		}

		accountComboBox.hide();
		accountComboBox.getItems().clear();
		accountComboBox.getItems().setAll(accounts);
		accountComboBox.getItems().add(new AccountMenuItem(" + New Account"));

		long selectedAccountId = prefs.getLong(ApplicationDefaults.SELECTED_ACCOUNT_KEY, -1);
		if (selectedAccountId != -1) {
			Optional<UserAccount> selectedAccount = userAccountDAO.findById(selectedAccountId);
			if (selectedAccount.isPresent()) {

				//Bug workaround. The only way way to pre-select the account is to find it's index in the list
				// If not, the selected cell is not rendered correctly
				accountComboBox.getItems().stream()
				.filter(account -> account.getId().equals(selectedAccount.get().getId()))
				.findAny()
				.ifPresent(accountComboBox.getSelectionModel()::select);
			} else {
				accountComboBox.setValue(null);
			}
		} else {
			accountComboBox.setValue(null);

		}
	}

	public StackPane getRootPane() {
		return rootPane;
	}

	public JFXDrawer getLeftDrawer() {
		return leftDrawer;
	}

	/**
	 * Set the left drawer content.
	 * @param node the content
	 */
	public void setLeftDrawer(Parent node) {

		if (node != null) {
			leftDrawer.setSidePane(node);
		}
	}

}
