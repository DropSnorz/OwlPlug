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
 
package com.owlplug.auth.ui;

import com.owlplug.auth.model.UserAccount;
import com.owlplug.auth.services.AuthenticationService;
import com.owlplug.core.components.ImageCache;
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
  private AuthenticationService authenticationService = null;
  private ImageCache imageCache;
  private Pos align = Pos.CENTER_LEFT;

  public AccountCellFactory(ImageCache imageCache) {
    this.imageCache = imageCache;

  }

  /**
   * Creates a CellFactory with custom content alignment.
   * 
   * @param imageCache image cache instance
   * @param align      node alignment
   */
  public AccountCellFactory(ImageCache imageCache, Pos align) {
    this.imageCache = imageCache;
    this.align = align;

  }

  /**
   * Creates a CellFactory with custom content alignment.
   * 
   * @param authenticationService   AuthenticationService instance
   * @param imageCache              image cache instance
   * @param showDeleteButton        if a button should be displayed to delete
   *                                accounts
   */
  public AccountCellFactory(AuthenticationService authenticationService, ImageCache imageCache,
      boolean showDeleteButton) {

    this.showDeleteButton = showDeleteButton;
    this.authenticationService = authenticationService;
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
              authenticationService.deleteAccount(account);
            });
          }

          setGraphic(cell);
          setText(null);
          return;
        }

        if (item instanceof AccountMenuItem) {
          AccountMenuItem accountMenuItem = (AccountMenuItem) item;
          setGraphic(null);
          setText(accountMenuItem.getText());
        }
      }
    };
  }

}
