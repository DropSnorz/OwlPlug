package com.dropsnorz.owlplug.core.controllers.dialogs;

import java.util.ArrayList;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dropsnorz.owlplug.auth.dao.UserAccountDAO;
import com.dropsnorz.owlplug.auth.model.UserAccount;
import com.dropsnorz.owlplug.auth.ui.AccountCellFactory;
import com.dropsnorz.owlplug.auth.ui.AccountItem;
import com.dropsnorz.owlplug.core.components.LazyViewRegistry;
import com.dropsnorz.owlplug.core.controllers.IEntityCreateOrUpdate;
import com.dropsnorz.owlplug.core.dao.GoogleDriveRepositoryDAO;
import com.dropsnorz.owlplug.core.model.GoogleDriveRepository;
import com.dropsnorz.owlplug.core.services.PluginRepositoryService;
import com.dropsnorz.owlplug.core.utils.FileUtils;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
@Controller
public class GoogleDriveRepositoryController extends AbstractDialog implements IEntityCreateOrUpdate<GoogleDriveRepository> {

	@Autowired
	LazyViewRegistry viewRegistry;
	@Autowired
	PluginRepositoryService pluginRepositoryService;
	@Autowired
	UserAccountDAO userAccountDAO;

	@Autowired
	GoogleDriveRepositoryDAO googleDriveRepositoryDAO;

	@FXML
	JFXComboBox<AccountItem> accountComboBox;

	@FXML
	JFXButton addButton;

	@FXML
	JFXButton closeButton;

	@FXML
	private JFXTextField repositoryNameTextField;
	@FXML
	private JFXTextField googleDirectoryURLTextField;
	@FXML
	private Label messageLabel;

	private GoogleDriveRepository currentRepository;


	public void initialize() {

		closeButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				close();
			};
		});


		addButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				if(currentRepository != null) {
					updateRepository();
				}
				else {
					createRepository();
				}
			};
		});

		AccountCellFactory cellFactory = new AccountCellFactory();

		accountComboBox.setButtonCell(cellFactory.call(null));
		accountComboBox.setCellFactory(cellFactory);
	}

	@Override
	public void startCreateSequence() {

		refresh();
		this.currentRepository = null;
		this.messageLabel.setText("");
		this.repositoryNameTextField.setText("");
		this.repositoryNameTextField.setDisable(false);
		this.accountComboBox.setValue(null);
		this.googleDirectoryURLTextField.setText("");
	}

	@Override
	public void startUpdateSequence(GoogleDriveRepository entity) {

		refresh();
		this.currentRepository = entity;
		this.messageLabel.setText("");
		this.repositoryNameTextField.setText(currentRepository.getName());
		this.repositoryNameTextField.setDisable(true);
		this.googleDirectoryURLTextField.setText(currentRepository.getRemoteRessourceId());
		
		//Bug workaround. The only way way to pre-select the account is to find it's index in the list
		// If not, the selected cell is not rendered correctly
		accountComboBox.getItems().stream()
	    .filter(account -> account.getId().equals(currentRepository.getUserAccount().getId()))
	    .findAny()
	    .ifPresent(accountComboBox.getSelectionModel()::select);
		
	}

	private void refresh() {

		this.accountComboBox.setValue(null);

		ArrayList<UserAccount> accounts = new ArrayList<UserAccount>();

		for(UserAccount account : userAccountDAO.findAll()) {
			accounts.add(account);
		}

		accountComboBox.getItems().setAll(accounts);

		AccountCellFactory cellFactory = new AccountCellFactory();
		accountComboBox.setButtonCell(cellFactory.call(null));


	}

	private void createRepository() {

		String name = repositoryNameTextField.getText();
		String id = googleDirectoryURLTextField.getText();



		if(!FileUtils.isFilenameValid(name)) {
			messageLabel.setText("Repository name contains illegal characters");
			return;
		}

		if(accountComboBox.getSelectionModel().getSelectedItem() == null) {
			messageLabel.setText("Select a valid Google Account");
			return;

		}

		UserAccount account = (UserAccount) accountComboBox.getSelectionModel().getSelectedItem();
		GoogleDriveRepository repository = new GoogleDriveRepository(name, id, account);

		if (!pluginRepositoryService.createRepository(repository)) {
			messageLabel.setText("A repository named " + name + " already exists");
			return;
		}

		// Repository successfully created, dialog can be closed
		close();

	}


	private void updateRepository() {

		String id = googleDirectoryURLTextField.getText();

		if(accountComboBox.getSelectionModel().getSelectedItem() == null) {
			messageLabel.setText("Select a valid Google Account");
			return;

		}

		UserAccount account = (UserAccount) accountComboBox.getSelectionModel().getSelectedItem();
		currentRepository.setRemoteRessourceId(id);
		currentRepository.setUserAccount(account);

		pluginRepositoryService.save(currentRepository);
		
		close();

	}

	@Override
	protected Node getNode() {
		return viewRegistry.getAsNode(LazyViewRegistry.NEW_GOOGLE_DRIVE_REPOSITORY);

	}

}
