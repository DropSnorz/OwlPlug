package com.dropsnorz.owlplug.auth.ui;

import com.dropsnorz.owlplug.auth.model.UserAccount;
import com.dropsnorz.owlplug.auth.services.AuthentificationService;
import com.dropsnorz.owlplug.core.components.ImageCache;

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
	private ImageCache imageCache;
	private Pos align = Pos.CENTER_LEFT;

	public AccountCellFactory(ImageCache imageCache) {
		this.imageCache = imageCache;

	}

	public AccountCellFactory(ImageCache imageCache, Pos align) {
		this.imageCache = imageCache;
		this.align = align;

	}
	public AccountCellFactory(AuthentificationService authentificationService, ImageCache imageCache, boolean showDeleteButton) {

		this.showDeleteButton = showDeleteButton;
		this.authentificationService = authentificationService;
		this.imageCache = imageCache;
	}

	@Override
	public ListCell<AccountItem> call(ListView<AccountItem> l) {
		return new ListCell<AccountItem>() {
			@Override
			protected void updateItem(AccountItem item, boolean empty) {
				super.updateItem(item, empty);

				setAlignment(align);

				if (item instanceof UserAccount) {

					UserAccount account = (UserAccount) item;

					HBox cell = new HBox();
					cell.setSpacing(5);
					cell.setAlignment(align);

					if (account.getIconUrl() != null) {
						Image image = imageCache.get(account.getIconUrl());
						ImageView imageView = new ImageView(image);
						imageView.setFitWidth(32);
						imageView.setFitHeight(32);
						cell.getChildren().add(imageView);
					}

					Label label = new Label(account.getName());
					cell.getChildren().add(label);

					if (showDeleteButton) {
						Region growingArea = new Region();
						HBox.setHgrow(growingArea, Priority.ALWAYS);
						cell.getChildren().add(growingArea);
						Hyperlink deleteButton = new Hyperlink("X");
						deleteButton.getStyleClass().add("hyperlink-button");
						cell.getChildren().add(deleteButton);

						deleteButton.setOnAction(e -> {
							authentificationService.deleteAccount(account);
						});
					}

					setGraphic(cell);
					setText(null);
					return;

				}

				if (item instanceof AccountMenuItem) {

					AccountMenuItem accountMenuItem = (AccountMenuItem)item;
					setGraphic(null);
					setText(accountMenuItem.getText());
				}

			}
		};
	}

}
