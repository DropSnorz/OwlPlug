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
import com.owlplug.core.utils.ColorUtils;
import java.util.Collection;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * Small colored badge displaying a {@link Label}.
 */
public class LabelBadgeView extends HBox {

  private static final Color BACKGROUND_BASE = Color.web("#1E1E1E");
  // Share of the dark base in the background (the rest is the label color)
  private static final double BASE_WEIGHT = 0.65;

  public LabelBadgeView(Label label) {
    this(label, null);
  }

  /**
   * Creates a badge with a remove cross when onRemove is not null.
   */
  public LabelBadgeView(Label label, Runnable onRemove) {
    super(3);
    setAlignment(Pos.CENTER_LEFT);
    getStyleClass().add("label-badge");
    setMaxHeight(USE_PREF_SIZE);

    javafx.scene.control.Label text = new javafx.scene.control.Label(label.getName());
    getChildren().add(text);

    Color color = ColorUtils.parse(label.getColor());
    Color background = color.interpolate(BACKGROUND_BASE, BASE_WEIGHT);
    Color textColor = ColorUtils.readableTextColor(color, background);
    // -label-badge-text is a looked-up color read by the remove icon (see .label-badge-remove in owlplug.css)
    // Due to a weird rendering issue with FontIcon, we cannot set the text color directly on the icon.
    // Otherwise, the icon renders as a default missing square "[]".
    setStyle("-fx-background-color: " + ColorUtils.toRgb(background)
        + "; -label-badge-text: " + ColorUtils.toRgb(textColor) + ";");
    text.setStyle("-fx-text-fill: " + ColorUtils.toRgb(textColor) + ";");

    if (onRemove != null) {
      FontIcon cross = new FontIcon("mdi2c-close");
      cross.setIconSize(10);
      // Never call setStyle on a FontIcon: it holds its font settings in its own style
      cross.getStyleClass().add("label-badge-remove");
      cross.setOnMouseClicked(e -> {
        e.consume();
        onRemove.run();
      });
      getChildren().add(cross);
    }
  }

  /**
   * Builds a badge for each label, sorted by name.
   */
  public static java.util.List<LabelBadgeView> of(Collection<Label> labels) {
    return labels.stream()
        .sorted(java.util.Comparator.comparing(Label::getName, String.CASE_INSENSITIVE_ORDER))
        .map(LabelBadgeView::new)
        .toList();
  }

}
