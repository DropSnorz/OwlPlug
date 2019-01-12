package com.dropsnorz.owlplug.auth.controllers;

import com.dropsnorz.owlplug.auth.services.AuthenticationService;
import com.dropsnorz.owlplug.core.components.LazyViewRegistry;
import com.dropsnorz.owlplug.core.controllers.MainController;
import com.dropsnorz.owlplug.core.controllers.dialogs.AbstractDialogController;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSpinner;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class AccountController extends AbstractDialogController {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private AuthenticationService authentificationService;
  @Autowired 
  private MainController mainController;
  @Autowired 
  private LazyViewRegistry viewRegistry;

  @FXML
  private HBox buttonPane;
  @FXML
  private JFXButton googleButton;
  @FXML
  private JFXSpinner authProgressIndicator;
  @FXML
  private Label messageLabel;
  @FXML
  private JFXButton cancelButton;
  @FXML
  private JFXButton closeButton;

  /** Indicates if the user has pressed the cancel button. */
  private boolean cancelFlag = false;

  /**
   * FXML initialize method.
   */
  public void initialize() {

    googleButton.setOnAction(e -> {
      buttonPane.setVisible(false);
      authProgressIndicator.setVisible(true);
      messageLabel.setText("Your default browser is opening... Proceed to sign in and come back here.");
      messageLabel.setVisible(true);

      Task<Void> task = new Task<Void>() {
        @Override
        protected Void call() throws Exception {
          log.debug("Google auth task started");
          authentificationService.createAccountAndAuth();
          this.updateProgress(1, 1);
          return null;
        }
      };

      task.setOnSucceeded(event ->  {
        log.debug("Google auth task complete");
        authProgressIndicator.setVisible(false);
        buttonPane.setVisible(false);
        cancelButton.setVisible(false);
        closeButton.setVisible(true);
        messageLabel.setText("Your account has been successfully added");
        messageLabel.setVisible(true);
        cancelFlag = false;
        mainController.refreshAccounts();	
      });

      task.setOnFailed(event -> {
        log.debug("Google auth task failed");
        authProgressIndicator.setVisible(false);
        buttonPane.setVisible(true);
        cancelButton.setVisible(false);
        closeButton.setVisible(true);
        messageLabel.setVisible(false);

        if (!cancelFlag) {
          messageLabel.setText("En error occured during authentification");
          messageLabel.setVisible(true);
        }
        cancelFlag = false;
      });

      cancelButton.setVisible(true);
      closeButton.setVisible(false);
      new Thread(task).start();
    });	

    cancelButton.setOnAction(event -> {
      cancelFlag = true;
      authentificationService.stopAuthReceiver();
    });

    closeButton.setOnAction(event -> {
      close();
    });

    // Do not compute layout for invisible nodes
    buttonPane.managedProperty().bind(buttonPane.visibleProperty());
    authProgressIndicator.managedProperty().bind(authProgressIndicator.visibleProperty());
    messageLabel.managedProperty().bind(messageLabel.visibleProperty());

  }

  @Override
  protected void onDialogShow() {

    buttonPane.setVisible(true);
    cancelButton.setVisible(false);
    closeButton.setVisible(true);
    messageLabel.setVisible(false);

  }

  @Override
  protected Node getBody() {
    return viewRegistry.get(LazyViewRegistry.NEW_ACCOUNT_VIEW);
  }

  @Override
  protected Node getHeading() {
    return null;
  }

}
