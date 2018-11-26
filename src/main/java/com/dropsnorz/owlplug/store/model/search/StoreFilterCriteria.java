package com.dropsnorz.owlplug.store.model.search;

import java.util.Objects;

public class StoreFilterCriteria {

	private String value;
	private StoreFilterCriteriaType filterType;

	public StoreFilterCriteria(String value) {
		super();
		this.value = value;
		filterType = StoreFilterCriteriaType.NAME;
	}

	public StoreFilterCriteria(String value, StoreFilterCriteriaType filterType) {
		super();
		this.value = value;
		this.filterType = filterType;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
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
		return value;
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

