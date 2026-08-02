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

package com.owlplug.explore.controllers;

import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.ui.SideBar;
import com.owlplug.core.utils.FX;
import com.owlplug.core.utils.StringUtils;
import com.owlplug.explore.components.ExploreFilterState;
import com.owlplug.explore.events.RemoteSourceUpdatedEvent;
import com.owlplug.explore.model.RemoteSource;
import com.owlplug.explore.services.ExploreService;
import com.owlplug.plugin.model.PluginFormat;
import com.owlplug.plugin.model.PluginType;
import com.owlplug.plugin.ui.PluginFormatBadgeView;
import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.SetChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Controller;

@Controller
public class ExploreFilterController {

  /**
   * Platform tags that have a checkbox in the filter panel, mapped to their display label.
   * {@link ExploreFilterState#getSelectedPlatformTags()} can also contain entries outside this
   * set (OS codes, tag aliases) used only for broader remote-package matching; only entries in
   * this map represent a filter the user can actually see/toggle.
   */
  private static final Map<String, String> PLATFORM_LABELS = new LinkedHashMap<>();

  static {
    PLATFORM_LABELS.put("win-x32", "Windows x32");
    PLATFORM_LABELS.put("win-x64", "Windows x64");
    PLATFORM_LABELS.put("win-arm64", "Windows arm64");
    PLATFORM_LABELS.put("win-arm64ec", "Windows arm64 E.C.");
    PLATFORM_LABELS.put("mac-x64", "MacOS x64");
    PLATFORM_LABELS.put("mac-arm64", "MacOS arm64");
    PLATFORM_LABELS.put("linux-x32", "Linux x32 / amd32");
    PLATFORM_LABELS.put("linux-x64", "Linux x64 / amd64");
    PLATFORM_LABELS.put("linux-arm32", "Linux arm32");
    PLATFORM_LABELS.put("linux-arm64", "Linux arm64");
  }

  @Autowired
  private ExploreFilterState filterState;
  @Autowired
  private ApplicationDefaults applicationDefaults;
  @Autowired
  private ExploreService exploreService;
  @Autowired
  private NewSourceDialogController newSourceDialogController;

  private SideBar sideBar;
  private VBox sourceSection;
  private VBox formatSection;
  private VBox typeSection;
  private VBox platformSection;

  private final IntegerProperty activeFilterCount = new SimpleIntegerProperty();

  @PostConstruct
  public void setup() {
    // Pre-select the platforms compatible with the current runtime, matching the previous
    // header popup's default behavior.
    filterState.getSelectedPlatformTags().addAll(
        applicationDefaults.getRuntimePlatform().getCompatiblePlatformsTags());

    sourceSection = new VBox(8);
    formatSection = new VBox(4);
    typeSection = new VBox(4);
    platformSection = new VBox(4);

    buildFormatSection();
    buildTypeSection();
    buildPlatformSection();
    rebuildSourceSection();

    ScrollPane scrollPane = new ScrollPane(buildContent());
    scrollPane.setFitToWidth(true);
    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    scrollPane.getStyleClass().add("plugin-filter-scroll");

    sideBar = new SideBar(260, scrollPane);
    sideBar.getStyleClass().add("plugin-filter-sidebar");
    sideBar.setVisible(false);
    sideBar.setPrefWidth(0);

    // Only platform tags with a visible checkbox (PLATFORM_LABELS) count as an active filter;
    // selectedPlatformTags can also hold OS-code/alias entries used only for package matching.
    activeFilterCount.bind(Bindings.createIntegerBinding(
        () -> filterState.getSelectedSourceIds().size() + filterState.getSelectedFormats().size()
            + filterState.getSelectedTypes().size()
            + (int) filterState.getSelectedPlatformTags().stream()
                .filter(PLATFORM_LABELS::containsKey).count(),
        filterState.getSelectedSourceIds(), filterState.getSelectedFormats(),
        filterState.getSelectedTypes(), filterState.getSelectedPlatformTags()));
  }

