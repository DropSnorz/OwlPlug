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

package com.owlplug.plugin.components;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.owlplug.core.model.Label;
import com.owlplug.plugin.model.IPlugin;
import com.owlplug.plugin.model.Plugin;
import com.owlplug.plugin.model.PluginFootprint;
import java.util.List;
import java.util.function.Predicate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PluginFilterStateTest {

  private final Label favorite = new Label("Favorite", "#A8492E");
  private final Label archived = new Label("Archived", "#7A5230");
  private PluginFilterState state;

  @BeforeEach
  public void setUp() {
    state = new PluginFilterState();
  }

  private static Plugin pluginWithLabels(Label... labels) {
    Plugin plugin = new Plugin();
    PluginFootprint footprint = new PluginFootprint("/plugins/test.vst3");
    footprint.getLabels().addAll(List.of(labels));
    plugin.setFootprint(footprint);
    return plugin;
  }

  @Test
  public void testShouldHaveNoPredicateWithoutFilter() {
    assertNull(state.predicateProperty().get());
    assertEquals(0, state.activeFilterCountProperty().get());
  }

  @Test
  public void testShouldKeepPluginsHavingAnySelectedLabel() {
    state.getSelectedLabels().add(favorite);
    Predicate<IPlugin> predicate = state.predicateProperty().get();

    assertTrue(predicate.test(pluginWithLabels(favorite)));
    assertTrue(predicate.test(pluginWithLabels(favorite, archived)));
    assertFalse(predicate.test(pluginWithLabels(archived)));
    assertFalse(predicate.test(pluginWithLabels()));
    // Legacy plugins may have no footprint
    assertFalse(predicate.test(new Plugin()));
  }

  @Test
  public void testShouldCountSelectedLabelsAndResetOnClearAll() {
    state.getSelectedLabels().addAll(List.of(favorite, archived));
    assertEquals(2, state.activeFilterCountProperty().get());

    state.clearAll();

    assertEquals(0, state.activeFilterCountProperty().get());
    assertNull(state.predicateProperty().get());
  }

}
