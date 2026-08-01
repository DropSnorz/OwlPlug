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

package com.owlplug.plugin.controllers;

import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.ui.SideBar;
import com.owlplug.core.utils.StringUtils;
import com.owlplug.plugin.components.PluginFilterState;
import com.owlplug.plugin.model.PluginFormat;
import com.owlplug.plugin.model.PluginType;
import com.owlplug.plugin.repositories.PluginComponentRepository;
import com.owlplug.plugin.ui.PluginFormatBadgeView;
import jakarta.annotation.PostConstruct;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class PluginFilterController {

  @Autowired
  private PluginFilterState filterState;
  @Autowired
  private ApplicationDefaults applicationDefaults;
  @Autowired
  private PluginComponentRepository pluginComponentRepository;

  private static final int PREVIEW_COUNT = 3;

  private SideBar sideBar;
  private VBox formatSection;
  private VBox typeSection;
  private VBox manufacturerSection;
  private VBox categorySection;
  private final Map<PluginFormat, CheckBox> formatCheckBoxes = new EnumMap<>(PluginFormat.class);
  private final Map<PluginType, CheckBox> typeCheckBoxes = new EnumMap<>(PluginType.class);
  private final Map<String, CheckBox> manufacturerCheckBoxes = new HashMap<>();
  private final Map<String, CheckBox> categoryCheckBoxes = new HashMap<>();

  @PostConstruct
  public void setup() {
    formatSection = new VBox(4);
    typeSection = new VBox(4);
    manufacturerSection = new VBox(4);
    categorySection = new VBox(4);

    // Format and Type checkboxes are fixed (one per enum value) so their listeners are
    // wired directly: checkbox → filterState (selectedProperty) and filterState → checkbox
    // (SetChangeListener). Both directions are safe here because the checkboxes are never
    // discarded — they live as long as the sidebar.
    for (PluginType type : PluginType.values()) {
      CheckBox cb = new CheckBox(StringUtils.capitalize(type.getText()));
      cb.setGraphic(new FontIcon(applicationDefaults.getPackageTypeIconLiteral(type)));
      // checkbox → filterState: toggling the checkbox adds/removes the type from the active filter
      cb.selectedProperty().addListener((obs, old, selected) -> {
        if (selected != filterState.getSelectedTypes().contains(type)) {
          if (selected) {
            filterState.getSelectedTypes().add(type);
          } else {
            filterState.getSelectedTypes().remove(type);
          }
        }
      });
      // filterState → checkbox: external changes (e.g. "Clear all") keep the checkbox in sync
      filterState.getSelectedTypes().addListener(
          (SetChangeListener<PluginType>) change -> cb.setSelected(filterState.getSelectedTypes().contains(type)));
      typeCheckBoxes.put(type, cb);
      typeSection.getChildren().add(cb);
    }

    for (PluginFormat format : PluginFormat.values()) {
      CheckBox cb = new CheckBox(format.getName());
      cb.setGraphic(new PluginFormatBadgeView(format, applicationDefaults, PluginFormatBadgeView.DisplayMode.ICON_ONLY));
      cb.selectedProperty().addListener((obs, old, selected) -> {
        if (selected != filterState.getSelectedFormats().contains(format)) {
          if (selected) {
            filterState.getSelectedFormats().add(format);
          } else {
            filterState.getSelectedFormats().remove(format);
          }
        }
      });
      filterState.getSelectedFormats().addListener(
          (SetChangeListener<PluginFormat>) change -> cb.setSelected(filterState.getSelectedFormats().contains(format)));
      formatCheckBoxes.put(format, cb);
      formatSection.getChildren().add(cb);
    }

    // Shared listeners keep expandable-section checkboxes in sync with the filter state.
    // Each listener holds a reference only to the value-to-checkbox map, not to individual
    // CheckBox instances, so rebuilt checkboxes are not retained past their section lifetime.
    filterState.getSelectedManufacturers().addListener((SetChangeListener<String>) change -> {
      String v = change.wasAdded() ? change.getElementAdded() : change.getElementRemoved();
      CheckBox cb = manufacturerCheckBoxes.get(v);
      if (cb != null) cb.setSelected(change.wasAdded());
    });
    filterState.getSelectedCategories().addListener((SetChangeListener<String>) change -> {
      String v = change.wasAdded() ? change.getElementAdded() : change.getElementRemoved();
      CheckBox cb = categoryCheckBoxes.get(v);
      if (cb != null) cb.setSelected(change.wasAdded());
    });

    ScrollPane scrollPane = new ScrollPane(buildContent());
    scrollPane.setFitToWidth(true);
    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    scrollPane.getStyleClass().add("plugin-filter-scroll");

    sideBar = new SideBar(220, scrollPane);
    sideBar.getStyleClass().add("plugin-filter-sidebar");
    sideBar.setVisible(false);
    sideBar.setPrefWidth(0);
  }

  private VBox buildContent() {
    VBox content = new VBox(8);
    content.setPadding(new Insets(12));

    HBox header = new HBox();
    header.setAlignment(Pos.CENTER_LEFT);
    Label titleLabel = new Label("Filters");
    titleLabel.getStyleClass().add("plugin-filter-title");
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    Hyperlink clearLink = new Hyperlink("Clear all");
    clearLink.getStyleClass().add("plugin-filter-clear");
    clearLink.setOnAction(e -> filterState.clearAll());
    header.getChildren().addAll(titleLabel, spacer, clearLink);

    Label formatLabel = new Label("Format");
    formatLabel.getStyleClass().add("plugin-filter-section-title");
    Label typeLabel = new Label("Type");
    typeLabel.getStyleClass().add("plugin-filter-section-title");
    Label manufacturerLabel = new Label("Manufacturer");
    manufacturerLabel.getStyleClass().add("plugin-filter-section-title");
    Label categoryLabel = new Label("Category");
    categoryLabel.getStyleClass().add("plugin-filter-section-title");

    content.getChildren().addAll(
        header, new Separator(),
        formatLabel, formatSection,
        new Separator(), typeLabel, typeSection,
        new Separator(), manufacturerLabel, manufacturerSection,
        new Separator(), categoryLabel, categorySection
    );
    return content;
  }

  /**
   * Refreshes filter section counts from the database and rebuilds expandable sections.
   * Called after each plugin scan or data reload. Format and Type labels are updated
   * in-place; Manufacturer and Category sections are fully rebuilt because their values
   * are dynamic (any string from component metadata).
   */
  public void refresh() {
    // Format counts: keyed by enum name() because the DB stores EnumType.STRING values
    Map<String, Long> formatCounts = pluginComponentRepository.countFormatsFromComponents()
        .stream().collect(Collectors.toMap(e -> e.getLabel(), e -> e.getCnt()));
    formatCheckBoxes.forEach((format, cb) -> {
      long count = formatCounts.getOrDefault(format.name(), 0L);
      cb.setText(format.getName() + " (" + count + ")");
    });

    typeCheckBoxes.forEach((type, cb) ->
        cb.setText(StringUtils.capitalize(type.getText()) + " (" + pluginComponentRepository.countByType(type) + ")"));

    // Manufacturer and Category sections are rebuilt on each refresh: values are dynamic
    // and the set of visible checkboxes changes with each scan result.
    Map<String, Long> manufacturerCounts = pluginComponentRepository.countManufacturerNamesFromComponents()
        .stream().collect(Collectors.toMap(e -> e.getLabel(), e -> e.getCnt()));
    buildExpandableSection(
        manufacturerSection, manufacturerCheckBoxes, manufacturerCounts,
        value -> toggle(filterState.getSelectedManufacturers(), value),
        filterState.getSelectedManufacturers()
    );

    Map<String, Long> categoryCounts = pluginComponentRepository.countCategoriesFromComponents()
        .stream().collect(Collectors.toMap(e -> e.getLabel(), e -> e.getCnt()));
    buildExpandableSection(
        categorySection, categoryCheckBoxes, categoryCounts,
        value -> toggle(filterState.getSelectedCategories(), value),
        filterState.getSelectedCategories()
    );
  }

  private void toggle(ObservableSet<String> set, String value) {
    if (set.contains(value)) {
      set.remove(value);
    } else {
      set.add(value);
    }
  }

  /**
   * Rebuilds a dynamic filter section. The first PREVIEW_COUNT values are always visible;
   * the rest are hidden behind a "View more" toggle. Entries are sorted by count desc,
   * then alphabetically so the most common values appear first.
   *
   * checkBoxMap is cleared and repopulated on every call so the shared SetChangeListeners
   * registered in setup() always resolve to a live checkbox, never a stale one.
   */
  private void buildExpandableSection(VBox container, Map<String, CheckBox> checkBoxMap,
      Map<String, Long> countMap, Consumer<String> onToggle, ObservableSet<String> selectedSet) {
    container.getChildren().clear();
    // Clearing the map releases references to the previous generation of CheckBox instances
    checkBoxMap.clear();
    if (countMap.isEmpty()) {
      return;
    }

    List<String> values = countMap.entrySet().stream()
        .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder())
            .thenComparing(Map.Entry.comparingByKey()))
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());

    List<String> preview = values.subList(0, Math.min(PREVIEW_COUNT, values.size()));
    List<String> remaining = values.size() > PREVIEW_COUNT
        ? values.subList(PREVIEW_COUNT, values.size())
        : List.of();

    VBox previewBox = new VBox(4);
    for (String value : preview) {
      CheckBox cb = createCheckBox(value, countMap.get(value), onToggle, selectedSet);
      checkBoxMap.put(value, cb);
      previewBox.getChildren().add(cb);
    }
    container.getChildren().add(previewBox);

    if (!remaining.isEmpty()) {
      // Overflow values are rendered in a hidden VBox toggled by the "View more" hyperlink.
      // Both visible and hidden checkboxes are registered in checkBoxMap so the shared
      // listener can reach them regardless of the expanded/collapsed state.
      VBox fullBox = new VBox(4);
      for (String value : remaining) {
        CheckBox cb = createCheckBox(value, countMap.get(value), onToggle, selectedSet);
        checkBoxMap.put(value, cb);
        fullBox.getChildren().add(cb);
      }
      fullBox.setVisible(false);
      fullBox.setManaged(false);

      Hyperlink viewMore = new Hyperlink("View more (" + remaining.size() + ")");
      viewMore.getStyleClass().add("plugin-filter-view-more");
      viewMore.setOnAction(e -> {
        boolean expanded = fullBox.isVisible();
        fullBox.setVisible(!expanded);
        fullBox.setManaged(!expanded);
        viewMore.setText(expanded ? "View more (" + remaining.size() + ")" : "View less");
      });
      container.getChildren().addAll(fullBox, viewMore);
    }
  }

  /**
   * Creates a filter checkbox for a single string value. The checkbox→filterState direction
   * is wired here via selectedProperty. The reverse direction (filterState→checkbox) is
   * handled by the shared SetChangeListeners in setup() to avoid leaking stale checkboxes.
   */
  private CheckBox createCheckBox(String value, long count, Consumer<String> onToggle,
      ObservableSet<String> selectedSet) {
    CheckBox cb = new CheckBox(value + " (" + count + ")");
    cb.setSelected(selectedSet.contains(value));
    // Guard against feedback loops: only propagate when the UI state diverges from the model
    cb.selectedProperty().addListener((obs, old, selected) -> {
      if (selected != selectedSet.contains(value)) {
        onToggle.accept(value);
      }
    });
    return cb;
  }

  public SideBar getView() {
    return sideBar;
  }
}
