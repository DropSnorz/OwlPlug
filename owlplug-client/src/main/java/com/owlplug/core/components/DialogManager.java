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
 
package com.owlplug.core.components;

import com.owlplug.controls.Dialog;
import com.owlplug.controls.DialogLayout;
import com.owlplug.core.controllers.MainController;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Stack;

@Component
public class DialogManager {

  @Autowired
  private MainController mainController;

  private Stack<Dialog> dialogStack = new Stack<>();

  /**
   * Creates a new dialog.
   * 
   * @return
   */
  public Dialog newDialog() {
    Dialog dialog = new Dialog();
    dialog.setDialogContainer(mainController.getRootPane());
    dialogStack.push(dialog);

    dialog.setOnDialogClosed(e -> dialogStack.pop());

    return dialog;
  }


  /**
   * Creates a new dialog.
   * 
   * @param width  - dialog width
   * @param height - dialog height
   * @param body   - dialog body
   * @return the dialog
   */
  public Dialog newDialog(double width, double height, DialogLayout layout) {
    layout.setMaxSize(width, height);
    layout.setPrefSize(width, height);
    return newDialog(layout);

  }


  /**
   * Creates a new dialog based on dialog layout.
   * 
   * @param layout - dialog layout
   * @return the dialog
   */
  public Dialog newDialog(DialogLayout layout) {

    Dialog dialog = newDialog();
    dialog.setContent(layout);
    return dialog;
  }

  public Dialog newSimpleInfoDialog(String title, String body) {

    return newSimpleInfoDialog(new Text(title), new Text(body));
  }

  /**
   * Creates a new information dialog.
   * 
   * @param title - dialog title
   * @param body  - dialog body
   * @return the dialog
   */
  public Dialog newSimpleInfoDialog(Node title, Node body) {
    DialogLayout layout = new DialogLayout();
    Dialog dialog = newDialog(layout);

    layout.setHeading(title);
    layout.setBody(body);

    Button button = new Button("Close");

    button.setOnAction(e -> dialog.close());

    layout.setActions(button);
    return dialog;

  }

  public Dialog getDialog() {
    return dialogStack.peek();
  }

}
