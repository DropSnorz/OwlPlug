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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import javafx.collections.ListChangeListener;
import javafx.scene.control.ListCell;
import javafx.util.StringConverter;
import org.kordamp.ikonli.javafx.FontIcon;

public class ExploreChipView extends ChipView<ExploreFilterCriteria> {

  private final ApplicationDefaults applicationDefaults;
  private final List<String> pluginCreators;

  private static final String PROMPT_TEXT = "Enter your search query by Name, Authors, Category...";

  /**
   * Creates an ExploreChipView.
   *
   * @param applicationDefaults - OwlPlug application defaults
   */
  public ExploreChipView(ApplicationDefaults applicationDefaults, List<String> pluginCreators) {
    super();
    this.applicationDefaults = applicationDefaults;
    this.pluginCreators = pluginCreators;
    this.setPromptText(PROMPT_TEXT);
    init();
  }

  private void init() {

    HashMap<String, ExploreFilterCriteria> suggestions = new LinkedHashMap<>();

    suggestions.put("Effect", new ExploreFilterCriteria(PluginType.EFFECT, ExploreFilterCriteriaType.TYPE,
        applicationDefaults.getPackageTypeIconLiteral(PluginType.EFFECT), "Effect"));
    suggestions.put("Instrument", new ExploreFilterCriteria(PluginType.INSTRUMENT, ExploreFilterCriteriaType.TYPE,
        applicationDefaults.getPackageTypeIconLiteral(PluginType.INSTRUMENT), "Instrument"));

    suggestions.put("Amp",
        new ExploreFilterCriteria("Amp", ExploreFilterCriteriaType.TAG, "mdi2t-tag-outline"));
    suggestions.put("Analog",
        new ExploreFilterCriteria("Analog", ExploreFilterCriteriaType.TAG, "mdi2t-tag-outline"));
    suggestions.put("Ambient",
        new ExploreFilterCriteria("Ambient", ExploreFilterCriteriaType.TAG, "mdi2t-tag-outline"));
    suggestions.put("Bass", new ExploreFilterCriteria("Bass", ExploreFilterCriteriaType.TAG, "mdi2t-tag-outline"));
    suggestions.put("Brass",
        new ExploreFilterCriteria("Brass", ExploreFilterCriteriaType.TAG, "mdi2t-tag-outline"));
    suggestions.put("Compressor",
        new ExploreFilterCriteria("Compressor", ExploreFilterCriteriaType.TAG, "mdi2t-tag-outline"));
    suggestions.put("Delay",
        new ExploreFilterCriteria("Delay", ExploreFilterCriteriaType.TAG, "mdi2t-tag-outline"));
    suggestions.put("Distortion",
        new ExploreFilterCriteria("Distortion", ExploreFilterCriteriaType.TAG, "mdi2t-tag-outline"));
    suggestions.put("Drum", new ExploreFilterCriteria("Drum", ExploreFilterCriteriaType.TAG, "mdi2t-tag-outline"));
    suggestions.put("Equalizer",
        new ExploreFilterCriteria("Equalizer", ExploreFilterCriteriaType.TAG, "mdi2t-tag-outline"));
    suggestions.put("Filter",
        new ExploreFilterCriteria("Filter", ExploreFilterCriteriaType.TAG, "mdi2t-tag-outline"));
    suggestions.put("Flanger",
        new ExploreFilterCriteria("Flanger", ExploreFilterCriteriaType.TAG, "mdi2t-tag-outline"));
    suggestions.put("Gate", new ExploreFilterCriteria("Gate", ExploreFilterCriteriaType.TAG, "mdi2t-tag-outline"));
    suggestions.put("Guitar",
        new ExploreFilterCriteria("Guitar", ExploreFilterCriteriaType.TAG, "mdi2t-tag-outline"));
    suggestions.put("LFO", new ExploreFilterCriteria("LFO", ExploreFilterCriteriaType.TAG, "mdi2t-tag-outline"));
    suggestions.put("Limiter",
        new ExploreFilterCriteria("Limiter", ExploreFilterCriteriaType.TAG, "mdi2t-tag-outline"));
    suggestions.put("Maximizer",
        new ExploreFilterCriteria("Maximizer", ExploreFilterCriteriaType.TAG, "mdi2t-tag-outline"));
    suggestions.put("Monophonic",
        new ExploreFilterCriteria("Monophonic", ExploreFilterCriteriaType.TAG, "mdi2t-tag-outline"));
    suggestions.put("Orchestral",
        new ExploreFilterCriteria("Orchestral", ExploreFilterCriteriaType.TAG, "mdi2t-tag-outline"));
    suggestions.put("Organ",
        new ExploreFilterCriteria("Organ", ExploreFilterCriteriaType.TAG, "mdi2t-tag-outline"));
    suggestions.put("Panner",
        new ExploreFilterCriteria("Panner", ExploreFilterCriteriaType.TAG, "mdi2t-tag-outline"));
    suggestions.put("Phaser",
        new ExploreFilterCriteria("Phaser", ExploreFilterCriteriaType.TAG, "mdi2t-tag-outline"));
    suggestions.put("Piano",
        new ExploreFilterCriteria("Piano", ExploreFilterCriteriaType.TAG, "mdi2t-tag-outline"));
    suggestions.put("Reverb",
        new ExploreFilterCriteria("Reverb", ExploreFilterCriteriaType.TAG, "mdi2t-tag-outline"));
    suggestions.put("Tremolo",
        new ExploreFilterCriteria("Tremolo", ExploreFilterCriteriaType.TAG, "mdi2t-tag-outline"));
    suggestions.put("Tube", new ExploreFilterCriteria("Tube", ExploreFilterCriteriaType.TAG, "mdi2t-tag-outline"));
    suggestions.put("Synth",
        new ExploreFilterCriteria("Synth", ExploreFilterCriteriaType.TAG, "mdi2t-tag-outline"));
    suggestions.put("Vintage",
        new ExploreFilterCriteria("Vintage", ExploreFilterCriteriaType.TAG, "mdi2t-tag-outline"));

    for (String creator : pluginCreators) {
      suggestions.put(creator, new ExploreFilterCriteria(creator, ExploreFilterCriteriaType.CREATOR,
          "mdi2a-account-group-outline"));
    }

    this.getSuggestions().addAll(suggestions.values());
    this.setConverter(new StringConverter<>() {
      @Override
      public String toString(ExploreFilterCriteria object) {
        return object.toString();
      }

      @Override
      public ExploreFilterCriteria fromString(String string) {
        String filter = string.trim();
        ExploreFilterCriteria found = suggestions.get(filter);
        return found == null ? new ExploreFilterCriteria(filter, ExploreFilterCriteriaType.NAME) : found;
      }
    });

    this.setChipFactory((chipView, criteria) -> new DefaultChip<>(chipView, criteria) {
      {
        if (getItem().getFilterType() == ExploreFilterCriteriaType.TYPE) {
          root.getStyleClass().add("chip-brown");
        }
        if (getItem().getFilterType() == ExploreFilterCriteriaType.TAG) {
          root.getStyleClass().add("chip-blue");
        }
        if (getItem().getFilterType() == ExploreFilterCriteriaType.CREATOR) {
          root.getStyleClass().add("chip-red");
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