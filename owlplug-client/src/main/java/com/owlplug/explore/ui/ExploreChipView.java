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
import javafx.scene.image.ImageView;
import javafx.util.StringConverter;

public class ExploreChipView extends ChipView<ExploreFilterCriteria> {

  private ApplicationDefaults applicationDefaults;
  private List<String> pluginCreators;

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
        applicationDefaults.effectImage, "Effect"));
    suggestions.put("Instrument", new ExploreFilterCriteria(PluginType.INSTRUMENT, ExploreFilterCriteriaType.TYPE,
        applicationDefaults.instrumentImage, "Instrument"));
    
    suggestions.put("Amp", new ExploreFilterCriteria("Amp", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Analog",
        new ExploreFilterCriteria("Analog", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Ambient",
        new ExploreFilterCriteria("Ambient", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Bass", new ExploreFilterCriteria("Bass", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Brass",
        new ExploreFilterCriteria("Brass", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Compressor",
        new ExploreFilterCriteria("Compressor", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Delay",
        new ExploreFilterCriteria("Delay", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Distortion",
        new ExploreFilterCriteria("Distortion", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Drum", new ExploreFilterCriteria("Drum", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Equalizer",
        new ExploreFilterCriteria("Equalizer", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Filter",
        new ExploreFilterCriteria("Filter", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Flanger",
        new ExploreFilterCriteria("Flanger", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Gate", new ExploreFilterCriteria("Gate", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Guitar",
        new ExploreFilterCriteria("Guitar", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("LFO", new ExploreFilterCriteria("LFO", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Limiter",
        new ExploreFilterCriteria("Limiter", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Maximizer",
        new ExploreFilterCriteria("Maximizer", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Monophonic",
        new ExploreFilterCriteria("Monophonic", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Orchestral",
        new ExploreFilterCriteria("Orchestral", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Organ",
        new ExploreFilterCriteria("Organ", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Panner",
        new ExploreFilterCriteria("Panner", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Phaser",
        new ExploreFilterCriteria("Phaser", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Piano",
        new ExploreFilterCriteria("Piano", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Reverb",
        new ExploreFilterCriteria("Reverb", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Tremolo",
        new ExploreFilterCriteria("Tremolo", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Tube", new ExploreFilterCriteria("Tube", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Synth",
        new ExploreFilterCriteria("Synth", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Vintage",
        new ExploreFilterCriteria("Vintage", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));

    for (String creator : pluginCreators) {
      suggestions.put(creator, new ExploreFilterCriteria(creator, ExploreFilterCriteriaType.CREATOR,
          applicationDefaults.userImage));
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
          ImageView imageView = new ImageView(item.getIcon());
          imageView.setFitWidth(10);
          imageView.setFitHeight(10);
          setGraphic(imageView);
        } else {
          setGraphic(null);
          setText(null);
        }
      }
    });

  }

}
