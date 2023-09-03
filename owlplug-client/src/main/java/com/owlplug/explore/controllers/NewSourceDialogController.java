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
 
package com.owlplug.explore.controllers;

import com.owlplug.core.components.LazyViewRegistry;
import com.owlplug.core.controllers.IEntityCreateOrUpdate;
import com.owlplug.core.controllers.dialogs.AbstractDialogController;
import com.owlplug.explore.components.ExploreTaskFactory;
import com.owlplug.explore.model.RemoteSource;
import com.owlplug.explore.services.ExploreService;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class NewSourceDialogController extends AbstractDialogController implements IEntityCreateOrUpdate<RemoteSource> {

  @Autowired
  private LazyViewRegistry lazyViewRegistry;
  @Autowired
  private ExploreService exploreService;
  @Autowired
  private SourceMenuController sourceMenuController;
  @Autowired
  private ExploreTaskFactory exploreTaskFactory;

  @FXML
  private TextField sourceUrlTextField;
  @FXML
  private ProgressIndicator progressSpinner;
  @FXML
  private Label errorLabel;
  @FXML
  private Button owlplugSuggestionButton;
  @FXML
  private Button studiorackSuggestionButton;
  @FXML
  private Button okButton;
  @FXML
  private Button cancelButton;

  public NewSourceDialogController() {
    super(500, 200);
  }

  /**
   * FXML initialize.
   */
  public void initialize() {

    progressSpinner.setVisible(false);
    errorLabel.setVisible(false);

    owlplugSuggestionButton.setOnAction(e -> {
      sourceUrlTextField.setText(this.getApplicationDefaults().getOwlPlugRegistryUrl());
      validateAndSaveSource();
    });

    studiorackSuggestionButton.setOnAction(e -> {
      sourceUrlTextField.setText(this.getApplicationDefaults().getStudiorackRegistryUrl());
      validateAndSaveSource();
    });

    okButton.setOnAction(e -> {
      validateAndSaveSource();
    });

    cancelButton.setOnAction(e -> {
      close();
    });

  }

  @Override
  public void show() {
    super.show();

    owlplugSuggestionButton.setDisable(
        exploreService.getRemoteSourceByUrl(this.getApplicationDefaults().getOwlPlugRegistryUrl()) != null
    );

    studiorackSuggestionButton.setDisable(
        exploreService.getRemoteSourceByUrl(this.getApplicationDefaults().getStudiorackRegistryUrl()) != null
    );

  }

  @Override
  public void startCreateSequence() {
    sourceUrlTextField.setText("");
    progressSpinner.setVisible(false);
    errorLabel.setVisible(false);

  }

  @Override
  public void startUpdateSequence(RemoteSource entity) {
    throw new UnsupportedOperationException();

  }

  private void validateAndSaveSource() {

    progressSpinner.setVisible(true);
    String sourceUrl = sourceUrlTextField.getText();

    if (sourceUrl != null && !sourceUrl.isEmpty()) {
      Task<RemoteSource> task = new Task<RemoteSource>() {
        @Override
        protected RemoteSource call() throws Exception {
          return exploreService.fetchSourceFromRemoteUrl(sourceUrl);
        }
      };

      task.setOnSucceeded(e -> {
        RemoteSource pluginRemoteSource = task.getValue();
        progressSpinner.setVisible(false);
        if (pluginRemoteSource != null) {
          errorLabel.setVisible(false);
          exploreService.save(pluginRemoteSource);
          sourceMenuController.refreshView();
          close();
          this.getDialogManager().newSimpleInfoDialog("Success",
              "The plugin store " + pluginRemoteSource.getName() + " has been successfully added !").show();
          this.getAnalyticsService().pageView("/app/store/action/add");
          this.exploreTaskFactory.createSourceSyncTask().scheduleNow();

        } else {
          errorLabel.setVisible(true);
        }
      });
      new Thread(task).start();

    }

  }

  @Override
  protected Node getBody() {
    return lazyViewRegistry.getAsNode(LazyViewRegistry.NEW_SOURCE_VIEW);
  }

  @Override
  protected Node getHeading() {
    Label title = new Label("Add a new source");
    title.getStyleClass().add("heading-3");

    ImageView iv = new ImageView(this.getApplicationDefaults().serverImage);
    iv.setFitHeight(20);
    iv.setFitWidth(20);
    title.setGraphic(iv);
    return title;
  }

}
