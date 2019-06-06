/* OwlPlug
 * Copyright (C) 2019 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OwlPlug is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OwlPlug.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package com.owlplug.core.ui;

import java.util.function.Predicate;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.TreeItem;

public class FilterableTreeItem<T> extends TreeItem<T> {
  private final ObservableList<TreeItem<T>> sourceChildren = FXCollections.observableArrayList();
  private final FilteredList<TreeItem<T>> filteredChildren = new FilteredList<>(sourceChildren);
  private final ObjectProperty<Predicate<T>> predicate = new SimpleObjectProperty<>();

  public FilterableTreeItem(T value) {
    super(value);

    filteredChildren.predicateProperty().bind(Bindings.createObjectBinding(() -> {
      Predicate<TreeItem<T>> p = child -> {
        if (child instanceof FilterableTreeItem) {
          ((FilterableTreeItem<T>) child).predicateProperty().set(predicate.get());
        }
        if (predicate.get() == null || !child.getChildren().isEmpty()) {
          return true;
        }
        return predicate.get().test(child.getValue());
      };
      return p;
    }, predicate));

    filteredChildren.addListener((ListChangeListener<TreeItem<T>>) c -> {
      while (c.next()) {
        getChildren().removeAll(c.getRemoved());
        getChildren().addAll(c.getAddedSubList());
      }
    });
  }

  public ObservableList<TreeItem<T>> getInternalChildren() {
    return sourceChildren;
  }

  public ObjectProperty<Predicate<T>> predicateProperty() {
    return predicate;
  }

} 
