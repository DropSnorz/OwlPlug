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
import com.owlplug.core.services.LabelService;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;

/**
 * Displays the labels of a footprint as removable badges, followed by a text field with
 * auto-completion to assign any other existing label. The edited set is mutated in place,
 * then the change callback is invoked (save, refresh...).
 */
public class LabelEditorView extends FlowPane {

  private static final int MAX_SUGGESTIONS = 8;

  private final LabelService labelService;
  private final TextField inputField = new TextField();
  private final ContextMenu suggestionMenu = new ContextMenu();
  private List<Label> availableLabels = List.of();
  private Set<Label> target;
  private Runnable onChange;

  public LabelEditorView(LabelService labelService) {
    super(4, 4);
    this.labelService = labelService;
    setAlignment(Pos.CENTER_LEFT);

    suggestionMenu.getStyleClass().add("label-suggestion-menu");
    inputField.setPromptText("Add label...");
    inputField.getStyleClass().add("label-editor-field");
    // Labels are reloaded on focus, so labels created in Settings are always suggested
    inputField.focusedProperty().addListener((obs, old, focused) -> {
      if (focused) {
        availableLabels = labelService.findAll();
        updateSuggestions();
      } else {
        suggestionMenu.hide();
      }
    });
    inputField.textProperty().addListener((obs, old, text) -> updateSuggestions());
    inputField.setOnMouseClicked(e -> updateSuggestions());
    inputField.setOnKeyPressed(e -> {
      if (e.getCode() == KeyCode.ENTER) {
        List<Label> matches = findMatches();
        if (!matches.isEmpty()) {
          assign(matches.get(0));
        }
      } else if (e.getCode() == KeyCode.ESCAPE) {
        suggestionMenu.hide();
      }
    });
    clearTarget();
  }

  /**
   * Binds the editor to a set of labels. Pass a null set to disable the editor.
   */
  public void setTarget(Set<Label> labels, Runnable onChange) {
    this.target = labels;
    this.onChange = onChange;
    inputField.setDisable(labels == null);
    inputField.clear();
    refresh();
  }

  public void clearTarget() {
    setTarget(null, null);
  }

  private void refresh() {
    getChildren().clear();
    if (target != null) {
      target.stream()
          .sorted(Comparator.comparing(Label::getName, String.CASE_INSENSITIVE_ORDER))
          .forEach(label -> getChildren().add(new LabelBadgeView(label, () -> unassign(label))));
    }
    getChildren().add(inputField);
  }

  private void assign(Label label) {
    target.add(label);
    inputField.clear();
    suggestionMenu.hide();
    refresh();
    notifyChange();
  }

  private void unassign(Label label) {
    target.remove(label);
    refresh();
    notifyChange();
  }

  private void notifyChange() {
    if (onChange != null) {
      onChange.run();
    }
  }

  /**
   * Labels not yet assigned whose name contains the typed text, starting-with matches first.
   */
  private List<Label> findMatches() {
    if (target == null) {
      return List.of();
    }
    String query = inputField.getText() == null ? "" : inputField.getText().trim().toLowerCase();
    return availableLabels.stream()
        .filter(l -> !target.contains(l))
        .filter(l -> l.getName().toLowerCase().contains(query))
        .sorted(Comparator.comparing((Label l) -> !l.getName().toLowerCase().startsWith(query)))
        .limit(MAX_SUGGESTIONS)
        .toList();
  }

  private void updateSuggestions() {
    suggestionMenu.getItems().clear();
    if (target == null || !inputField.isFocused()) {
      suggestionMenu.hide();
      return;
    }
    for (Label label : findMatches()) {
      // The whole row is the click target, not only the badge it contains
      HBox row = new HBox(new LabelBadgeView(label));
      row.setAlignment(Pos.CENTER_LEFT);
      row.setPadding(new Insets(3, 8, 3, 8));
      row.setPrefWidth(Math.max(inputField.getWidth(), 140));
      row.getStyleClass().add("label-suggestion-row");
      CustomMenuItem item = new CustomMenuItem(row, false);
      // Fired by mouse click and keyboard activation alike
      item.setOnAction(e -> assign(label));
      suggestionMenu.getItems().add(item);
    }
    if (suggestionMenu.getItems().isEmpty()) {
      suggestionMenu.hide();
    } else if (!suggestionMenu.isShowing()) {
      suggestionMenu.show(inputField, Side.BOTTOM, 0, 0);
    }
  }

}
