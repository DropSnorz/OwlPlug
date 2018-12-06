package com.dropsnorz.owlplug.store.ui;

import com.dropsnorz.owlplug.core.components.ApplicationDefaults;
import com.dropsnorz.owlplug.core.model.PluginType;
import com.dropsnorz.owlplug.store.model.search.StoreFilterCriteria;
import com.dropsnorz.owlplug.store.model.search.StoreFilterCriteriaType;
import com.jfoenix.controls.JFXChipView;
import com.jfoenix.controls.JFXDefaultChip;
import com.jfoenix.controls.JFXListCell;
import java.util.HashMap;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.util.StringConverter;

public class StoreChipView extends JFXChipView<StoreFilterCriteria> {
	
	private ApplicationDefaults applicationDefaults;

	public StoreChipView(ApplicationDefaults applicationDefaults) {
		super();
		this.applicationDefaults = applicationDefaults;
		init();
	}

	public void init() {
		
		HashMap<String, StoreFilterCriteria> suggestions = new HashMap<>();
		suggestions.put("Amp", 
				new StoreFilterCriteria("Amp", StoreFilterCriteriaType.TAG, applicationDefaults.tagImage));
		suggestions.put("Analog", 
				new StoreFilterCriteria("Analog", StoreFilterCriteriaType.TAG, applicationDefaults.tagImage));
		suggestions.put("Ambient", 
				new StoreFilterCriteria("Ambient", StoreFilterCriteriaType.TAG, applicationDefaults.tagImage));
		suggestions.put("Bass", 
				new StoreFilterCriteria("Bass", StoreFilterCriteriaType.TAG, applicationDefaults.tagImage));
		suggestions.put("Brass", 
				new StoreFilterCriteria("Brass", StoreFilterCriteriaType.TAG, applicationDefaults.tagImage));
		suggestions.put("Compressor",
				new StoreFilterCriteria("Compressor", StoreFilterCriteriaType.TAG, applicationDefaults.tagImage));
		suggestions.put("Delay", 
				new StoreFilterCriteria("Delay", StoreFilterCriteriaType.TAG, applicationDefaults.tagImage));
		suggestions.put("Distortion", 
				new StoreFilterCriteria("Distortion", StoreFilterCriteriaType.TAG, applicationDefaults.tagImage));
		suggestions.put("Drum", 
				new StoreFilterCriteria("Drum", StoreFilterCriteriaType.TAG, applicationDefaults.tagImage));
		suggestions.put("Equalizer", 
				new StoreFilterCriteria("Equalizer", StoreFilterCriteriaType.TAG, applicationDefaults.tagImage));
		suggestions.put("Filter", 
				new StoreFilterCriteria("Filter", StoreFilterCriteriaType.TAG, applicationDefaults.tagImage));
		suggestions.put("Flanger", 
				new StoreFilterCriteria("Flanger", StoreFilterCriteriaType.TAG, applicationDefaults.tagImage));
		suggestions.put("Gate", 
				new StoreFilterCriteria("Gate", StoreFilterCriteriaType.TAG, applicationDefaults.tagImage));
		suggestions.put("Guitar", 
				new StoreFilterCriteria("Guitar", StoreFilterCriteriaType.TAG, applicationDefaults.tagImage));
		suggestions.put("LFO", 
				new StoreFilterCriteria("LFO", StoreFilterCriteriaType.TAG, applicationDefaults.tagImage));
		suggestions.put("Limiter", 
				new StoreFilterCriteria("Limiter", StoreFilterCriteriaType.TAG, applicationDefaults.tagImage));
		suggestions.put("Maximizer", 
				new StoreFilterCriteria("Maximizer", StoreFilterCriteriaType.TAG, applicationDefaults.tagImage));
		suggestions.put("Monophonic", 
				new StoreFilterCriteria("Monophonic", StoreFilterCriteriaType.TAG, applicationDefaults.tagImage));
		suggestions.put("Orchestral", 
				new StoreFilterCriteria("Orchestral", StoreFilterCriteriaType.TAG, applicationDefaults.tagImage));
		suggestions.put("Organ", 
				new StoreFilterCriteria("Organ", StoreFilterCriteriaType.TAG, applicationDefaults.tagImage));
		suggestions.put("Panner", 
				new StoreFilterCriteria("Panner", StoreFilterCriteriaType.TAG, applicationDefaults.tagImage));
		suggestions.put("Phaser", 
				new StoreFilterCriteria("Phaser", StoreFilterCriteriaType.TAG, applicationDefaults.tagImage));
		suggestions.put("Piano", 
				new StoreFilterCriteria("Piano", StoreFilterCriteriaType.TAG, applicationDefaults.tagImage));
		suggestions.put("Reverb", 
				new StoreFilterCriteria("Reverb", StoreFilterCriteriaType.TAG, applicationDefaults.tagImage));
		suggestions.put("Tremolo", 
				new StoreFilterCriteria("Tremolo", StoreFilterCriteriaType.TAG, applicationDefaults.tagImage));
		suggestions.put("Tube", 
				new StoreFilterCriteria("Tube", StoreFilterCriteriaType.TAG, applicationDefaults.tagImage));
		suggestions.put("Synth", 
				new StoreFilterCriteria("Synth", StoreFilterCriteriaType.TAG, applicationDefaults.tagImage));
		suggestions.put("Vintage", 
				new StoreFilterCriteria("Vintage", StoreFilterCriteriaType.TAG, applicationDefaults.tagImage));

		suggestions.put("Effect", new StoreFilterCriteria(
				PluginType.EFFECT, StoreFilterCriteriaType.TYPE, applicationDefaults.effectImage, "Effect"));
		suggestions.put("Instrument", new StoreFilterCriteria(
				PluginType.INSTRUMENT, StoreFilterCriteriaType.TYPE, applicationDefaults.instrumentImage, "Instrument"));
		
		this.getSuggestions().addAll(suggestions.values());
		this.setConverter(new StringConverter<StoreFilterCriteria>() {
			@Override
			public String toString(StoreFilterCriteria object) {
				return object.toString();
			}
			
			@Override
			public StoreFilterCriteria fromString(String string) {
				String filter = string.trim();
				StoreFilterCriteria found = suggestions.get(filter);
				return found == null ? new StoreFilterCriteria(filter, StoreFilterCriteriaType.NAME) : found;
			}
		});

		this.setChipFactory((chipView, criteria) -> new JFXDefaultChip<StoreFilterCriteria>(chipView, criteria) {
			{
				if (getItem().getFilterType() == StoreFilterCriteriaType.TYPE) {
					root.getStyleClass().add("chip-blue");
				}
				if (getItem().getFilterType() == StoreFilterCriteriaType.TAG) {
					root.getStyleClass().add("chip-red");
				}
			}
		});

		this.getChips().addListener((ListChangeListener) change -> {
			//Only display prompt text if any chips is selected
			if (getChips().size() == 0) {
				displayPromptText();
			} else {
				hidePromptText();
			}
		});
		
		this.setSuggestionsCellFactory(param -> new ListCell<StoreFilterCriteria>() {
			protected void updateItem(StoreFilterCriteria item, boolean empty) {
				super.updateItem(item, empty);
				if (item != null && !empty) {
					setText(item.toString());
					ImageView imageView = new ImageView(item.getIcon());
					imageView.setFitWidth(10);
					imageView.setFitHeight(10);
					setGraphic(imageView);
				}
			}
		});
		

		displayPromptText();
	}

	private void displayPromptText() {
		Node textAreaNode = this.lookup(".text-area");
		if (textAreaNode instanceof TextArea) {
			TextArea textArea = (TextArea)textAreaNode;
			textArea.setPromptText("Enter your search query");
		}
	}

	private void hidePromptText() {
		Node textAreaNode = this.lookup(".text-area");
		if (textAreaNode instanceof TextArea) {
			TextArea textArea = (TextArea)textAreaNode;
			textArea.setPromptText("");
		}
	}

}
