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

package com.owlplug.project.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.owlplug.core.model.Label;
import com.owlplug.project.model.DawApplication;
import com.owlplug.project.model.DawProject;
import com.owlplug.project.model.ProjectFootprint;
import java.util.List;
import java.util.function.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProjectFilterStateTest {

  private final Label favorite = new Label("Favorite", "#A8492E");
  private final Label archived = new Label("Archived", "#7A5230");
  private ProjectFilterState state;

  @BeforeEach
  public void setUp() {
    state = new ProjectFilterState();
  }

  private static DawProject project(DawApplication application, Label... labels) {
    DawProject project = new DawProject();
    project.setApplication(application);
    ProjectFootprint footprint = new ProjectFootprint("/projects/test");
    footprint.getLabels().addAll(List.of(labels));
    project.setFootprint(footprint);
    return project;
  }

  @Test
  public void testShouldHaveNoPredicateWithoutFilter() {
    assertNull(state.predicateProperty().get());
    assertEquals(0, state.activeFilterCountProperty().get());
  }

  @Test
  public void testShouldKeepProjectsHavingAnySelectedLabel() {
    state.getSelectedLabels().add(favorite);
    Predicate<DawProject> predicate = state.predicateProperty().get();

    assertTrue(predicate.test(project(DawApplication.ABLETON, favorite)));
    assertTrue(predicate.test(project(DawApplication.REAPER, favorite, archived)));
    assertFalse(predicate.test(project(DawApplication.ABLETON, archived)));
    assertFalse(predicate.test(project(DawApplication.ABLETON)));
    // Projects synced before footprints were introduced have none
    assertFalse(predicate.test(new DawProject()));
  }

  @Test
  public void testShouldCombineApplicationAndLabelFilters() {
    state.getSelectedApplications().add(DawApplication.ABLETON);
    state.getSelectedLabels().add(favorite);
    Predicate<DawProject> predicate = state.predicateProperty().get();

    assertTrue(predicate.test(project(DawApplication.ABLETON, favorite)));
    assertFalse(predicate.test(project(DawApplication.REAPER, favorite)));
    assertFalse(predicate.test(project(DawApplication.ABLETON, archived)));
    assertEquals(2, state.activeFilterCountProperty().get());
  }

  @Test
  public void testShouldResetLabelsOnClearAll() {
    state.getSelectedLabels().add(favorite);

    state.clearAll();

    assertTrue(state.getSelectedLabels().isEmpty());
    assertNull(state.predicateProperty().get());
  }

}
