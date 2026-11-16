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

package com.owlplug.core.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.owlplug.AppTestContext;
import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.components.ApplicationPreferences;
import com.owlplug.core.model.Label;
import com.owlplug.core.repositories.LabelRepository;
import com.owlplug.plugin.model.Plugin;
import com.owlplug.plugin.model.PluginFootprint;
import com.owlplug.plugin.repositories.PluginFootprintRepository;
import com.owlplug.plugin.repositories.PluginRepository;
import com.owlplug.project.model.DawProject;
import com.owlplug.project.model.ProjectFootprint;
import com.owlplug.project.repositories.DawProjectRepository;
import com.owlplug.project.repositories.ProjectFootprintRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(classes = LabelServiceTest.Config.class, properties = {
    "spring.datasource.url=jdbc:h2:mem:label-service-test;DB_CLOSE_DELAY=-1",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LabelServiceTest extends AppTestContext {

  // Minimal context limited to the persistence layer, so the test doesn't boot the JavaFX application.
  @Configuration
  @EnableAutoConfiguration
  @EntityScan(basePackageClasses = {Label.class, Plugin.class, DawProject.class})
  @EnableJpaRepositories(basePackageClasses = {
      LabelRepository.class, PluginRepository.class, DawProjectRepository.class})
  @Import(LabelService.class)
  static class Config {
  }

  @MockitoBean
  private ApplicationDefaults applicationDefaults;
  @MockitoBean
  private ApplicationPreferences applicationPreferences;

  @Autowired
  private LabelService labelService;
  @Autowired
  private LabelRepository labelRepository;
  @Autowired
  private PluginRepository pluginRepository;
  @Autowired
  private PluginFootprintRepository pluginFootprintRepository;
  @Autowired
  private DawProjectRepository dawProjectRepository;
  @Autowired
  private ProjectFootprintRepository projectFootprintRepository;

  @BeforeEach
  public void clearData() {
    pluginRepository.deleteAll();
    dawProjectRepository.deleteAll();
    pluginFootprintRepository.deleteAll();
    projectFootprintRepository.deleteAll();
    labelRepository.deleteAll();
  }

  @Test
  public void testShouldCreateDefaultLabelsWhenNoLabelExists() {
    labelService.seedDefaults();

    List<String> names = labelService.findAll().stream().map(Label::getName).toList();
    assertTrue(names.contains("Favorite"));
    assertTrue(names.contains("Archived"));
  }

  @Test
  public void testShouldNotCreateDefaultLabelsWhenLabelsExist() {
    labelRepository.save(new Label("Mine", "#A8492E"));

    labelService.seedDefaults();

    assertEquals(1, labelService.findAll().size());
  }

  @Test
  public void testShouldListLabelsInCreationOrder() {
    labelRepository.save(new Label("Zulu", "#A8492E"));
    labelRepository.save(new Label("Alpha", "#D9A441"));
    labelRepository.save(new Label("Mike", "#8A9A5B"));

    List<String> names = labelService.findAll().stream().map(Label::getName).toList();

    assertEquals(List.of("Zulu", "Alpha", "Mike"), names);
  }

  @Test
  public void testShouldCheckNameExistenceIgnoringCase() {
    labelRepository.save(new Label("Favorite", "#A8492E"));

    assertTrue(labelService.nameExists("favorite"));
    assertFalse(labelService.nameExists("Other"));
  }

  @Test
  public void testShouldRestoreDefaultLabelsAfterDeletingEveryLabel() {
    Label last = labelRepository.save(new Label("Last", "#A8492E"));

    labelService.delete(last);
    assertTrue(labelService.findAll().isEmpty());

    // Defaults are recreated at next startup
    labelService.seedDefaults();
    assertFalse(labelService.findAll().isEmpty());
  }

  @Test
  public void testShouldDetachDeletedLabelFromFootprints() {
    Label deleted = labelRepository.save(new Label("Deleted", "#A8492E"));
    Label kept = labelRepository.save(new Label("Kept", "#D9A441"));

    PluginFootprint pluginFootprint = new PluginFootprint("/plugins/a.vst3");
    pluginFootprint.getLabels().addAll(List.of(deleted, kept));
    pluginFootprint = pluginFootprintRepository.save(pluginFootprint);
    ProjectFootprint projectFootprint = new ProjectFootprint("/projects/a.als");
    projectFootprint.getLabels().add(deleted);
    projectFootprint = projectFootprintRepository.save(projectFootprint);

    labelService.delete(deleted);

    List<String> pluginLabels = pluginFootprintRepository.findById(pluginFootprint.getId()).orElseThrow()
        .getLabels().stream().map(Label::getName).toList();
    assertEquals(List.of("Kept"), pluginLabels);
    assertTrue(projectFootprintRepository.findById(projectFootprint.getId()).orElseThrow()
        .getLabels().isEmpty());
  }

  @Test
  public void testShouldFindAndDeleteOrphanPluginFootprints() {
    PluginFootprint used = pluginFootprintRepository.save(new PluginFootprint("/plugins/used.vst3"));
    pluginFootprintRepository.save(new PluginFootprint("/plugins/orphan.vst3"));
    Plugin plugin = new Plugin();
    plugin.setName("Used");
    plugin.setPath("/plugins/used.vst3");
    plugin.setFootprint(used);
    pluginRepository.save(plugin);

    List<PluginFootprint> orphans = labelService.findOrphanPluginFootprints();

    assertEquals(List.of("/plugins/orphan.vst3"), orphans.stream().map(PluginFootprint::getPath).toList());

    labelService.deletePluginFootprints(orphans);

    assertTrue(labelService.findOrphanPluginFootprints().isEmpty());
    assertEquals(1, pluginFootprintRepository.count());
  }

  @Test
  public void testShouldFindAndDeleteOrphanProjectFootprints() {
    ProjectFootprint used = projectFootprintRepository.save(new ProjectFootprint("/projects/used.als"));
    projectFootprintRepository.save(new ProjectFootprint("/projects/orphan.als"));
    DawProject project = new DawProject();
    project.setName("Used");
    project.setPath("/projects/used.als");
    project.setFootprint(used);
    dawProjectRepository.save(project);

    List<ProjectFootprint> orphans = labelService.findOrphanProjectFootprints();

    assertEquals(List.of("/projects/orphan.als"), orphans.stream().map(ProjectFootprint::getPath).toList());

    labelService.deleteProjectFootprints(orphans);

    assertTrue(labelService.findOrphanProjectFootprints().isEmpty());
    assertEquals(1, projectFootprintRepository.count());
  }

}
