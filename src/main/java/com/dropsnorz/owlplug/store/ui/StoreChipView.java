package com.dropsnorz.owlplug.store.ui;

import com.dropsnorz.owlplug.store.model.search.StoreFilterCriteria;
import com.dropsnorz.owlplug.store.model.search.StoreFilterCriteriaType;
import com.jfoenix.controls.JFXChipView;
import com.jfoenix.controls.JFXDefaultChip;
import java.util.HashMap;
import javafx.collections.ListChangeListener;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.util.StringConverter;

public class StoreChipView extends JFXChipView<StoreFilterCriteria> {

	public StoreChipView() {
		super();
		init();
	}

	public void init() {


		HashMap<String, StoreFilterCriteria> suggestions = new HashMap<>();
		suggestions.put("Analog", new StoreFilterCriteria("Analog", StoreFilterCriteriaType.TAG));
		suggestions.put("Compressor", new StoreFilterCriteria("Compressor", StoreFilterCriteriaType.TAG));
		suggestions.put("Filter", new StoreFilterCriteria("Filter", StoreFilterCriteriaType.TAG));
		suggestions.put("Reverb", new StoreFilterCriteria("Reverb", StoreFilterCriteriaType.TAG));
		suggestions.put("Delay", new StoreFilterCriteria("Delay", StoreFilterCriteriaType.TAG));

		suggestions.put("Effect", new StoreFilterCriteria("Effect", StoreFilterCriteriaType.TYPE));
		suggestions.put("Instrument", new StoreFilterCriteria("Instrument", StoreFilterCriteriaType.TYPE));
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
				return found == null ? new StoreFilterCriteria(filter) : found;
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

		displayPromptText();
	}

	private void displayPromptText() {
		Node textAreaNode = this.lookup(".text-area");
		if (textAreaNode instanceof TextArea) {
			TextArea textArea = (TextArea)textAreaNode;
			textArea.setPromptText("Search");
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
