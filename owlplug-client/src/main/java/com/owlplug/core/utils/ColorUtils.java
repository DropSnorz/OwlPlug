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

import javafx.scene.paint.Color;

public class ColorUtils {

  // WCAG AA contrast ratio for normal text
  private static final double MIN_CONTRAST = 4.5;

  /**
   * Parses a web color (e.g. "#A8492E"), falling back to gray when the value is invalid.
   */
  public static Color parse(String color) {
    try {
      return Color.web(color);
    } catch (RuntimeException e) {
      return Color.GRAY;
    }
  }

  /**
   * Formats a color as an uppercase hex string, e.g. "#A8492E".
   */
  public static String toHex(Color color) {
    return String.format("#%02X%02X%02X", channel(color.getRed()), channel(color.getGreen()),
        channel(color.getBlue()));
  }

  /**
   * Formats a color as a CSS rgb() function, e.g. "rgb(168,73,46)".
   */
  public static String toRgb(Color color) {
    return "rgb(" + channel(color.getRed()) + "," + channel(color.getGreen()) + "," + channel(color.getBlue()) + ")";
  }

  /**
   * Text color derived from a base color: kept as is when readable on the background, otherwise
   * progressively lightened toward white until the contrast ratio reaches the WCAG AA threshold.
   */
  public static Color readableTextColor(Color base, Color background) {
    for (double mix = 0; mix <= 1.0; mix += 0.1) {
      Color candidate = base.interpolate(Color.WHITE, mix);
      if (contrastRatio(candidate, background) >= MIN_CONTRAST) {
        return candidate;
      }
    }
    return Color.WHITE;
  }

  /**
   * WCAG contrast ratio between two colors, from 1 (identical) to 21 (black on white).
   */
  public static double contrastRatio(Color a, Color b) {
    double la = luminance(a);
    double lb = luminance(b);
    return (Math.max(la, lb) + 0.05) / (Math.min(la, lb) + 0.05);
  }

  private static double luminance(Color color) {
    return 0.2126 * linear(color.getRed()) + 0.7152 * linear(color.getGreen()) + 0.0722 * linear(color.getBlue());
  }

  private static double linear(double value) {
    return value <= 0.03928 ? value / 12.92 : Math.pow((value + 0.055) / 1.055, 2.4);
  }

  private static long channel(double value) {
    return Math.round(value * 255);
  }

}
