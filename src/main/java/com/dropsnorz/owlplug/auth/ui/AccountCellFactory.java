package com.dropsnorz.owlplug.auth.ui;

import com.dropsnorz.owlplug.auth.model.UserAccount;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

public class AccountCellFactory implements Callback<ListView<AccountItem>, ListCell<AccountItem>> {


	@Override
	public ListCell<AccountItem> call(ListView<AccountItem> l) {
		return new ListCell<AccountItem>() {
			@Override
			protected void updateItem(AccountItem item, boolean empty) {
				super.updateItem(item, empty);

				if(item instanceof UserAccount) {
					
					UserAccount account = (UserAccount) item;
					
					if (account.getIconUrl() != null) {
						Image image = new Image(account.getIconUrl(), 32, 32, false, false, true);
						ImageView imageView = new ImageView(image);
						setGraphic(imageView);
					}
					else {
						setGraphic(null);
					}
					
					setText(account.getName());
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
