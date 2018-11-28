package com.dropsnorz.owlplug.store.model.search;

import java.util.Objects;

public class StoreFilterCriteria {

	private Object value;
	private String textValue;
	private StoreFilterCriteriaType filterType;

	public StoreFilterCriteria(Object value) {
		super();
		this.value = value;
		filterType = StoreFilterCriteriaType.NAME;
	}

	public StoreFilterCriteria(Object value, StoreFilterCriteriaType filterType) {
		super();
		this.value = value;
		this.filterType = filterType;
	}
	
	public StoreFilterCriteria(Object value, String textValue, StoreFilterCriteriaType filterType) {
		super();
		this.value = value;
		this.filterType = filterType;
		this.textValue = textValue;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public StoreFilterCriteriaType getFilterType() {
		return filterType;
	}

	public void setFilterType(StoreFilterCriteriaType filterType) {
		this.filterType = filterType;
	}

	@Override
	public String toString() {
		if (textValue != null) {
			return textValue;
		}
		return value.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		StoreFilterCriteria criteria = (StoreFilterCriteria) o;
		return Objects.equals(value, criteria.getValue());
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}
}

