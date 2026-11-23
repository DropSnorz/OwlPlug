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

import com.owlplug.explore.model.BundleType;
import com.owlplug.explore.model.PackageBundle;
import com.owlplug.explore.model.RemotePackage;
import com.owlplug.explore.model.search.ExploreFilterCriteria;
import com.owlplug.explore.model.search.ExploreFilterCriteriaType;
import com.owlplug.plugin.model.PluginFormat;
import com.owlplug.plugin.model.PluginType;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
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

  public static final Set<BundleType> DEFAULT_BUNDLE_TYPES = Set.of(BundleType.ARCHIVE);

  private final ObservableSet<Long> selectedSourceIds = FXCollections.observableSet();
  private final ObservableSet<PluginFormat> selectedFormats = FXCollections.observableSet();
  private final ObservableSet<PluginType> selectedTypes = FXCollections.observableSet();
  private final ObservableSet<String> selectedPlatformTags = FXCollections.observableSet();
  // Installers are handed over to the OS and need manual confirmation, so only archives
  // (installed by OwlPlug itself) are shown unless the user opts in.
  private final ObservableSet<BundleType> selectedBundleTypes =
      FXCollections.observableSet(EnumSet.copyOf(DEFAULT_BUNDLE_TYPES));

  private final ObjectProperty<List<ExploreFilterCriteria>> criteriaList = new SimpleObjectProperty<>();

  public ExploreFilterState() {
    criteriaList.bind(Bindings.createObjectBinding(this::buildCriteriaList,
        selectedSourceIds, selectedFormats, selectedTypes, selectedPlatformTags,
        selectedBundleTypes));
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
    if (!selectedBundleTypes.isEmpty()) {
      criteria.add(new ExploreFilterCriteria(new ArrayList<>(EnumSet.copyOf(selectedBundleTypes)),
          ExploreFilterCriteriaType.BUNDLE_TYPE_LIST));
    }
    return criteria;
  }

  public ObservableSet<BundleType> getSelectedBundleTypes() {
    return selectedBundleTypes;
  }

  /**
   * Returns whether bundles of the given type pass the current bundle type filter.
   * An empty selection means no restriction, like every other filter section.
   */
  public boolean isBundleTypeVisible(BundleType type) {
    return selectedBundleTypes.isEmpty() || selectedBundleTypes.contains(type);
  }

  /**
   * Returns the bundle types accepted by the current filter, all types if none is selected.
   */
  public Set<BundleType> getVisibleBundleTypes() {
    if (selectedBundleTypes.isEmpty()) {
      return EnumSet.allOf(BundleType.class);
    }
    return EnumSet.copyOf(selectedBundleTypes);
  }

  /**
   * Returns the package bundles matching the current bundle type filter, in registry order.
   */
  public List<PackageBundle> visibleBundles(RemotePackage remotePackage) {
    if (remotePackage.getBundles() == null) {
      return List.of();
    }
    return remotePackage.getBundles().stream()
        .filter(bundle -> isBundleTypeVisible(bundle.getType()))
        .sorted(Comparator.comparingInt(PackageBundle::getOrder))
        .toList();
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
    // Reset to defaults rather than clearing, so installers don't reappear unexpectedly
    selectedBundleTypes.retainAll(DEFAULT_BUNDLE_TYPES);
    selectedBundleTypes.addAll(DEFAULT_BUNDLE_TYPES);
  }
}
