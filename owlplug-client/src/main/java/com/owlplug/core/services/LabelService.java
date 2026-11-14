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

import com.owlplug.core.components.ApplicationDefaults.Prefs;
import com.owlplug.core.events.LabelsChangedEvent;
import com.owlplug.core.events.PreferencesChangedEvent;
import com.owlplug.core.model.Label;
import com.owlplug.core.repositories.LabelRepository;
import com.owlplug.plugin.model.PluginFootprint;
import com.owlplug.plugin.repositories.PluginFootprintRepository;
import com.owlplug.project.model.ProjectFootprint;
import com.owlplug.project.repositories.ProjectFootprintRepository;
import jakarta.annotation.PostConstruct;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LabelService extends BaseService {

  /** Warm palette matching the OwlPlug theme, used as color picker presets. */
  public static final List<String> PALETTE = List.of(
      "#A8492E", "#D9A441", "#C97B84", "#8A9A5B", "#7A5230", "#C2A878");

  @Autowired
  private LabelRepository labelRepository;
  @Autowired
  private PluginFootprintRepository pluginFootprintRepository;
  @Autowired
  private ProjectFootprintRepository projectFootprintRepository;
  @Autowired
  private ApplicationEventPublisher publisher;

  private volatile boolean showInLists;

  /**
   * Creates default labels at startup when there is no label at all (first launch, or after every
   * label was deleted, which is how a user restores the defaults).
   */
  @PostConstruct
  public void seedDefaults() {
    showInLists = getPreferences().getBoolean(Prefs.Labels.SHOW_IN_LISTS, true);
    if (labelRepository.count() == 0) {
      labelRepository.saveAll(List.of(
          new Label("Favorite", PALETTE.get(0)),
          new Label("Mixing", PALETTE.get(1)),
          new Label("Mastering", PALETTE.get(2)),
          new Label("Trial", PALETTE.get(3)),
          new Label("Needs Review", PALETTE.get(5)),
          new Label("Archived", PALETTE.get(4))));
    }
  }

  /**
   * Cached value of the "show labels in lists" preference, read by list cells on every render.
   */
  public boolean isShowInLists() {
    return showInLists;
  }

  @EventListener
  void handle(PreferencesChangedEvent event) {
    showInLists = getPreferences().getBoolean(Prefs.Labels.SHOW_IN_LISTS, true);
  }

  public List<Label> findAll() {
    // Creation order, so new labels always appear at the bottom of lists
    return labelRepository.findAll(org.springframework.data.domain.Sort.by("id").ascending());
  }

  public boolean nameExists(String name) {
    return labelRepository.existsByNameIgnoreCase(name);
  }

  public Label save(Label label) {
    Label saved = labelRepository.save(label);
    publisher.publishEvent(new LabelsChangedEvent());
    return saved;
  }

  /**
   * Deletes a label after detaching it from every footprint (footprints own the relation).
   */
  @Transactional
  public void delete(Label label) {
    List<PluginFootprint> pluginFootprints = pluginFootprintRepository.findByLabelsId(label.getId());
    pluginFootprints.forEach(f -> f.getLabels().remove(label));
    pluginFootprintRepository.saveAll(pluginFootprints);

    List<ProjectFootprint> projectFootprints = projectFootprintRepository.findByLabelsId(label.getId());
    projectFootprints.forEach(f -> f.getLabels().remove(label));
    projectFootprintRepository.saveAll(projectFootprints);

    labelRepository.deleteById(label.getId());
    publisher.publishEvent(new LabelsChangedEvent());
  }

  public List<PluginFootprint> findOrphanPluginFootprints() {
    return pluginFootprintRepository.findOrphans();
  }

  public List<ProjectFootprint> findOrphanProjectFootprints() {
    return projectFootprintRepository.findOrphans();
  }

  public void deletePluginFootprints(List<PluginFootprint> footprints) {
    pluginFootprintRepository.deleteAll(footprints);
    publisher.publishEvent(new LabelsChangedEvent());
  }

  public void deleteProjectFootprints(List<ProjectFootprint> footprints) {
    projectFootprintRepository.deleteAll(footprints);
    publisher.publishEvent(new LabelsChangedEvent());
  }

}
