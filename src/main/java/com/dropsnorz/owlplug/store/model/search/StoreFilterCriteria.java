package com.dropsnorz.owlplug.store.model.search;

import java.util.Objects;

public class StoreFilterCriteria {

	private String name;
	private StoreFilterCriteriaType filterType;

	public StoreFilterCriteria(String name) {
		super();
		this.name = name;
		filterType = StoreFilterCriteriaType.NAME;
	}

	public StoreFilterCriteria(String name, StoreFilterCriteriaType filterType) {
		super();
		this.name = name;
		this.filterType = filterType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public StoreFilterCriteriaType getFilterType() {
		return filterType;
	}

	public void setFilterType(StoreFilterCriteriaType filterType) {
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
		StoreFilterCriteria criteria = (StoreFilterCriteria) o;
		return Objects.equals(name, criteria.getName());
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}
}

