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

import java.util.Objects;
import java.util.function.Consumer;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextFormatter;

/**
 * Multiline note editor committing its content when focus is lost.
 * The note is bound to a target (a plugin or project footprint) through a commit callback.
 */
public class NoteTextArea extends TextArea {

  // Must not exceed the note column length of footprint entities (VARCHAR(2000))
  public static final int MAX_LENGTH = 2000;

  private Consumer<String> onCommit;
  private String committedNote;

  public NoteTextArea() {
    setWrapText(true);
    setPrefRowCount(4);
    setPromptText("Add a note...");
    getStyleClass().add("note-text-area");
    setDisable(true);

    // Truncate inserted text instead of rejecting the whole change, so a long paste
    // still fills the area up to the limit.
    setTextFormatter(new TextFormatter<String>(change -> {
      int overflow = change.getControlNewText().length() - MAX_LENGTH;
      if (overflow > 0) {
        String inserted = change.getText();
        if (overflow > inserted.length()) {
          return null;
        }
        change.setText(inserted.substring(0, inserted.length() - overflow));
      }
      return change;
    }));

    focusedProperty().addListener((obs, oldValue, focused) -> {
      if (!focused) {
        commit();
      }
    });
  }

  /**
   * Displays the given note and binds future commits to the given callback.
   * Any pending edit is committed to the previous target first: when the user types
   * and then selects another item, the selection change may be handled before focus is lost.
   */
  public void setTarget(String note, Consumer<String> onCommit) {
    commit();
    this.onCommit = onCommit;
    this.committedNote = note;
    setText(note == null ? "" : note);
    setDisable(false);
  }

  /**
   * Detaches the editor from any target and disables it.
   */
  public void clearTarget() {
    commit();
    this.onCommit = null;
    this.committedNote = null;
    setText("");
    setDisable(true);
  }

  private void commit() {
    if (onCommit == null) {
      return;
    }
    String text = getText();
    String note = text == null || text.isBlank() ? null : text;
    if (!Objects.equals(note, committedNote)) {
      committedNote = note;
      onCommit.accept(note);
    }
  }

}
