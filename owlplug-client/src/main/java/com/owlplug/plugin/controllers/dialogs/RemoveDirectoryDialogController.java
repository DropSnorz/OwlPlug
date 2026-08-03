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

package com.owlplug.plugin.controllers.dialogs;

import com.owlplug.controls.DialogLayout;
import com.owlplug.core.controllers.dialogs.AbstractDialogController;
import com.owlplug.core.utils.Async;
import com.owlplug.core.utils.FX;
import com.owlplug.core.utils.FileUtils;
import com.owlplug.plugin.components.PluginTaskFactory;
import com.owlplug.plugin.model.PluginDirectory;
import com.owlplug.plugin.tasks.DirectoryRemoveTask;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class RemoveDirectoryDialogController extends AbstractDialogController {

  private static final int MAX_DISPLAYED_FILES = 200;

  @Autowired
  private PluginTaskFactory taskFactory;

  private PluginDirectory directory;
  private Text descriptionText;
  private ListView<String> fileListView;

  // Guards against a slower, superseded enumeration (e.g. the dialog was closed
  // and reopened before the previous file walk completed) overwriting the
  // currently displayed result.
  private final Async.Sequence loadSequence = new Async.Sequence();

  public RemoveDirectoryDialogController() {
    super(600, 500);
  }

  @Override
  protected DialogLayout getLayout() {
    DialogLayout layout = new DialogLayout();
    layout.setHeading(new Label("Remove directory " + directory.getName()));

    VBox vbox = new VBox(10);
    vbox.setMaxWidth(Double.MAX_VALUE);

    descriptionText = new Text(baseDescription());
    TextFlow descriptionFlow = new TextFlow(descriptionText);
    vbox.getChildren().add(descriptionFlow);

    fileListView = new ListView<>();
    Label placeholder = new Label("Loading files ...");
    placeholder.getStyleClass().add("label-disabled");
    fileListView.setPlaceholder(placeholder);
    VBox.setVgrow(fileListView, Priority.ALWAYS);
    VBox.setMargin(fileListView, new Insets(10, 0, 0, 0));
    vbox.getChildren().add(fileListView);

    VBox.setVgrow(vbox, Priority.ALWAYS);
    layout.setBody(vbox);

    Button cancelButton = new Button("Cancel");
    cancelButton.setOnAction(e -> this.close());

    Button removeButton = new Button("Remove");
    removeButton.getStyleClass().add("button-danger");
    removeButton.setOnAction(e -> {
      this.close();
      taskFactory.create(new DirectoryRemoveTask(directory))
          .setOnSucceeded(x -> taskFactory.createPluginScanTask(directory.getPath()).schedule())
          .schedule();
    });

    layout.setActions(removeButton, cancelButton);
    return layout;
  }

  @Override
  protected void onDialogShow() {
    loadFileList();
  }

  public void setDirectory(PluginDirectory directory) {
    this.directory = directory;
  }

  private String baseDescription() {
    return "Do you really want to remove " + directory.getName()
        + " and all of its content ? This will permanently delete it from your hard drive.";
  }

  private void loadFileList() {
    File rootFile = new File(directory.getPath());

    fileListView.getItems().clear();
    descriptionText.setText(baseDescription());

    loadSequence.supply(() -> {
      List<File> files = FileUtils.listUniqueFilesAndDirs(rootFile).stream()
          .filter(File::isFile)
          .collect(Collectors.toList());

      long totalSize = files.stream().mapToLong(File::length).sum();
      Path rootPath = rootFile.toPath();
      List<String> relativePaths = files.stream()
          .map(file -> rootPath.relativize(file.toPath()).toString())
          .sorted()
          .collect(Collectors.toList());

      return new FileListResult(relativePaths, totalSize);
    }).thenAccept(result -> FX.run(() -> {
      descriptionText.setText("Do you really want to remove " + directory.getName()
          + " and all of its content ? This will permanently delete "
          + result.paths().size() + " files ("
          + FileUtils.humanReadableByteCount(result.totalSize(), true)
          + ") from your hard drive.");

      List<String> displayedPaths = result.paths();
      if (displayedPaths.size() > MAX_DISPLAYED_FILES) {
        displayedPaths = new ArrayList<>(displayedPaths.subList(0, MAX_DISPLAYED_FILES));
        displayedPaths.add("... and " + (result.paths().size() - MAX_DISPLAYED_FILES) + " more files");
      }

      Label emptyPlaceholder = new Label("No files found.");
      emptyPlaceholder.getStyleClass().add("label-disabled");
      fileListView.setPlaceholder(emptyPlaceholder);
      fileListView.setItems(FXCollections.observableList(displayedPaths));
    }));
  }

  private record FileListResult(List<String> paths, long totalSize) {}

}
