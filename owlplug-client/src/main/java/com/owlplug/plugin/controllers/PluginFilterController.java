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
import com.owlplug.plugin.components.PluginFilterModel;
import com.owlplug.plugin.model.Plugin;
import com.owlplug.plugin.model.PluginFormat;
import com.owlplug.plugin.ui.PluginFormatBadgeView;
import jakarta.annotation.PostConstruct;
import java.util.Comparator;
import java.util.EnumMap;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class PluginFilterController {

  @Autowired
  private PluginFilterModel filterModel;
  @Autowired
  private ApplicationDefaults applicationDefaults;

  private static final int PREVIEW_COUNT = 3;

  private SideBar sideBar;
  private VBox formatSection;
  private VBox manufacturerSection;
  private VBox categorySection;
  private final Map<PluginFormat, CheckBox> formatCheckBoxes = new EnumMap<>(PluginFormat.class);

  @PostConstruct
  public void setup() {
    formatSection = new VBox(4);
    manufacturerSection = new VBox(4);
    categorySection = new VBox(4);

    for (PluginFormat format : PluginFormat.values()) {
      CheckBox cb = new CheckBox(format.getName());
      cb.setGraphic(new PluginFormatBadgeView(format, applicationDefaults, PluginFormatBadgeView.DisplayMode.ICON_ONLY));
      cb.selectedProperty().addListener((obs, old, selected) -> {
        if (selected != filterModel.getSelectedFormats().contains(format)) {
          if (selected) {
            filterModel.getSelectedFormats().add(format);
          } else {
            filterModel.getSelectedFormats().remove(format);
          }
        }
      });
      filterModel.getSelectedFormats().addListener(
          (SetChangeListener<PluginFormat>) change -> cb.setSelected(filterModel.getSelectedFormats().contains(format)));
      formatCheckBoxes.put(format, cb);
      formatSection.getChildren().add(cb);
    }

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
    clearLink.setOnAction(e -> filterModel.clearAll());
    header.getChildren().addAll(titleLabel, spacer, clearLink);

    Label formatLabel = new Label("Format");
    formatLabel.getStyleClass().add("plugin-filter-section-title");
    Label manufacturerLabel = new Label("Manufacturer");
    manufacturerLabel.getStyleClass().add("plugin-filter-section-title");
    Label categoryLabel = new Label("Category");
    categoryLabel.getStyleClass().add("plugin-filter-section-title");

    content.getChildren().addAll(
        header, new Separator(),
        formatLabel, formatSection,
        new Separator(), manufacturerLabel, manufacturerSection,
        new Separator(), categoryLabel, categorySection
    );
    return content;
  }

  public void refresh(List<Plugin> plugins) {
    Map<PluginFormat, Long> formatCounts = plugins.stream()
        .filter(p -> p.getFormat() != null)
        .collect(Collectors.groupingBy(Plugin::getFormat, Collectors.counting()));
    formatCheckBoxes.forEach((format, cb) -> {
      long count = formatCounts.getOrDefault(format, 0L);
      cb.setText(format.getName() + " (" + count + ")");
    });

    Map<String, Long> manufacturerCounts = plugins.stream()
        .filter(p -> p.getManufacturerName() != null && !p.getManufacturerName().isBlank())
        .collect(Collectors.groupingBy(Plugin::getManufacturerName, Collectors.counting()));
    buildExpandableSection(
        manufacturerSection, manufacturerCounts,
        value -> toggle(filterModel.getSelectedManufacturers(), value),
        filterModel.getSelectedManufacturers()
    );

    Map<String, Long> categoryCounts = plugins.stream()
        .filter(p -> p.getCategory() != null && !p.getCategory().isBlank())
        .collect(Collectors.groupingBy(Plugin::getCategory, Collectors.counting()));
    buildExpandableSection(
        categorySection, categoryCounts,
        value -> toggle(filterModel.getSelectedCategories(), value),
        filterModel.getSelectedCategories()
    );
  }

  private void toggle(ObservableSet<String> set, String value) {
    if (set.contains(value)) {
      set.remove(value);
    } else {
      set.add(value);
    }
  }

  private void buildExpandableSection(VBox container, Map<String, Long> countMap,
      Consumer<String> onToggle, ObservableSet<String> selectedSet) {
    container.getChildren().clear();
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
      previewBox.getChildren().add(createCheckBox(value, countMap.get(value), onToggle, selectedSet));
    }
    container.getChildren().add(previewBox);

    if (!remaining.isEmpty()) {
      VBox fullBox = new VBox(4);
      for (String value : remaining) {
        fullBox.getChildren().add(createCheckBox(value, countMap.get(value), onToggle, selectedSet));
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

  private CheckBox createCheckBox(String value, long count, Consumer<String> onToggle,
      ObservableSet<String> selectedSet) {
    CheckBox cb = new CheckBox(value + " (" + count + ")");
    cb.setSelected(selectedSet.contains(value));
    cb.selectedProperty().addListener((obs, old, selected) -> {
      if (selected != selectedSet.contains(value)) {
        onToggle.accept(value);
      }
    });
    selectedSet.addListener(
        (SetChangeListener<String>) change -> cb.setSelected(selectedSet.contains(value)));
    return cb;
  }

  public SideBar getView() {
    return sideBar;
  }
}
