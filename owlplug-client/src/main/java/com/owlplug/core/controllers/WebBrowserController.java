/* OwlPlug
 * Copyright (C) 2019 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OwlPlug is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OwlPlug.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.owlplug.core.controllers;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.springframework.stereotype.Controller;
import org.w3c.dom.Document;

@Controller
public class WebBrowserController extends BaseController {

  @FXML
  private Pane browserParent;
  @FXML
  private Button reloadButton;
  @FXML
  private TextField locationTextField;
  
  private WebView webView;

  /**
   * FXML initialize.
   */
  @FXML
  public void initialize() {

    Platform.runLater(() -> {
      webView = new WebView();
      browserParent.getChildren().add(webView);
      WebEngine engine = webView.getEngine();
      engine.load("https://google.com");
      engine.documentProperty().addListener(new ChangeListener<Document>() {
        @Override public void changed(ObservableValue<? extends Document> prop, Document oldDoc, Document newDoc) {
          locationTextField.setText(engine.getLocation());
        }
      });
    });
    
    reloadButton.setOnAction((e) -> {
      webView.getEngine().load(locationTextField.getText());
    });
  }
  

}
