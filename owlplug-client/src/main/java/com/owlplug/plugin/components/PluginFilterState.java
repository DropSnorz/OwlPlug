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

package com.owlplug.plugin.components;

import com.owlplug.plugin.model.IPlugin;
import com.owlplug.plugin.model.Plugin;
import com.owlplug.plugin.model.PluginComponent;
import com.owlplug.plugin.model.PluginFormat;
import com.owlplug.plugin.model.PluginType;
import java.util.function.Predicate;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import org.springframework.stereotype.Component;

@Component
public class PluginFilterState {

  private final ObservableSet<PluginFormat> selectedFormats = FXCollections.observableSet();
  private final ObservableSet<PluginType> selectedTypes = FXCollections.observableSet();
  private final ObservableSet<String> selectedManufacturers = FXCollections.observableSet();
  private final ObservableSet<String> selectedCategories = FXCollections.observableSet();
  private final BooleanProperty disabledOnly = new SimpleBooleanProperty(false);
  private final ObjectProperty<Predicate<IPlugin>> predicate = new SimpleObjectProperty<>();

  public PluginFilterState() {
    predicate.bind(Bindings.createObjectBinding(this::buildPredicate,
        selectedFormats, selectedTypes, selectedManufacturers, selectedCategories, disabledOnly));
  }

  private Predicate<IPlugin> buildPredicate() {
    if (selectedFormats.isEmpty() && selectedTypes.isEmpty()
        && selectedManufacturers.isEmpty() && selectedCategories.isEmpty() && !disabledOnly.get()) {
      return null;
    }
    return plugin -> {
      if (plugin instanceof PluginComponent component) {
        return matchesFormat(component.asPlugin().getFormat())
            && matchesType(component.getType())
            && matchesManufacturer(component.getManufacturerName())
            && matchesCategory(component.getCategory())
            && matchesDisabled(component.asPlugin().isDisabled());
      } else {
        Plugin p = (Plugin) plugin;
        return matchesFormat(p.getFormat())
            && (matchesType(p.getType())
                || p.getComponents().stream().anyMatch(c -> matchesType(c.getType())))
            && (matchesManufacturer(p.getManufacturerName())
                || p.getComponents().stream().anyMatch(c -> matchesManufacturer(c.getManufacturerName())))
            && (matchesCategory(p.getCategory())
                || p.getComponents().stream().anyMatch(c -> matchesCategory(c.getCategory())))
            && matchesDisabled(p.isDisabled());
      }
    };
  }

  private boolean matchesDisabled(boolean disabled) {
    return !disabledOnly.get() || disabled;
  }

  private boolean matchesFormat(PluginFormat format) {
    return selectedFormats.isEmpty() || selectedFormats.contains(format);
  }

  private boolean matchesType(PluginType type) {
    return selectedTypes.isEmpty() || selectedTypes.contains(type);
  }

  private boolean matchesManufacturer(String manufacturer) {
    return selectedManufacturers.isEmpty() || selectedManufacturers.contains(manufacturer);
  }

  private boolean matchesCategory(String category) {
    return selectedCategories.isEmpty() || selectedCategories.contains(category);
  }

  public ObservableSet<PluginFormat> getSelectedFormats() {
    return selectedFormats;
  }

  public ObservableSet<PluginType> getSelectedTypes() {
    return selectedTypes;
  }

  public ObservableSet<String> getSelectedManufacturers() {
    return selectedManufacturers;
  }

  public ObservableSet<String> getSelectedCategories() {
    return selectedCategories;
  }

  public ObjectProperty<Predicate<IPlugin>> predicateProperty() {
    return predicate;
  }

  public BooleanProperty disabledOnlyProperty() {
    return disabledOnly;
  }

  public boolean isDisabledOnly() {
    return disabledOnly.get();
  }

  public void setDisabledOnly(boolean disabledOnly) {
    this.disabledOnly.set(disabledOnly);
  }

  public void clearAll() {
    selectedFormats.clear();
    selectedTypes.clear();
    selectedManufacturers.clear();
    selectedCategories.clear();
    disabledOnly.set(false);
  }
}
