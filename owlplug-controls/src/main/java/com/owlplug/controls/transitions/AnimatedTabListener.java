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

package com.owlplug.controls.transitions;

import javafx.animation.FadeTransition;
import javafx.animation.SequentialTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.util.Duration;

public class AnimatedTabListener implements ChangeListener<Tab> {

  @Override
  public void changed(ObservableValue<? extends Tab> obs, Tab oldTab, Tab newTab) {
    final Node oldContent = oldTab.getContent();
    final Node newContent = newTab.getContent();
    newTab.setContent(oldContent);
    FadeTransition fadeOut = new FadeTransition(
            Duration.seconds(0.1), oldContent);
    fadeOut.setFromValue(1.0);
    fadeOut.setToValue(0.0);
    FadeTransition fadeIn = new FadeTransition(
            Duration.seconds(0.1), newContent);
    fadeIn.setFromValue(0.0);
    fadeIn.setToValue(1.0);

    fadeOut.setOnFinished(event -> {
      newTab.setContent(newContent);
    });

    SequentialTransition crossFade = new SequentialTransition(
            fadeOut, fadeIn);
    crossFade.play();

  }
}
