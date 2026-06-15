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

package com.owlplug.explore.model.search;

import java.util.Objects;

public class ExploreFilterCriteria {

  private Object value;
  private String textValue;
  private ExploreFilterCriteriaType filterType;
  private String iconLiteral;

  /**
   * Creates a ExploreFilterCriteria.
   *
   * @param value      - criteria value
   * @param filterType - criteria type
   */
  public ExploreFilterCriteria(Object value, ExploreFilterCriteriaType filterType) {
    super();
    this.value = value;
    this.filterType = filterType;
  }

  /**
   * Creates a ExploreFilterCriteria.
   *
   * @param value       - criteria value
   * @param filterType  - criteria type
   * @param iconLiteral - Ikonli icon literal identifier
   */
  public ExploreFilterCriteria(Object value, ExploreFilterCriteriaType filterType, String iconLiteral) {
    super();
    this.value = value;
    this.filterType = filterType;
    this.iconLiteral = iconLiteral;
  }

  /**
   * Creates a ExploreFilterCriteria.
   *
   * @param value       - criteria value
   * @param filterType  - criteria type
   * @param iconLiteral - Ikonli icon literal identifier
   * @param textValue   - custom text value overwriting original value toString()
   *                    conversion.
   */
  public ExploreFilterCriteria(Object value, ExploreFilterCriteriaType filterType, String iconLiteral,
                               String textValue) {
    super();
    this.value = value;
    this.iconLiteral = iconLiteral;
    this.filterType = filterType;
    this.textValue = textValue;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public ExploreFilterCriteriaType getFilterType() {
    return filterType;
  }

  public void setFilterType(ExploreFilterCriteriaType filterType) {
    this.filterType = filterType;
  }

  public String getIconLiteral() {
    return iconLiteral;
  }

  public void setIconLiteral(String iconLiteral) {
    this.iconLiteral = iconLiteral;
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
    ExploreFilterCriteria criteria = (ExploreFilterCriteria) o;
    return Objects.equals(value, criteria.getValue());
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }
}