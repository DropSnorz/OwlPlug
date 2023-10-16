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

package com.owlplug.project.controllers;

import com.owlplug.core.controllers.BaseController;
import com.owlplug.core.utils.PlatformUtils;
import com.owlplug.project.model.Project;
import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.springframework.stereotype.Controller;

@Controller
public class ProjectInfoController extends BaseController {

  @FXML
  private VBox projectInfoPane;
  @FXML
  private Label projectNameLabel;
  @FXML
  private Label projectAppLabel;
  @FXML
  private Label appFullNameLabel;
  @FXML
  private Label projectPathLabel;
  @FXML
  private Button openDirectoryButton;


  @FXML
  public void initialize() {
    openDirectoryButton.setOnAction(e -> {
      File projectFile = new File(projectPathLabel.getText());
      PlatformUtils.openDirectoryExplorer(projectFile.getParentFile());
    });

    // Set invisible by default if no project is selected.
    projectInfoPane.setVisible(false);

  }

  public void setProject(Project project) {
    projectInfoPane.setVisible(true);
    projectNameLabel.setText(project.getName());
    projectAppLabel.setText(project.getApplication().getName());
    appFullNameLabel.setText(project.getAppFullName());
    projectPathLabel.setText(project.getPath());

  }

}
