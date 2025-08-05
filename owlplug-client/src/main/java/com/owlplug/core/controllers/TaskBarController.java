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
 
package com.owlplug.core.controllers;

import com.owlplug.controls.Popup;
import com.owlplug.core.components.TaskRunner;
import com.owlplug.core.tasks.AbstractTask;
import java.util.ArrayList;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class TaskBarController extends BaseController {

  @Autowired
  private TaskRunner taskRunner;

  @FXML
  public Label taskLabel;
  @FXML
  public ProgressBar taskProgressBar;
  @FXML
  private Button taskHistoryButton;
  @FXML
  private Button logsButton;

  /**
   * FXML initialize.
   */
  public void initialize() {

    taskHistoryButton.setOnAction(e -> openTaskHistory());
    resetErrorLog();

  }

  public void setErrorLog(String title, String content) {
    logsButton.setVisible(true);
    logsButton.setManaged(true);
    logsButton.setOnAction(e -> {
      showErrorDialog(title, content);
    });
  }

  public void resetErrorLog() {
    logsButton.setManaged(false);
    logsButton.setVisible(false);
  }

  private void openTaskHistory() {

    if (!taskRunner.getTaskHistory().isEmpty()) {
      ListView<AbstractTask> list = new ListView<>();
      list.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

      ArrayList<AbstractTask> tasks = new ArrayList<>(taskRunner.getTaskHistory());
      tasks.addAll(taskRunner.getPendingTasks());
      list.getItems().addAll(tasks);

      list.setCellFactory(new Callback<ListView<AbstractTask>, ListCell<AbstractTask>>() {
        @Override
        public ListCell<AbstractTask> call(ListView<AbstractTask> param) {
          return new ListCell<>() {
            @Override
            public void updateItem(AbstractTask item, boolean empty) {
              super.updateItem(item, empty);
              if (item != null && !empty) {
                Image icon = getApplicationDefaults().taskPendingImage;
                if (item.isRunning()) {
                  icon = getApplicationDefaults().taskRunningImage;
                } else if (item.isDone()) {
                  icon = getApplicationDefaults().taskSuccessImage;
                }
                if (item.getState().equals(State.FAILED)) {
                  icon = getApplicationDefaults().taskFailImage;
                  setOnMouseClicked(e -> {
                    showErrorDialog(item.getException().getMessage(), item.getException().toString());
                  });
                }
                ImageView imageView = new ImageView(icon);
                setGraphic(imageView);
                setText(item.getName());
              }
            }
          };
        }
      });

      Popup popup = new Popup(list);
      popup.show(taskHistoryButton, Popup.PopupVPosition.BOTTOM, Popup.PopupHPosition.RIGHT);
    }

  }

  private void showErrorDialog(String title, String content) {
    this.getDialogManager().newSimpleInfoDialog(
            new Label(title), new TextArea(content)
    ).show();
  }

}
