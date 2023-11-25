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
import com.owlplug.core.model.PluginType;
import com.owlplug.explore.model.search.ExploreFilterCriteriaType;
import com.owlplug.explore.model.search.StoreFilterCriteria;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import javafx.collections.ListChangeListener;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.util.StringConverter;

public class ExploreChipView extends ChipView<StoreFilterCriteria> {

  private ApplicationDefaults applicationDefaults;
  private List<String> pluginCreators;

  private static final String PROMPT_TEXT = "Enter your search query by Name, Authors, Category...";

  /**
   * Creates a StoreChipView.
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

    HashMap<String, StoreFilterCriteria> suggestions = new LinkedHashMap<>();
    
    suggestions.put("Effect", new StoreFilterCriteria(PluginType.EFFECT, ExploreFilterCriteriaType.TYPE,
        applicationDefaults.effectImage, "Effect"));
    suggestions.put("Instrument", new StoreFilterCriteria(PluginType.INSTRUMENT, ExploreFilterCriteriaType.TYPE,
        applicationDefaults.instrumentImage, "Instrument"));
    
    suggestions.put("Amp", new StoreFilterCriteria("Amp", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Analog",
        new StoreFilterCriteria("Analog", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Ambient",
        new StoreFilterCriteria("Ambient", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Bass", new StoreFilterCriteria("Bass", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Brass",
        new StoreFilterCriteria("Brass", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Compressor",
        new StoreFilterCriteria("Compressor", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Delay",
        new StoreFilterCriteria("Delay", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Distortion",
        new StoreFilterCriteria("Distortion", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Drum", new StoreFilterCriteria("Drum", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Equalizer",
        new StoreFilterCriteria("Equalizer", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Filter",
        new StoreFilterCriteria("Filter", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Flanger",
        new StoreFilterCriteria("Flanger", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Gate", new StoreFilterCriteria("Gate", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Guitar",
        new StoreFilterCriteria("Guitar", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("LFO", new StoreFilterCriteria("LFO", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Limiter",
        new StoreFilterCriteria("Limiter", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Maximizer",
        new StoreFilterCriteria("Maximizer", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Monophonic",
        new StoreFilterCriteria("Monophonic", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Orchestral",
        new StoreFilterCriteria("Orchestral", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Organ",
        new StoreFilterCriteria("Organ", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Panner",
        new StoreFilterCriteria("Panner", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Phaser",
        new StoreFilterCriteria("Phaser", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Piano",
        new StoreFilterCriteria("Piano", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Reverb",
        new StoreFilterCriteria("Reverb", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Tremolo",
        new StoreFilterCriteria("Tremolo", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Tube", new StoreFilterCriteria("Tube", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Synth",
        new StoreFilterCriteria("Synth", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));
    suggestions.put("Vintage",
        new StoreFilterCriteria("Vintage", ExploreFilterCriteriaType.TAG, applicationDefaults.tagImage));

    for (String creator : pluginCreators) {
      suggestions.put(creator, new StoreFilterCriteria(creator, ExploreFilterCriteriaType.CREATOR,
          applicationDefaults.userImage));
    }

    this.getSuggestions().addAll(suggestions.values());
    this.setConverter(new StringConverter<>() {
      @Override
      public String toString(StoreFilterCriteria object) {
        return object.toString();
      }

      @Override
      public StoreFilterCriteria fromString(String string) {
        String filter = string.trim();
        StoreFilterCriteria found = suggestions.get(filter);
        return found == null ? new StoreFilterCriteria(filter, ExploreFilterCriteriaType.NAME) : found;
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

    this.getChips().addListener((ListChangeListener<StoreFilterCriteria>) change -> {
      // Only display prompt text if any chips is selected
      if (getChips().size() == 0) {
        this.setPromptText(PROMPT_TEXT);
      } else {
        this.setPromptText("");
      }
    });

    this.setSuggestionsCellFactory(param -> new ListCell<>() {
      protected void updateItem(StoreFilterCriteria item, boolean empty) {
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
