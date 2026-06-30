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
import java.util.function.Predicate;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import org.springframework.stereotype.Component;

@Component
public class PluginFilterState {

  private final ObservableSet<PluginFormat> selectedFormats = FXCollections.observableSet();
  private final ObservableSet<String> selectedManufacturers = FXCollections.observableSet();
  private final ObservableSet<String> selectedCategories = FXCollections.observableSet();
  private final ObjectProperty<Predicate<IPlugin>> predicate = new SimpleObjectProperty<>();

  public PluginFilterState() {
    predicate.bind(Bindings.createObjectBinding(this::buildPredicate,
        selectedFormats, selectedManufacturers, selectedCategories));
  }

  private Predicate<IPlugin> buildPredicate() {
    if (selectedFormats.isEmpty() && selectedManufacturers.isEmpty() && selectedCategories.isEmpty()) {
      return null;
    }
    return plugin -> {
      if (plugin instanceof PluginComponent component) {
        return matchesFormat(component.asPlugin().getFormat())
            && matchesManufacturer(component.getManufacturerName())
            && matchesCategory(component.getCategory());
      } else {
        Plugin p = (Plugin) plugin;
        return matchesFormat(p.getFormat())
            && (matchesManufacturer(p.getManufacturerName())
                || p.getComponents().stream().anyMatch(c -> matchesManufacturer(c.getManufacturerName())))
            && (matchesCategory(p.getCategory())
                || p.getComponents().stream().anyMatch(c -> matchesCategory(c.getCategory())));
      }
    };
  }

  private boolean matchesFormat(PluginFormat format) {
    return selectedFormats.isEmpty() || selectedFormats.contains(format);
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

  public ObservableSet<String> getSelectedManufacturers() {
    return selectedManufacturers;
  }

  public ObservableSet<String> getSelectedCategories() {
    return selectedCategories;
  }

  public ObjectProperty<Predicate<IPlugin>> predicateProperty() {
    return predicate;
  }

  public void clearAll() {
    selectedFormats.clear();
    selectedManufacturers.clear();
    selectedCategories.clear();
  }
}
