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

import java.util.List;
import java.util.Random;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class SlidingLabel extends Label {

  private final List<String> values;
  private final Random random = new Random();

  public SlidingLabel(List<String> values) {
    this.values = values;
    // Start the animation loop
    showNextName();
  }

  private void showNextName() {
    String name;
    // Workaround to not display 2 times the same value from the list
    do {
      name = values.get(random.nextInt(values.size()));
    } while (name.equals(this.getText()) && values.size() > 1);

    this.setText(name);
    this.setTranslateY(20);
    this.setOpacity(0);

    // Fade and slide in
    TranslateTransition slideIn = new TranslateTransition(Duration.millis(500), this);
    slideIn.setFromY(20);
    slideIn.setToY(0);

    FadeTransition fadeIn = new FadeTransition(Duration.millis(500), this);
    fadeIn.setFromValue(0);
    fadeIn.setToValue(1);

    // Pause
    PauseTransition pause = new PauseTransition(Duration.seconds(2));

    // Fade and slide out
    TranslateTransition slideOut = new TranslateTransition(Duration.millis(500), this);
    slideOut.setFromY(0);
    slideOut.setToY(-20);

    FadeTransition fadeOut = new FadeTransition(Duration.millis(500), this);
    fadeOut.setFromValue(1);
    fadeOut.setToValue(0);

    // Combine and repeat
    SequentialTransition sequence = new SequentialTransition(
        new ParallelTransition(slideIn, fadeIn),
        pause,
        new ParallelTransition(slideOut, fadeOut)
    );

    sequence.setOnFinished(e -> showNextName());
    sequence.play();
  }
}