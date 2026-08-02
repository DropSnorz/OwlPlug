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

package com.owlplug.project.components;

import com.owlplug.project.model.DawApplication;
import com.owlplug.project.model.DawProject;
import java.util.function.Predicate;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import org.springframework.stereotype.Component;

@Component
public class ProjectFilterState {

  private final ObservableSet<DawApplication> selectedApplications = FXCollections.observableSet();
  private final ObjectProperty<Predicate<DawProject>> predicate = new SimpleObjectProperty<>();
  private final IntegerProperty activeFilterCount = new SimpleIntegerProperty();

  public ProjectFilterState() {
    predicate.bind(Bindings.createObjectBinding(this::buildPredicate, selectedApplications));
    activeFilterCount.bind(Bindings.createIntegerBinding(selectedApplications::size, selectedApplications));
  }

  private Predicate<DawProject> buildPredicate() {
    if (selectedApplications.isEmpty()) {
      return null;
    }
    return project -> selectedApplications.contains(project.getApplication());
  }

  public ObservableSet<DawApplication> getSelectedApplications() {
    return selectedApplications;
  }

  public ObjectProperty<Predicate<DawProject>> predicateProperty() {
    return predicate;
  }

  public IntegerProperty activeFilterCountProperty() {
    return activeFilterCount;
  }

  public void clearAll() {
    selectedApplications.clear();
  }
}
