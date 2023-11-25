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

package com.owlplug.controls;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

@Deprecated
public class DefaultChip<T> extends Chip<T> {
  protected HBox root;

  public DefaultChip(ChipView<T> view, T item) {
    super(view, item);
    Button closeButton = new Button("x");
    closeButton.getStyleClass().add("close-button");
    closeButton.setOnAction((event) -> view.getChips().remove(item));

    String tagString = null;
    if (getItem() instanceof String) {
      tagString = (String) getItem();
    } else {
      tagString = view.getConverter().toString(getItem());
    }
    Label label = new Label(tagString);
    label.setWrapText(true);
    root = new HBox(label, closeButton);
    getChildren().setAll(root);
    label.setMaxWidth(100);
  }
}