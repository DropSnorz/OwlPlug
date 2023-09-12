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

package com.owlplug.controls.skins;

import com.owlplug.controls.AutoCompletePopup;
import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.beans.InvalidationListener;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

/**
 * AutoCompletePopupSkin is an animated popup list view that allow filtering.
 * Based on JFXAutocompletePopupSkin from Jfoenix.
 * Deprecated, alternatives must be used.
 *
 */
@Deprecated
public class AutoCompletePopupSkin<T> implements Skin<AutoCompletePopup<T>> {

  private AutoCompletePopup<T> control;
  private final ListView<T> suggestionList;
  private final StackPane pane = new StackPane();
  private Scale scale;
  private Timeline showTransition;
  private boolean itemChanged = true;

  public AutoCompletePopupSkin(AutoCompletePopup<T> control) {
    this.control = control;
    suggestionList = new ListView<T>(control.getFilteredSuggestions()) {
      @Override
      protected void layoutChildren() {
        super.layoutChildren();
        if (itemChanged) {
          if (suggestionList.getItems().size() > 0) {
            suggestionList.getSelectionModel().select(0);
            suggestionList.scrollTo(0);
          }
          itemChanged = false;
        }
      }
    };
    suggestionList.setFixedCellSize(control.getFixedCellSize());
    control.fixedCellSizeProperty().addListener(observable -> suggestionList.setFixedCellSize(control.getFixedCellSize()));
    suggestionList.getItems().addListener((InvalidationListener) observable -> {
      itemChanged = true;
      updateListHeight();
    });
    suggestionList.getStyleClass().add("autocomplete-list");
    control.suggestionsCellFactoryProperty().addListener((o, oldVal, newVal) -> {
      if (newVal != null) {
        suggestionList.setCellFactory(newVal);
      }
    });
    if (control.getSuggestionsCellFactory() != null) {
      suggestionList.setCellFactory(control.getSuggestionsCellFactory());
    }
    pane.getChildren().add(new Group(suggestionList));
    pane.getStyleClass().add("autocomplete-container");
    suggestionList.prefWidthProperty().bind(control.prefWidthProperty());
    suggestionList.maxWidthProperty().bind(control.maxWidthProperty());
    suggestionList.minWidthProperty().bind(control.minWidthProperty());
    registerEventListener();
  }

  private void registerEventListener() {
    suggestionList.setOnMouseClicked(me -> {
      if (me.getButton() == MouseButton.PRIMARY) {
        selectItem();
        getSkinnable().hide();
      }
    });
    control.showingProperty().addListener((o, oldVal, newVal) -> {
      if (newVal) {
        animate();
      }
    });

    suggestionList.setOnKeyPressed(event -> {
      switch (event.getCode()) {
      case ENTER:
        selectItem();
        getSkinnable().hide();
        break;
      case ESCAPE:
        getSkinnable().hide();
        break;
      default:
        break;
      }
    });
  }

  public void animate() {
    updateListHeight();
    if (showTransition == null || showTransition.getStatus().equals(Status.STOPPED)) {
      if (scale == null) {
        scale = new Scale(1, 0);
        pane.getTransforms().add(scale);
      }
      scale.setY(0);
      suggestionList.setOpacity(0);
      scale.setPivotX(pane.getLayoutBounds().getWidth() / 2);
      showTransition = new Timeline(new KeyFrame(Duration.millis(120),
              new KeyValue(scale.yProperty(), 1, Interpolator.EASE_BOTH)));
      showTransition.setOnFinished((finish) -> {
        Group vf = (Group) suggestionList.lookup(".sheet");
        ParallelTransition trans = new ParallelTransition();
        for (int i = 0; i < vf.getChildren().size(); i++) {
          ListCell<T> cell = (ListCell<T>) vf.getChildren().get(i);
          int index = cell.getIndex();
          if (index > -1) {
            cell.setOpacity(0);
            cell.setTranslateY(-suggestionList.getFixedCellSize() / 8);
            Timeline f = new Timeline(new KeyFrame(Duration.millis(120),
                    end -> {
                      cell.setOpacity(1);
                      cell.setTranslateY(0);
                    },
                    new KeyValue(cell.opacityProperty(), 1, Interpolator.EASE_BOTH),
                    new KeyValue(cell.translateYProperty(), 0, Interpolator.EASE_BOTH)));
            f.setDelay(Duration.millis(index * 20));
            trans.getChildren().add(f);
          }
        }
        suggestionList.setOpacity(1);
        trans.play();
      });
      showTransition.play();
    }
  }

  private void updateListHeight() {
    final double height = Math.min(suggestionList.getItems().size(), getSkinnable().getCellLimit())
            * suggestionList.getFixedCellSize();
    suggestionList.setPrefHeight(height + suggestionList.getFixedCellSize() / 2);
  }

  private void selectItem() {
    T item = suggestionList.getSelectionModel().getSelectedItem();
    if (item == null) {
      suggestionList.getSelectionModel().select(0);
      item = suggestionList.getSelectionModel().getSelectedItem();
    }
    if (item != null) {
      control.getSelectionHandler().handle(new AutoCompletePopup.AutoCompleteEvent<T>(AutoCompletePopup.AutoCompleteEvent.SELECTION, item));
    }
  }

  @Override
  public Node getNode() {
    return pane;
  }

  @Override
  public AutoCompletePopup<T> getSkinnable() {
    return control;
  }

  @Override
  public void dispose() {
    this.control = null;
    if (showTransition != null) {
      showTransition.stop();
      showTransition.getKeyFrames().clear();
      showTransition = null;
    }
  }
}