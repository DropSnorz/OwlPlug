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

import com.owlplug.core.controllers.BaseController;
import javafx.scene.Node;

public abstract class AbstractDialogController extends BaseController {
  
  private double width = -1;
  private double height = -1;
  private boolean overlayClose = true;

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

  protected abstract Node getBody();

  protected abstract Node getHeading();

  /**
   * Open and display dialog frame.
   */
  public void show() {

    onDialogShow();

    if (width != -1 && height != -1) {
      if (this.getHeading() != null) {
        this.getDialogManager().newDialog(width, height, this.getBody(), this.getHeading());
      } else {
        this.getDialogManager().newDialog(width, height, this.getBody());
      }
    } else {
      if (this.getHeading() != null) {
        this.getDialogManager().newDialog(this.getBody(), this.getHeading());
      } else {
        this.getDialogManager().newDialog(this.getBody());
      }
    }
    this.getDialogManager().getDialog().setOverlayClose(overlayClose);
    this.getDialogManager().getDialog().show();

  }

  /**
   * Close dialog frame.
   */
  public void close() {
    onDialogClose();
    this.getDialogManager().getDialog().close();

  }

  protected void setOverlayClose(boolean overlayClose) {
    this.overlayClose = overlayClose;
  }

  protected void onDialogShow() {

  }

  protected void onDialogClose() {

  }

}
