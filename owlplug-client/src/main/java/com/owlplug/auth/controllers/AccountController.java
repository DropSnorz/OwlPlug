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
 
package com.owlplug.auth.controllers;

import com.owlplug.auth.services.AuthenticationService;
import com.owlplug.controls.DialogLayout;
import com.owlplug.core.components.LazyViewRegistry;
import com.owlplug.core.controllers.MainController;
import com.owlplug.core.controllers.dialogs.AbstractDialogController;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class AccountController extends AbstractDialogController {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private AuthenticationService authenticationService;
  @Autowired 
  private MainController mainController;
  @Autowired 
  private LazyViewRegistry viewRegistry;

  @FXML
  private HBox buttonPane;
  @FXML
  private Button googleButton;
  @FXML
  private ProgressIndicator authProgressIndicator;
  @FXML
  private Label messageLabel;
  @FXML
  private Button cancelButton;
  @FXML
  private Button closeButton;

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
          authenticationService.createAccountAndAuth();
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
          messageLabel.setText("En error occurred during authentication");
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
      authenticationService.stopAuthReceiver();
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
  protected DialogLayout getLayout() {
    DialogLayout layout = new DialogLayout();
    layout.setBody(viewRegistry.get(LazyViewRegistry.NEW_ACCOUNT_VIEW));
    return layout;
  }

}
