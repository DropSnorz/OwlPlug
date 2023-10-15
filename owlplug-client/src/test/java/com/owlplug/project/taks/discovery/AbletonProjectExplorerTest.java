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

package com.owlplug.project.taks.discovery;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import com.owlplug.project.model.Project;
import com.owlplug.project.model.ProjectPlugin;
import com.owlplug.project.tasks.discovery.AbletonProjectExplorer;
import java.io.File;
import org.junit.jupiter.api.Test;

public class AbletonProjectExplorerTest {

  @Test
  public void Ableton11Schema5ContainingVstAndVst3ValidProject() {
    AbletonProjectExplorer explorer = new AbletonProjectExplorer();

    File file = new File(this.getClass().getClassLoader()
            .getResource("projects/ableton/ableton11Schema5.als").getFile());

    Project project = explorer.explore(file);
    assertEquals("ableton11Schema5",project.getName());
    assertEquals(2, project.getPlugins().size());

    for(ProjectPlugin plug : project.getPlugins()) {
      System.out.println("p=" + plug.getName());
    }

    assertThat( project.getPlugins(), containsInAnyOrder(
            hasProperty("name", is("Vital")),
            hasProperty("name", is("Tunefish4"))
    ));

  }
}
