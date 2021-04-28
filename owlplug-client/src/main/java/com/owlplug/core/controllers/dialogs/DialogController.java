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

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.owlplug.core.controllers.MainController;
import javafx.scene.Node;
import javafx.scene.text.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class DialogController {

  @Autowired
  private MainController mainController;

  private JFXDialog dialog;

  /**
   * Creates a new dialog.
   * 
   * @return
   */
  public JFXDialog newDialog() {
    dialog = new JFXDialog();
    dialog.setDialogContainer(mainController.getRootPane());

    return dialog;
  }

  /**
   * Creates a new dialog.
   * 
   * @param body - dialog body
   * @return
   */
  public JFXDialog newDialog(Node body) {

    JFXDialogLayout layout = new JFXDialogLayout();
    layout.setBody(body);
    return newDialog(layout);
  }

  /**
   * Creates a new dialog.
   * 
   * @param body    - dialog body
   * @param heading - dialog header
   * @return
   */
  public JFXDialog newDialog(Node body, Node heading) {

    JFXDialogLayout layout = new JFXDialogLayout();
    layout.setBody(body);
    layout.setHeading(heading);
    return newDialog(layout);
  }

  /**
   * Creates a new dialog.
   * 
   * @param width  - dialog width
   * @param height - dialog height
   * @param body   - dialog body
   * @return the dialog
   */
  public JFXDialog newDialog(double width, double height, Node body) {

    JFXDialogLayout layout = new JFXDialogLayout();
    layout.setMaxSize(width, height);
    layout.setPrefSize(width, height);
    layout.setBody(body);

    return newDialog(layout);

  }

  /**
   * Creates a new dialog.
   * 
   * @param width   - dialog width
   * @param height  - dialog height
   * @param body    - dialog body
   * @param heading - dialog header
   * @return the dialog
   */
  public JFXDialog newDialog(double width, double height, Node body, Node heading) {

    JFXDialogLayout layout = new JFXDialogLayout();
    layout.setMaxSize(width, height);
    layout.setPrefSize(width, height);
    layout.setBody(body);
    layout.setHeading(heading);

    return newDialog(layout);

  }

  /**
   * Creates a new dialog based on dialog layout.
   * 
   * @param layout - dialog layout
   * @return the dialog
   */
  public JFXDialog newDialog(JFXDialogLayout layout) {

    newDialog();
    dialog.setContent(layout);
    return dialog;
  }

  public JFXDialog newSimpleInfoDialog(String title, String body) {

    return newSimpleInfoDialog(new Text(title), new Text(body));
  }

  /**
   * Creates a new information dialog.
   * 
   * @param title - dialog title
   * @param body  - dialog body
   * @return the dialog
   */
  public JFXDialog newSimpleInfoDialog(Node title, Node body) {
    JFXDialogLayout layout = new JFXDialogLayout();

    layout.setHeading(title);
    layout.setBody(body);

    JFXButton button = new JFXButton("Close");

    button.setOnAction(e -> {
      dialog.close();
    });

    layout.setActions(button);
    return newDialog(layout);

  }

  public JFXDialog getDialog() {
    return dialog;
  }

}