  private void buildFormatSection() {
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
      filterState.getSelectedFormats().addListener((SetChangeListener<PluginFormat>) change ->
          cb.setSelected(filterState.getSelectedFormats().contains(format)));
      formatSection.getChildren().add(cb);
    }
  }

  private void buildTypeSection() {
    for (PluginType type : PluginType.values()) {
      CheckBox cb = new CheckBox(StringUtils.capitalize(type.getText()));
      cb.setGraphic(new FontIcon(applicationDefaults.getPackageTypeIconLiteral(type)));
      cb.selectedProperty().addListener((obs, old, selected) -> {
        if (selected != filterState.getSelectedTypes().contains(type)) {
          if (selected) {
            filterState.getSelectedTypes().add(type);
          } else {
            filterState.getSelectedTypes().remove(type);
          }
        }
      });
      filterState.getSelectedTypes().addListener((SetChangeListener<PluginType>) change ->
          cb.setSelected(filterState.getSelectedTypes().contains(type)));
      typeSection.getChildren().add(cb);
    }
  }

  private void buildPlatformSection() {
    for (Entry<String, String> entry : PLATFORM_LABELS.entrySet()) {
      String tag = entry.getKey();
      CheckBox cb = new CheckBox(entry.getValue());
      cb.setGraphic(new FontIcon(platformIconLiteral(tag)));
      cb.setSelected(filterState.getSelectedPlatformTags().contains(tag));
      cb.selectedProperty().addListener((obs, old, selected) -> {
        if (selected != filterState.getSelectedPlatformTags().contains(tag)) {
          if (selected) {
            filterState.getSelectedPlatformTags().add(tag);
          } else {
            filterState.getSelectedPlatformTags().remove(tag);
          }
        }
      });
      filterState.getSelectedPlatformTags().addListener((SetChangeListener<String>) change ->
          cb.setSelected(filterState.getSelectedPlatformTags().contains(tag)));
      platformSection.getChildren().add(cb);
    }
  }

  private String platformIconLiteral(String platformTag) {
    if (platformTag.startsWith("win-")) {
      return "mdi2m-microsoft-windows";
    }
    if (platformTag.startsWith("mac-")) {
      return "mdi2a-apple";
    }
    return "mdi2l-linux";
  }

  /**
   * Rebuilds the Source section from scratch: one row per RemoteSource. Called on setup and
   * whenever a source is added, enabled/disabled or deleted, since the set of sources itself
   * (not just the current selection) can change. Also prunes the current filter selection down
   * to currently enabled sources on every call, since a source can be disabled (or deleted)
   * from elsewhere (e.g. Settings) without going through this panel's own selection checkbox.
   */
  private void rebuildSourceSection() {
    List<RemoteSource> sources = new ArrayList<>();
    exploreService.getRemoteSources().forEach(sources::add);

    Set<Long> enabledSourceIds = sources.stream()
        .filter(RemoteSource::isEnabled)
        .map(RemoteSource::getId)
        .collect(Collectors.toSet());
    filterState.getSelectedSourceIds().retainAll(enabledSourceIds);

    sourceSection.getChildren().clear();
    for (RemoteSource remoteSource : sources) {
      sourceSection.getChildren().add(buildSourceRow(remoteSource));
    }
  }

  private HBox buildSourceRow(RemoteSource remoteSource) {
    HBox row = new HBox(8);
    row.getStyleClass().add("menu-item");
    row.setAlignment(Pos.CENTER_LEFT);

    CheckBox selectCheckBox = new CheckBox();
    selectCheckBox.setSelected(filterState.getSelectedSourceIds().contains(remoteSource.getId()));
    // A disabled source's packages are already excluded from every query
    // (RemotePackageRepository.sourceEnabled()), so filtering by it would have no effect.
    selectCheckBox.setDisable(!remoteSource.isEnabled());
    selectCheckBox.selectedProperty().addListener((obs, old, selected) -> {
      if (selected) {
        filterState.getSelectedSourceIds().add(remoteSource.getId());
      } else {
        filterState.getSelectedSourceIds().remove(remoteSource.getId());
      }
    });
    row.getChildren().add(selectCheckBox);

    VBox infoPane = new VBox(2);
    HBox.setHgrow(infoPane, Priority.ALWAYS);

    Label nameLabel = new Label(remoteSource.getName());
    infoPane.getChildren().add(nameLabel);

    Label url = new Label(formatUrl(remoteSource.getDisplayUrl()));
    url.getStyleClass().add("label-disabled");
    infoPane.getChildren().add(url);

    row.getChildren().add(infoPane);

    return row;
  }

  private String formatUrl(String url) {
    if (url == null) {
      return null;
    }
    return url.replace("http://", "").replaceAll("https://", "");
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
    Label typeLabel = new Label("Category");
    typeLabel.getStyleClass().add("plugin-filter-section-title");
    Label platformLabel = new Label("Target platform");
    platformLabel.getStyleClass().add("plugin-filter-section-title");

    content.getChildren().addAll(
        header, new Separator(),
        buildSourceSectionHeader(), sourceSection,
        new Separator(), formatLabel, formatSection,
        new Separator(), typeLabel, typeSection,
        new Separator(), platformLabel, platformSection
    );
    return content;
  }

  private HBox buildSourceSectionHeader() {
    HBox header = new HBox();
    header.setAlignment(Pos.CENTER_LEFT);
    Label sourceLabel = new Label("Source");
    sourceLabel.getStyleClass().add("plugin-filter-section-title");
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    Hyperlink addLink = new Hyperlink();
    addLink.setGraphic(new FontIcon("mdi2p-plus"));
    addLink.getStyleClass().add("plugin-filter-clear");
    Tooltip.install(addLink, new Tooltip("Add a new source"));
    addLink.setOnAction(e -> {
      newSourceDialogController.show();
      newSourceDialogController.startCreateSequence();
    });
    header.getChildren().addAll(sourceLabel, spacer, addLink);
    return header;
  }

  @EventListener
  private void handle(RemoteSourceUpdatedEvent event) {
    FX.run(this::rebuildSourceSection);
  }

  public ExploreFilterState getFilterState() {
    return filterState;
  }

  public IntegerProperty activeFilterCountProperty() {
    return activeFilterCount;
  }

  public SideBar getView() {
    return sideBar;
  }
}
