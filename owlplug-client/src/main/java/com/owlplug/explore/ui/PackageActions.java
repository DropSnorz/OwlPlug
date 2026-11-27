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

package com.owlplug.explore.ui;

import com.owlplug.explore.model.PackageBundle;

public final class PackageActions {

  public static final String INSTALL = "Install";
  public static final String DOWNLOAD = "Download";

  private PackageActions() {
  }

  /**
   * Returns the action label for a bundle: installers are downloaded and opened externally,
   * archives are installed by OwlPlug.
   *
   * @param bundle bundle to label, may be null
   * @return the action label
   */
  public static String labelFor(PackageBundle bundle) {
    return bundle != null && bundle.isInstaller() ? DOWNLOAD : INSTALL;
  }
}
