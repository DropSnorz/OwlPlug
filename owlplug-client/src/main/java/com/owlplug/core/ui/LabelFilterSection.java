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

package com.owlplug.core.ui;

import com.owlplug.core.model.Label;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Filter sidebar section listing one checkbox per label, kept in sync with a set of selected labels.
 */
public class LabelFilterSection extends VBox {

  private final ObservableSet<Label> selected;
  private final Map<Label, CheckBox> checkBoxes = new HashMap<>();

  public LabelFilterSection(ObservableSet<Label> selected) {
    super(4);
    this.selected = selected;
    // filterState -> checkbox: external changes (e.g. "Clear all") keep checkboxes in sync
    selected.addListener((SetChangeListener<Label>) change -> {
      Label label = change.wasAdded() ? change.getElementAdded() : change.getElementRemoved();
      CheckBox cb = checkBoxes.get(label);
      if (cb != null) {
        cb.setSelected(change.wasAdded());
      }
    });
  }

  /**
   * Rebuilds the checkboxes from the available labels. Selected labels that no longer exist are dropped.
   */
  public void setLabels(List<Label> labels) {
    selected.retainAll(labels);
    checkBoxes.clear();
    getChildren().clear();
    for (Label label : labels) {
      // A checkbox without text has no gap before its graphic, so the badge sits in an HBox beside it
      CheckBox cb = new CheckBox();
      cb.setSelected(selected.contains(label));
      LabelBadgeView badge = new LabelBadgeView(label);
      badge.setCursor(Cursor.HAND);
      badge.setOnMouseClicked(e -> cb.fire());
      HBox row = new HBox(8, cb, badge);
      row.setAlignment(Pos.CENTER_LEFT);
      // checkbox -> filterState, guarded against feedback loops
      cb.selectedProperty().addListener((obs, old, isSelected) -> {
        if (isSelected != selected.contains(label)) {
          if (isSelected) {
            selected.add(label);
          } else {
            selected.remove(label);
          }
        }
      });
      checkBoxes.put(label, cb);
      getChildren().add(row);
    }
  }

}
