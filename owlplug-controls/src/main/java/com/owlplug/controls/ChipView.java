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

import com.owlplug.controls.skins.ChipViewSkin;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Control;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * ChipView inspired by JFXChipView from JFoenix.
 *
 */
@Deprecated
public class ChipView<T> extends Control {

  private static <T> StringConverter<T> defaultStringConverter() {
    return new StringConverter<T>() {
      @Override
      public String toString(T t) {
        return t == null ? null : t.toString();
      }

      @Override
      public T fromString(String string) {
        return (T) string;
      }
    };
  }

  public ChipView() {
    getStyleClass().add(DEFAULT_STYLE_CLASS);
  }

  private static final String DEFAULT_STYLE_CLASS = "chip-view";

  @Override
  protected Skin<?> createDefaultSkin() {
    return new ChipViewSkin<T>(this);
  }

  private ObjectProperty<BiFunction<ChipView<T>, T, Chip<T>>> chipFactory;

  public BiFunction<ChipView<T>, T, Chip<T>> getChipFactory() {
    return chipFactory == null ? null : chipFactory.get();
  }

  public ObjectProperty<BiFunction<ChipView<T>, T, Chip<T>>> chipFactoryProperty() {
    if (chipFactory == null) {
      chipFactory = new SimpleObjectProperty<>(this, "chipFactory");
    }
    return chipFactory;
  }

  public void setChipFactory(BiFunction<ChipView<T>, T, Chip<T>> chipFactory) {
    chipFactoryProperty().set(chipFactory);
  }

  private ObjectProperty<Function<T, T>> selectionHandler;

  public Function<T, T> getSelectionHandler() {
    return selectionHandler == null ? null : selectionHandler.get();
  }

  public ObjectProperty<Function<T, T>> selectionHandlerProperty() {
    if (selectionHandler == null) {
      selectionHandler = new SimpleObjectProperty<>(this, "selectionHandler");
    }
    return selectionHandler;
  }

  public void setSelectionHandler(Function<T, T> selectionHandler) {
    selectionHandlerProperty().set(selectionHandler);
  }

  private StringProperty promptText = new SimpleStringProperty(this, "promptText", "");

  public final StringProperty promptTextProperty() {
    return promptText;
  }

  public final String getPromptText() {
    return promptText.get();
  }

  public final void setPromptText(String value) {
    promptText.set(value);
  }

  private AutoCompletePopup<T> autoCompletePopup = new ChipViewSkin.ChipsAutoComplete<T>();

  public AutoCompletePopup<T> getAutoCompletePopup() {
    return autoCompletePopup;
  }

  public ObservableList<T> getSuggestions() {
    return autoCompletePopup.getSuggestions();
  }

  public void setSuggestionsCellFactory(Callback<ListView<T>, ListCell<T>> factory) {
    autoCompletePopup.setSuggestionsCellFactory(factory);
  }

  private ObjectProperty<BiPredicate<T, String>> predicate = new SimpleObjectProperty<>(
      (item, text) -> {
        StringConverter<T> converter = getConverter();
        String itemString = converter != null ? converter.toString(item) : item.toString();
        return itemString.toLowerCase().contains(text.toLowerCase());
      }
  );

  public BiPredicate<T, String> getPredicate() {
    return predicate.get();
  }

  public ObjectProperty<BiPredicate<T, String>> predicateProperty() {
    return predicate;
  }

  public void setPredicate(BiPredicate<T, String> predicate) {
    this.predicate.set(predicate);
  }
  private ObservableList<T> chips = FXCollections.observableArrayList();

  public ObservableList<T> getChips() {
    return chips;
  }

  /**
   * Converts the user-typed input (when the ChipArea is
   * editable to an object of type T, such that
   * the input may be retrieved via the property.
   */

  private ObjectProperty<StringConverter<T>> converter =
      new SimpleObjectProperty<StringConverter<T>>(this, "converter", ChipView.defaultStringConverter());

  public ObjectProperty<StringConverter<T>> converterProperty() {
    return converter;
  }

  public final void setConverter(StringConverter<T> value) {
    converterProperty().set(value);
  }

  public final StringConverter<T> getConverter() {
    return converterProperty().get();
  }
}