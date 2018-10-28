package com.dropsnorz.owlplug.store.controllers;

import com.dropsnorz.owlplug.core.components.ApplicationDefaults;
import com.dropsnorz.owlplug.core.components.LazyViewRegistry;
import com.dropsnorz.owlplug.core.controllers.IEntityCreateOrUpdate;
import com.dropsnorz.owlplug.core.controllers.dialogs.AbstractDialogController;
import com.dropsnorz.owlplug.core.controllers.dialogs.DialogController;
import com.dropsnorz.owlplug.store.dao.StoreDAO;
import com.dropsnorz.owlplug.store.model.Store;
import com.dropsnorz.owlplug.store.service.StoreService;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSpinner;
import com.jfoenix.controls.JFXTextField;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class NewStoreDialogController extends AbstractDialogController implements IEntityCreateOrUpdate<Store> {
	
	@Autowired
	private ApplicationDefaults applicationDefaults;
	@Autowired
	private LazyViewRegistry lazyViewRegistry;
	@Autowired
	private StoreService pluginStoreService;
	@Autowired
	private StoreDAO pluginStoreDAO;
	@Autowired
	private StoreMenuController storeMenuController;
	@Autowired
	private DialogController dialogController;
	
	@FXML
	private JFXTextField storeUrlTextField;
	@FXML
	private JFXSpinner progressSpinner;
	@FXML
	private Label errorLabel;
	@FXML
	private JFXButton okButton;
	@FXML
	private JFXButton cancelButton;
	
	public NewStoreDialogController() {
		super(500,200);
	}
	
	/**
	 * FXML initialize.
	 */
	public void initialize() {
		
		progressSpinner.setVisible(false);
		errorLabel.setVisible(false);
		
		okButton.setOnAction(e -> {
			getPluginStore();
		});
		
		
		cancelButton.setOnAction(e -> {
			close();
		});
		
	}
	
	@Override
	public void startCreateSequence() {
		storeUrlTextField.setText("");
		progressSpinner.setVisible(false);
		errorLabel.setVisible(false);
		
	}

	@Override
	public void startUpdateSequence(Store entity) {
		throw new UnsupportedOperationException();
		
	}
	
	private void getPluginStore() {
		
		progressSpinner.setVisible(true);
		String storeUrl = storeUrlTextField.getText();
		
		if (storeUrl != null && !storeUrl.isEmpty()) {
			Task<Store> task = new Task<Store>() {
				@Override
				protected Store call() throws Exception {
					return pluginStoreService.getPluginStoreFromUrl(storeUrl);
				}
			};
			
			task.setOnSucceeded(e -> {
				Store pluginStore = task.getValue();
				progressSpinner.setVisible(false);
				if (pluginStore != null) {
					errorLabel.setVisible(false);
					pluginStoreDAO.save(pluginStore);
					storeMenuController.refreshView();
					close();
					dialogController.newSimpleInfoDialog("Success", 
							"The plugin store " + pluginStore.getName() + " has been sucessfully added !"
							).show();
				} else {
					errorLabel.setVisible(true);
				}
			});
			new Thread(task).start();

		}
		
	}

	@Override
	protected Node getBody() {
		return lazyViewRegistry.getAsNode(LazyViewRegistry.NEW_STORE_VIEW);
	}

	@Override
	protected Node getHeading() {
		Label title = new Label("Add a new store");
		title.getStyleClass().add("heading-3");

		ImageView iv = new ImageView(applicationDefaults.storeImage);
		iv.setFitHeight(20);
		iv.setFitWidth(20);
		title.setGraphic(iv);
		return title;
	}

}
