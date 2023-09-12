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

import com.owlplug.controls.skins.AutoCompletePopupSkin;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.PopupControl;
import javafx.scene.control.Skin;
import javafx.scene.layout.Region;
import javafx.stage.Window;
import javafx.util.Callback;

import java.util.List;
import java.util.function.Predicate;

/**
 * AutoCompletePopup is an animated popup list view that allow filtering.
 * Based on JFXAutocompletePopup from Jfoenix.
 * Deprecated, alternatives must be used.
 *
 */
@Deprecated
public class AutoCompletePopup<T> extends PopupControl {

  private final ObservableList<T> suggestions = FXCollections.observableArrayList();
  private final ObjectProperty<EventHandler<AutoCompleteEvent<T>>> selectionHandler = new SimpleObjectProperty<>();
  private final FilteredList<T> filteredData = new FilteredList<T>(suggestions, s -> true);
  private final ObjectProperty<Callback<ListView<T>, ListCell<T>>> suggestionsCellFactory = new SimpleObjectProperty<Callback<ListView<T>, ListCell<T>>>();

  private static final String DEFAULT_STYLE_CLASS = "autocomplete-popup";

  public AutoCompletePopup() {
    super();
    setAutoFix(true);
    setAutoHide(true);
    setHideOnEscape(true);
    getStyleClass().add(DEFAULT_STYLE_CLASS);
  }

  @Override
  protected Skin<?> createDefaultSkin() {
    return new AutoCompletePopupSkin<T>(this);
  }

  public void show(Node node) {
    if (!isShowing()) {
      if (node.getScene() == null || node.getScene().getWindow() == null) {
        throw new IllegalStateException("Can not show popup. The node must be attached to a scene/window.");
      }
      Window parent = node.getScene().getWindow();
      this.show(parent, parent.getX() + node.localToScene(0, 0).getX()
                      + node.getScene().getX(),
              parent.getY() + node.localToScene(0, 0).getY()
                      + node.getScene().getY() + ((Region)node).getHeight());
      ((AutoCompletePopupSkin<T>)getSkin()).animate();
    }
  }

  public ObservableList<T> getSuggestions() {
    return suggestions;
  }

  public void filter(Predicate<T> predicate) {
    filteredData.setPredicate(predicate);
  }

  public ObservableList<T> getFilteredSuggestions() {
    return filteredData;
  }

  public EventHandler<AutoCompleteEvent<T>> getSelectionHandler() {
    return selectionHandler.get();
  }

  public void setSelectionHandler(EventHandler<AutoCompleteEvent<T>> selectionHandler) {
    this.selectionHandler.set(selectionHandler);
  }

  public final ObjectProperty<Callback<ListView<T>, ListCell<T>>> suggestionsCellFactoryProperty() {
    return this.suggestionsCellFactory;
  }


  public final Callback<ListView<T>, ListCell<T>> getSuggestionsCellFactory() {
    return this.suggestionsCellFactoryProperty().get();
  }


  public final void setSuggestionsCellFactory(
          final javafx.util.Callback<ListView<T>, ListCell<T>> suggestionsCellFactory) {
    this.suggestionsCellFactoryProperty().set(suggestionsCellFactory);
  }

  /**
   * limits the number of cells to be shown, used to compute the list size
   */
  private IntegerProperty cellLimit = new SimpleIntegerProperty(
          AutoCompletePopup.this, "cellLimit", 10);

  public final void setCellLimit(int value) {
    cellLimitProperty().set(value);
  }

  public final int getCellLimit() {
    return cellLimitProperty().get();
  }

  public final IntegerProperty cellLimitProperty() {
    return cellLimit;
  }

  private DoubleProperty fixedCellSize = new SimpleDoubleProperty(
          AutoCompletePopup.this, "fixedCellSize", 40d);

  public final void setFixedCellSize(double value) {
    fixedCellSizeProperty().set(value);
  }

  public final double getFixedCellSize() {
    return fixedCellSizeProperty().get();
  }

  public final DoubleProperty fixedCellSizeProperty() {
    return fixedCellSize;
  }

  @Override
  public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
    return getClassCssMetaData();
  }

  public static class AutoCompleteEvent<T> extends Event {

    private final T object;

    public AutoCompleteEvent(EventType<? extends Event> eventType, T object) {
      super(eventType);
      this.object = object;
    }

    public T getObject() {
      return object;
    }

    public static final EventType<AutoCompleteEvent> SELECTION =
            new EventType<>(Event.ANY, "AUTOCOMPLETE_SELECTION");

  }
}