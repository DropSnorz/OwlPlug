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

package com.owlplug.explore.components;

import com.owlplug.explore.model.search.ExploreFilterCriteria;
import com.owlplug.explore.model.search.ExploreFilterCriteriaType;
import com.owlplug.plugin.model.PluginFormat;
import com.owlplug.plugin.model.PluginType;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import org.springframework.stereotype.Component;

/**
 * Holds the current Explore view filter selections and derives the list of
 * {@link ExploreFilterCriteria} to send to {@code ExploreService#getRemotePackages}, since
 * package filtering is performed by a database query rather than an in-memory predicate.
 */
@Component
public class ExploreFilterState {

  private final ObservableSet<Long> selectedSourceIds = FXCollections.observableSet();
  private final ObservableSet<PluginFormat> selectedFormats = FXCollections.observableSet();
  private final ObservableSet<PluginType> selectedTypes = FXCollections.observableSet();
  private final ObservableSet<String> selectedPlatformTags = FXCollections.observableSet();

  private final ObjectProperty<List<ExploreFilterCriteria>> criteriaList = new SimpleObjectProperty<>();

  public ExploreFilterState() {
    criteriaList.bind(Bindings.createObjectBinding(this::buildCriteriaList,
        selectedSourceIds, selectedFormats, selectedTypes, selectedPlatformTags));
  }

  private List<ExploreFilterCriteria> buildCriteriaList() {
    List<ExploreFilterCriteria> criteria = new ArrayList<>();
    if (!selectedSourceIds.isEmpty()) {
      criteria.add(new ExploreFilterCriteria(new ArrayList<>(selectedSourceIds),
          ExploreFilterCriteriaType.SOURCE_LIST));
    }
    if (!selectedFormats.isEmpty()) {
      List<String> formats = selectedFormats.stream()
          .map(format -> format.getName().toLowerCase())
          .toList();
      criteria.add(new ExploreFilterCriteria(formats, ExploreFilterCriteriaType.FORMAT_LIST));
    }
    if (!selectedTypes.isEmpty()) {
      criteria.add(new ExploreFilterCriteria(new ArrayList<>(selectedTypes),
          ExploreFilterCriteriaType.TYPE_LIST));
    }
    if (!selectedPlatformTags.isEmpty()) {
      criteria.add(new ExploreFilterCriteria(new ArrayList<>(selectedPlatformTags),
          ExploreFilterCriteriaType.PLATFORM_LIST));
    }
    return criteria;
  }

  public ObservableSet<Long> getSelectedSourceIds() {
    return selectedSourceIds;
  }

  public ObservableSet<PluginFormat> getSelectedFormats() {
    return selectedFormats;
  }

  public ObservableSet<PluginType> getSelectedTypes() {
    return selectedTypes;
  }

  public ObservableSet<String> getSelectedPlatformTags() {
    return selectedPlatformTags;
  }

  public ObjectProperty<List<ExploreFilterCriteria>> criteriaListProperty() {
    return criteriaList;
  }

  public void clearAll() {
    selectedSourceIds.clear();
    selectedFormats.clear();
    selectedTypes.clear();
    selectedPlatformTags.clear();
  }
}
