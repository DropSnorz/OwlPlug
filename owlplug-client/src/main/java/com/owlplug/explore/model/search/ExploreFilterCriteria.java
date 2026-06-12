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
import org.kordamp.ikonli.javafx.FontIcon;

public class ExploreFilterCriteria {

  private Object value;
  private String textValue;
  private ExploreFilterCriteriaType filterType;
  private FontIcon icon;

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
   * @param value      - criteria value
   * @param filterType - criteria type
   * @param icon       - criteria icon displayed
   */
  public ExploreFilterCriteria(Object value, ExploreFilterCriteriaType filterType, FontIcon icon) {
    super();
    this.value = value;
    this.filterType = filterType;
    this.icon = icon;
  }

  /**
   * Creates a ExploreFilterCriteria.
   * 
   * @param value      - criteria value
   * @param filterType - criteria type
   * @param icon       - criteria icon to display
   * @param textValue  - custom text value overwriting original value toString()
   *                   conversion.
   */
  public ExploreFilterCriteria(Object value, ExploreFilterCriteriaType filterType, FontIcon icon, String textValue) {
    super();
    this.value = value;
    this.icon = icon;
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

  public FontIcon getIcon() {
    return icon;
  }

  public void setIcon(FontIcon icon) {
    this.icon = icon;
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
