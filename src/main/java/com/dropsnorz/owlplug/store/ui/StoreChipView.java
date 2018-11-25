package com.dropsnorz.owlplug.store.ui;

import com.dropsnorz.owlplug.store.model.search.StoreFilterCriteria;
import com.dropsnorz.owlplug.store.model.search.StoreFilterCriteriaType;
import com.jfoenix.controls.JFXChipView;
import java.util.HashMap;
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

		this.setConverter(new StringConverter<StoreFilterCriteria>() {
			@Override
			public String toString(StoreFilterCriteria object) {
				return object.toString();
			}

			@Override
			public StoreFilterCriteria fromString(String string) {
				StoreFilterCriteria found = suggestions.get(string);
				return found == null ? new StoreFilterCriteria(string) : found;
			}
		});

		this.getSuggestions().addAll(suggestions.values());

	}

}
