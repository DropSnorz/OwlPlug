package com.dropsnorz.owlplug.auth.controllers;

import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dropsnorz.owlplug.auth.services.AuthentificationService;
import com.dropsnorz.owlplug.core.controllers.MainController;
import com.dropsnorz.owlplug.core.controllers.dialogs.DialogFrame;
import com.jfoenix.controls.JFXButton;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;

@Controller
public class AccountController extends DialogFrame {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AuthentificationService authentificationService;
	
	@Autowired 
	private MainController mainController;
	
	@FXML
	private HBox buttonPane;
	@FXML
	private JFXButton googleButton;
	@FXML
	private ProgressIndicator authProgressIndicator;
	@FXML
	private Label messageLabel;
	@FXML
	private JFXButton cancelButton;
	@FXML
	private JFXButton closeButton;
	
	private Task currentAuthTask = null;
	
	private boolean cancelFlag = false;
	
	public void initialize() {
		
		buttonPane.setVisible(true);
		cancelButton.setVisible(false);
		closeButton.setVisible(true);
		
		googleButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				
				authProgressIndicator.setVisible(true);
				
				Task<Void> task = new Task<Void>() {
					@Override
					protected Void call() throws Exception {
						
						log.debug("Google auth task started");
						authentificationService.createAccountAndAuth();
						
						this.updateProgress(1, 1);
						return null;
					}
					
				};
				
				task.setOnSucceeded(new EventHandler<WorkerStateEvent>(){
					@Override
					public void handle(WorkerStateEvent event) {
						
						log.debug("Google auth task complete");
						authProgressIndicator.setVisible(false);
						buttonPane.setVisible(false);
						cancelButton.setVisible(false);
						closeButton.setVisible(true);
						currentAuthTask = null;
						messageLabel.setText("Your account has been successfully added");
						cancelFlag = false;
						
						mainController.refreshAccounts();
					}
				});
				
				task.setOnFailed(new EventHandler<WorkerStateEvent>(){
					@Override
					public void handle(WorkerStateEvent event) {
						
						
						log.debug("Google auth task failed");
						authProgressIndicator.setVisible(false);
						buttonPane.setVisible(true);
						cancelButton.setVisible(false);
						closeButton.setVisible(true);
						currentAuthTask = null;
						
						if(!cancelFlag) messageLabel.setText("En error occured during authentification");
						cancelFlag = false;
					}
				});
				
				
				currentAuthTask = task;
				cancelButton.setVisible(true);
				closeButton.setVisible(false);
				new Thread(task).start();

			};
		});	
		
		cancelButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				
				cancelFlag = true;
				authentificationService.stopAuthReceiver();
				
			}
				
		});
		
		closeButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				
				close();
				
			}
				
		});
		
		
		
		
	}

}
