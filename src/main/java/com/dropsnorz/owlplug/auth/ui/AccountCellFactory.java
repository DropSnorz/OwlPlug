package com.dropsnorz.owlplug.auth.ui;

import com.dropsnorz.owlplug.auth.model.UserAccount;
import com.dropsnorz.owlplug.auth.services.AuthentificationService;

import javafx.geometry.Pos;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.util.Callback;

public class AccountCellFactory implements Callback<ListView<AccountItem>, ListCell<AccountItem>> {

	private boolean showDeleteButton = false;
	private AuthentificationService authentificationService = null;
	private Pos align = Pos.CENTER_LEFT;
	
	public AccountCellFactory() {
		
	}
	public AccountCellFactory(Pos align) {
		this.align = align;
	}
	public AccountCellFactory(AuthentificationService authentificationService, boolean showDeleteButton){
		
		this.showDeleteButton = showDeleteButton;
		this.authentificationService = authentificationService;
	}

	@Override
	public ListCell<AccountItem> call(ListView<AccountItem> l) {
		return new ListCell<AccountItem>() {
			@Override
			protected void updateItem(AccountItem item, boolean empty) {
				super.updateItem(item, empty);
				
				setAlignment(align);

				if(item instanceof UserAccount) {
					
					UserAccount account = (UserAccount) item;
					
					HBox hBox= new HBox();
					hBox.setSpacing(5);
					hBox.setAlignment(align);
					
					if (account.getIconUrl() != null) {
						Image image = new Image(account.getIconUrl(), 32, 32, false, false, true);
						ImageView imageView = new ImageView(image);
						hBox.getChildren().add(imageView);
					}
					
					Label label = new Label(account.getName());
					hBox.getChildren().add(label);
					
					if(showDeleteButton) {
						Region growingArea = new Region();
						HBox.setHgrow(growingArea, Priority.ALWAYS);
						hBox.getChildren().add(growingArea);
						Hyperlink deleteButton = new Hyperlink("X");
						deleteButton.setVisited(true);
						hBox.getChildren().add(deleteButton);
						
						deleteButton.setOnAction(e -> {
							authentificationService.deleteAccount(account);
						});
					}
					
					setGraphic(hBox);
                    setText(null);
					return;
					
				}
				
				if(item instanceof AccountMenuItem) {

					AccountMenuItem accountMenuItem = (AccountMenuItem)item;
					setGraphic(null);
					setText(accountMenuItem.getText());
				}
								
			}
		} ;
	}
	
}
