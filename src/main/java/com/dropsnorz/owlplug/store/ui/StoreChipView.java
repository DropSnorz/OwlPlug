package com.dropsnorz.owlplug.store.ui;

import com.dropsnorz.owlplug.store.ui.StoreFilter.StoreFilterType;
import com.jfoenix.controls.JFXChipView;
import java.util.HashMap;
import javafx.util.StringConverter;

public class StoreChipView extends JFXChipView<StoreFilter> {

	public StoreChipView() {
		super();
		init();
	}

	public void init() {

		HashMap<String, StoreFilter> suggestions = new HashMap<>();
		suggestions.put("Analog", new StoreFilter("Analog", StoreFilterType.TAG));
		suggestions.put("Compressor", new StoreFilter("Compressor", StoreFilterType.TAG));
		suggestions.put("Filter", new StoreFilter("Filter", StoreFilterType.TAG));
		suggestions.put("Reverb", new StoreFilter("Reverb", StoreFilterType.TAG));
		suggestions.put("Delay", new StoreFilter("Delay", StoreFilterType.TAG));
		
		suggestions.put("Effect", new StoreFilter("Effect", StoreFilterType.TYPE));
		suggestions.put("Instrument", new StoreFilter("Instrument", StoreFilterType.TYPE));

		this.setConverter(new StringConverter<StoreFilter>() {
			@Override
			public String toString(StoreFilter object) {
				return object.toString();
			}

			@Override
			public StoreFilter fromString(String string) {
				StoreFilter found = suggestions.get(string);
				return found == null ? new StoreFilter(string) : found;
			}
		});

		this.getSuggestions().addAll(suggestions.values());

	}

}
