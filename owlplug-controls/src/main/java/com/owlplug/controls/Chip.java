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


import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Region;

public class Chip<T> extends Region {

  protected final ChipView<T> view;
  // --- item
  private ObjectProperty<T> item = new SimpleObjectProperty<T>(this, "item");

  public Chip(ChipView<T> view, T item) {
    this.view = view;
    getStyleClass().add("chip");
    setItem(item);
  }

  public final ObjectProperty<T> itemProperty() { return item; }

  public final void setItem(T value) { item.set(value); }

  public final T getItem() { return item.get(); }

  public final ChipView getChipView() {
    return view;
  }
}