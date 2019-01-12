package com.dropsnorz.owlplug.core.controllers.dialogs;

import com.dropsnorz.owlplug.auth.dao.UserAccountDAO;
import com.dropsnorz.owlplug.auth.model.UserAccount;
import com.dropsnorz.owlplug.auth.ui.AccountCellFactory;
import com.dropsnorz.owlplug.auth.ui.AccountItem;
import com.dropsnorz.owlplug.core.components.ApplicationDefaults;
import com.dropsnorz.owlplug.core.components.ImageCache;
import com.dropsnorz.owlplug.core.components.LazyViewRegistry;
import com.dropsnorz.owlplug.core.controllers.IEntityCreateOrUpdate;
import com.dropsnorz.owlplug.core.controllers.PluginsController;
import com.dropsnorz.owlplug.core.model.GoogleDriveRepository;
import com.dropsnorz.owlplug.core.services.PluginRepositoryService;
import com.dropsnorz.owlplug.core.utils.FileUtils;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import java.util.ArrayList;
import java.util.Optional;
import java.util.prefs.Preferences;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class GoogleDriveRepositoryController extends AbstractDialogController
    implements IEntityCreateOrUpdate<GoogleDriveRepository> {

  @Autowired
  private LazyViewRegistry viewRegistry;
  @Autowired
  private PluginRepositoryService pluginRepositoryService;
  @Autowired
  private UserAccountDAO userAccountDAO;
  @Autowired
  private PluginsController pluginController;
  @Autowired
  ApplicationDefaults applicationDefaults;
  @Autowired
  private Preferences prefs;
  @Autowired
  private ImageCache imageCache;

  @FXML
  private JFXComboBox<AccountItem> accountComboBox;
  @FXML
  private JFXButton addButton;
  @FXML
  private JFXButton closeButton;
  @FXML
  private JFXTextField repositoryNameTextField;
  @FXML
  private JFXTextField googleDirectoryURLTextField;
  @FXML
  private Label messageLabel;

  private GoogleDriveRepository currentRepository;

  GoogleDriveRepositoryController() {
    super(600, 300);
  }

  /**
   * FXML initialize.
   */
  public void initialize() {

    closeButton.setOnAction(e -> {
      close();
    });

    addButton.setOnAction(e -> {
      if (currentRepository != null) {
        updateRepository();
      } else {
        createRepository();
      }
      pluginController.refreshPlugins();

    });

    AccountCellFactory cellFactory = new AccountCellFactory(imageCache);
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
    this.googleDirectoryURLTextField.setText("");

    long selectedAccountId = prefs.getLong(ApplicationDefaults.SELECTED_ACCOUNT_KEY, -1);
    if (selectedAccountId != -1) {
      Optional<UserAccount> selectedAccount = userAccountDAO.findById(selectedAccountId);

      if (selectedAccount.isPresent()) {
        // Bug workaround. The only way way to pre-select the account is to find it's
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

  @Override
  public void startUpdateSequence(GoogleDriveRepository entity) {

    refresh();
    this.currentRepository = entity;
    this.messageLabel.setText("");
    this.repositoryNameTextField.setText(currentRepository.getName());
    this.repositoryNameTextField.setDisable(true);
    this.googleDirectoryURLTextField.setText(currentRepository.getRemoteRessourceId());

    // Bug workaround. The only way way to pre-select the account is to find it's
    // index in the list
    // If not, the selected cell is not rendered correctly
    if (currentRepository.getUserAccount() != null) {
      accountComboBox.getItems().stream()
          .filter(account -> account.getId().equals(currentRepository.getUserAccount().getId())).findAny()
          .ifPresent(accountComboBox.getSelectionModel()::select);
    }
  }

  private void refresh() {

    this.accountComboBox.setValue(null);
    ArrayList<UserAccount> accounts = new ArrayList<>();

    for (UserAccount account : userAccountDAO.findAll()) {
      accounts.add(account);
    }

    accountComboBox.getItems().setAll(accounts);
    AccountCellFactory cellFactory = new AccountCellFactory(imageCache);
    accountComboBox.setButtonCell(cellFactory.call(null));

  }

  private void createRepository() {

    String name = repositoryNameTextField.getText();
    String id = googleDirectoryURLTextField.getText();
    if (!FileUtils.isFilenameValid(name)) {
      messageLabel.setText("Repository name contains illegal characters");
      return;
    }
    if (accountComboBox.getSelectionModel().getSelectedItem() == null) {
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

    if (accountComboBox.getSelectionModel().getSelectedItem() == null) {
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
  protected Node getBody() {
    return viewRegistry.getAsNode(LazyViewRegistry.NEW_GOOGLE_DRIVE_REPOSITORY);

  }

  @Override
  protected Node getHeading() {
    Label title = new Label("Google Drive Repository");
    title.getStyleClass().add("heading-3");

    ImageView iv = new ImageView(applicationDefaults.googleDriveRepositoryImage);
    iv.setFitHeight(20);
    iv.setFitWidth(20);
    title.setGraphic(iv);
    return title;
  }

}
