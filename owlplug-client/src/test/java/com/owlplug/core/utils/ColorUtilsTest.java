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

package com.owlplug.core.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

public class ColorUtilsTest {

  @Test
  public void testShouldConvertBetweenHexAndColor() {
    assertEquals("#A8492E", ColorUtils.toHex(ColorUtils.parse("#a8492e")));
  }

  @Test
  public void testShouldFormatCssRgb() {
    assertEquals("rgb(168,73,46)", ColorUtils.toRgb(Color.web("#A8492E")));
  }

  @Test
  public void testShouldFallBackToGrayOnInvalidColor() {
    assertEquals(Color.GRAY, ColorUtils.parse("not-a-color"));
  }

  @Test
  public void testShouldComputeWcagContrastRatio() {
    assertEquals(21.0, ColorUtils.contrastRatio(Color.BLACK, Color.WHITE), 0.01);
    assertEquals(1.0, ColorUtils.contrastRatio(Color.RED, Color.RED), 0.01);
  }

  @Test
  public void testShouldKeepTextColorWhenAlreadyReadable() {
    Color base = Color.web("#D9A441");

    assertEquals(base, ColorUtils.readableTextColor(base, Color.web("#1E1E1E")));
  }

  @Test
  public void testShouldLightenTextColorUntilReadable() {
    Color dark = Color.web("#7A5230");
    Color background = Color.web("#1E1E1E");

    Color text = ColorUtils.readableTextColor(dark, background);

    assertTrue(ColorUtils.contrastRatio(text, background) >= 4.5);
  }

}
