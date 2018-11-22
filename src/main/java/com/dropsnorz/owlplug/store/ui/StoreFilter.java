package com.dropsnorz.owlplug.store.ui;

import java.util.Objects;

public class StoreFilter {

	private String name;
	private StoreFilterType filterType;

	public StoreFilter(String name) {
		super();
		this.name = name;
		filterType = StoreFilterType.NAME;
	}

	public StoreFilter(String name, StoreFilterType filterType) {
		super();
		this.name = name;
		this.filterType = filterType;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		StoreFilter email1 = (StoreFilter) o;
		return Objects.equals(name, email1.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
	
	public enum StoreFilterType {
		NAME,
		TAG,
		TYPE
	}
}

