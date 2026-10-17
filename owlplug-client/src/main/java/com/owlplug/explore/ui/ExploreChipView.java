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

package com.owlplug.explore.ui;

import com.owlplug.controls.ChipView;
import com.owlplug.controls.DefaultChip;
import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.explore.model.search.ExploreFilterCriteria;
import com.owlplug.explore.model.search.ExploreFilterCriteriaType;
import com.owlplug.plugin.model.PluginType;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javafx.collections.ListChangeListener;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;
import org.kordamp.ikonli.javafx.FontIcon;

public class ExploreChipView extends ChipView<ExploreFilterCriteria> {

  private final ApplicationDefaults applicationDefaults;
  // Suggestions keyed by their trimmed, lowercase label so the converter
  // resolves user input regardless of case or surrounding whitespace.
  private final Map<String, ExploreFilterCriteria> suggestionsByKey = new LinkedHashMap<>();

  private static final String PROMPT_TEXT = "Enter your search query by Name, Authors, Category...";

  /**
   * Creates an ExploreChipView.
   *
   * @param applicationDefaults - OwlPlug application defaults
   * @param tags - package tags to suggest
   * @param creators - package creators to suggest
   */
  public ExploreChipView(ApplicationDefaults applicationDefaults, List<String> tags, List<String> creators) {
    super();
    this.applicationDefaults = applicationDefaults;
    this.setPromptText(PROMPT_TEXT);
    init();
    updateSuggestions(tags, creators);
  }

  /**
   * Rebuilds the suggestions from the given tags and creators.
   * Package type suggestions are always included.
   *
   * @param tags - package tags to suggest
   * @param creators - package creators to suggest
   */
  public void updateSuggestions(List<String> tags, List<String> creators) {
    suggestionsByKey.clear();

    addSuggestion(new ExploreFilterCriteria(PluginType.EFFECT, ExploreFilterCriteriaType.TYPE,
        applicationDefaults.getPackageTypeIconLiteral(PluginType.EFFECT), "Effect"));
    addSuggestion(new ExploreFilterCriteria(PluginType.INSTRUMENT, ExploreFilterCriteriaType.TYPE,
        applicationDefaults.getPackageTypeIconLiteral(PluginType.INSTRUMENT), "Instrument"));

    for (String tag : tags) {
      if (tag != null && !tag.isBlank()) {
        addSuggestion(createTagCriteria(tag));
      }
    }

    for (String creator : creators) {
      if (creator != null && !creator.isBlank()) {
        addSuggestion(new ExploreFilterCriteria(creator, ExploreFilterCriteriaType.CREATOR,
            "mdi2a-account-group-outline"));
      }
    }

    this.getSuggestions().setAll(suggestionsByKey.values());
  }

  /**
   * Creates a tag filter criteria.
   *
   * @param tag - tag name
   * @return the tag filter criteria
   */
  public static ExploreFilterCriteria createTagCriteria(String tag) {
    return new ExploreFilterCriteria(tag.trim(), ExploreFilterCriteriaType.TAG, "mdi2t-tag-outline");
  }

  private void addSuggestion(ExploreFilterCriteria criteria) {
    // On label collision the first suggestion wins: types, then tags, then creators.
    suggestionsByKey.putIfAbsent(toKey(criteria.toString()), criteria);
  }

  private static String toKey(String label) {
    return label.trim().toLowerCase(Locale.ROOT);
  }

  private void init() {

    this.setConverter(new StringConverter<>() {
      @Override
      public String toString(ExploreFilterCriteria object) {
        return object.toString();
      }

      @Override
      public ExploreFilterCriteria fromString(String string) {
        String filter = string.trim();
        ExploreFilterCriteria found = suggestionsByKey.get(toKey(filter));
        return found == null ? new ExploreFilterCriteria(filter, ExploreFilterCriteriaType.NAME) : found;
      }
    });

    this.setChipFactory((chipView, criteria) -> new DefaultChip<>(chipView, criteria) {
      {
        if (getItem().getFilterType() == ExploreFilterCriteriaType.TYPE) {
          root.getStyleClass().add("chip-brown");
        }
        if (getItem().getFilterType() == ExploreFilterCriteriaType.TAG) {
          root.getStyleClass().add("chip-red");
        }
        if (getItem().getFilterType() == ExploreFilterCriteriaType.CREATOR) {
          root.getStyleClass().add("chip-blue");
        }
      }
    });

    this.getChips().addListener((ListChangeListener<ExploreFilterCriteria>) change -> {
      // Only display prompt text if any chips is selected
      if (getChips().size() == 0) {
        this.setPromptText(PROMPT_TEXT);
      } else {
        this.setPromptText("");
      }
    });

    this.setSuggestionsCellFactory(param -> new ListCell<>() {
      protected void updateItem(ExploreFilterCriteria item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null && !empty) {
          setText(item.toString());
          String literal = item.getIconLiteral();
          setGraphic(literal != null ? new FontIcon(literal) : null);
        } else {
          setGraphic(null);
          setText(null);
        }
      }
    });

  }

}
