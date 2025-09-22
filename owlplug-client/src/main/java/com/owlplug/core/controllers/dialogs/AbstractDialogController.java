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
 
package com.owlplug.core.controllers.dialogs;

import com.owlplug.controls.Dialog;
import com.owlplug.controls.DialogLayout;
import com.owlplug.core.controllers.BaseController;

public abstract class AbstractDialogController extends BaseController {
  
  private double width = -1;
  private double height = -1;
  private boolean overlayClose = true;

  private Dialog dialog;


  public AbstractDialogController() {

  }

  /**
   * Creates a new Dialog with fixed size.
   * 
   * @param width  dialog width
   * @param height dialog height
   */
  public AbstractDialogController(double width, double height) {

    this.width = width;
    this.height = height;
  }

  protected abstract DialogLayout getLayout();

  /**
   * Open and display dialog frame.
   */
  public void show() {
    onDialogShow();
    if (dialog == null) {
      if (width != -1 && height != -1) {
        dialog = this.getDialogManager().newDialog(width, height, this.getLayout());
      } else {
        dialog = this.getDialogManager().newDialog(this.getLayout());
      }
      dialog.setOverlayClose(overlayClose);
      dialog.show();
    }
  }

  /**
   * Close dialog frame.
   */
  public void close() {
    if (dialog != null) {
      dialog.close();
      dialog = null;
    }
  }

  protected void setOverlayClose(boolean overlayClose) {
    this.overlayClose = overlayClose;
  }

  protected void onDialogShow() {

  }

  protected void onDialogClose() {

  }

}
