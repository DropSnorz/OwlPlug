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

import com.owlplug.core.components.TaskRunner;
import com.owlplug.core.tasks.AbstractTask;
import java.util.ArrayList;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Worker.State;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class TaskBarController extends BaseController {

  @Autowired
  private TaskRunner taskRunner;

  @FXML
  private Label taskLabel;
  @FXML
  private ProgressBar taskProgressBar;
  @FXML
  private Button taskHistoryButton;
  @FXML
  private Button logsButton;

  private final DoubleProperty progressProperty = new SimpleDoubleProperty();
  private final StringProperty taskNameProperty = new SimpleStringProperty();

  private Timeline progressTimeline;

  /**
   * FXML initialize.
   */
  public void initialize() {
    taskHistoryButton.setOnAction(e -> openTaskHistory());
    resetErrorLog();

    progressProperty.addListener((obs, oldVal, newVal) -> updateProgress(newVal.doubleValue()));
    taskLabel.textProperty().bind(taskNameProperty);
    taskNameProperty.set("Application is ready");
  }

  private void openTaskHistory() {
    ArrayList<AbstractTask> tasks = new ArrayList<>(taskRunner.getTaskHistory());
    tasks.addAll(taskRunner.getPendingTasks());
    if (tasks.isEmpty()) {
      return;
    }

    ContextMenu menu = new ContextMenu();
    for (AbstractTask task : tasks) {
      MenuItem item = new MenuItem(task.getName(), buildTaskIcon(task));
      if (task.getState().equals(State.FAILED)) {
        item.setOnAction(e -> showErrorDialog(
            task.getException().getMessage(), task.getException().toString()));
      }
      menu.getItems().add(item);
    }
    menu.show(taskHistoryButton, Side.TOP, 0, 0);
  }

  private FontIcon buildTaskIcon(AbstractTask task) {
    FontIcon icon;
    if (task.getState().equals(State.FAILED)) {
      icon = new FontIcon("mdi2a-alert-circle-outline");
    } else if (task.isRunning()) {
      icon = new FontIcon("mdi2s-sync");
    } else if (task.isDone()) {
      icon = new FontIcon("mdi2c-check-circle-outline");
    } else {
      icon = new FontIcon("mdi2c-clock-outline");
    }
    icon.setIconSize(14);
    return icon;
  }

  private void showErrorDialog(String title, String content) {
    this.getDialogManager().newSimpleInfoDialog(
        new Label(title), new TextArea(content)
    ).show();
  }

  public void setErrorLog(AbstractTask task, String title, String content, String summary) {
    this.getTelemetryService().event("/Error/TaskExecution", p -> {
      p.put("taskName", task.getName());
      p.put("error", title);
      p.put("summary", summary);
    });
    taskProgressBar.getStyleClass().add("progress-bar-error");
    logsButton.setVisible(true);
    logsButton.setManaged(true);
    logsButton.setOnAction(e -> showErrorDialog(title, content));
  }

  public void resetErrorLog() {
    taskProgressBar.getStyleClass().remove("progress-bar-error");
    logsButton.setManaged(false);
    logsButton.setVisible(false);
  }

  public StringProperty taskNameProperty() {
    return taskNameProperty;
  }

  public DoubleProperty progressProperty() {
    return progressProperty;
  }

  private void updateProgress(double target) {
    double current = taskProgressBar.getProgress();
    if (Double.isNaN(current)) {
      current = 0.0;
    }
    // Non-running task can report negative progress to indicate indeterminate state
    // Clamp to 0.0 for display animation purposes
    if (target < 0.0) {
      target = 0.0;
    }

    // Apply immediately if decreasing or equal
    if (target <= current) {
      if (progressTimeline != null) {
        progressTimeline.stop();
        progressTimeline = null;
      }
      taskProgressBar.setProgress(target);
      return;
    }

    // If a timeline is already running, stop it
    if (progressTimeline != null) {
      progressTimeline.stop();
    }

    // | Delta | Step-by-step                    | millis result            |
    // | :---- | :------------------------------ | :----------------------- |
    // | 0.5   | `delta/0.5 = 1 → (1−1)=0`       | `1000 + 0×4000 = 1000`   |
    // | 0.25  | `delta/0.5 = 0.5 → (1−0.5)=0.5` | `1000 + 0.5×4000 = 3000` |
    // | 0.1   | `delta/0.5 = 0.2 → (1−0.2)=0.8` | `1000 + 0.8×4000 = 4200` |
    // | 0.05  | `delta/0.5 = 0.1 → (1−0.1)=0.9` | `1000 + 0.9×4000 = 4600` |
    // | 0.0   | (edge)                          | `1000 + 1×4000 = 5000`   |
    // Increasing progress animation duration from 1s to 5s depending on delta
    // Small increments takes longer to reach target
    double delta = target - current;
    double millis = 1000 + (1 - Math.min(1, delta / 0.5)) * 4000;
    KeyValue kv = new KeyValue(taskProgressBar.progressProperty(), target, Interpolator.EASE_BOTH);
    KeyFrame kf = new KeyFrame(Duration.millis(millis), kv);

    progressTimeline = new Timeline(kf);
    progressTimeline.setOnFinished(ev -> progressTimeline = null);
    progressTimeline.play();
  }

}