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

package com.owlplug.theme;

import atlantafx.base.theme.Theme;
import java.util.Objects;

public class OwlPlugDarkTheme implements Theme {

  @Override
  public String getName() {
    return "OwlPlug Dark";
  }

  @Override
  public String getUserAgentStylesheet() {
    return Objects.requireNonNull(
        OwlPlugDarkTheme.class.getResource("owlplug-dark.css")
    ).toExternalForm();
  }

  @Override
  public String getUserAgentStylesheetBSS() {
    return Objects.requireNonNull(
        OwlPlugDarkTheme.class.getResource("owlplug-dark-bss.css")
    ).toExternalForm();
  }

  @Override
  public boolean isDarkMode() {
    return true;
  }

}