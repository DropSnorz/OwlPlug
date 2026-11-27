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

package com.owlplug.explore.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import com.owlplug.AppTestContext;
import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.components.ApplicationPreferences;
import com.owlplug.core.model.OperatingSystem;
import com.owlplug.core.model.RuntimePlatform;
import com.owlplug.explore.model.BundleType;
import com.owlplug.explore.model.PackageBundle;
import com.owlplug.explore.model.PackageTag;
import com.owlplug.explore.model.RemotePackage;
import com.owlplug.explore.model.RemoteSource;
import com.owlplug.explore.model.search.ExploreFilterCriteria;
import com.owlplug.explore.model.search.ExploreFilterCriteriaType;
import com.owlplug.explore.repositories.RemotePackageRepository;
import com.owlplug.explore.repositories.RemoteSourceRepository;
import com.owlplug.plugin.services.PluginService;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(classes = ExploreServiceTest.Config.class, properties = {
    "spring.datasource.url=jdbc:h2:mem:explore-service-test;DB_CLOSE_DELAY=-1",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ExploreServiceTest extends AppTestContext {

  // Minimal context limited to the Explore persistence layer, so the test
  // doesn't boot the whole JavaFX application.
  @Configuration
  @EnableAutoConfiguration
  @EntityScan(basePackageClasses = RemotePackage.class)
  @EnableJpaRepositories(basePackageClasses = RemotePackageRepository.class)
  @Import(ExploreService.class)
  static class Config {
  }

  @MockitoBean
  private ApplicationDefaults applicationDefaults;
  @MockitoBean
  private ApplicationPreferences applicationPreferences;
  @MockitoBean
  private PluginService pluginService;

  @Autowired
  private ExploreService exploreService;
  @Autowired
  private RemotePackageRepository remotePackageRepository;
  @Autowired
  private RemoteSourceRepository remoteSourceRepository;

  private RemoteSource remoteSource;

  @BeforeEach
  public void setUpData() {
    remotePackageRepository.deleteAll();
    remoteSource = new RemoteSource();
    remoteSource.setName("Test source");
    remoteSource = remoteSourceRepository.save(remoteSource);
  }

  @Test
  public void testGetDistinctTagsShouldBeOrderedByUsage() {
    savePackage("A", "Reverb", "Delay", "Synth");
    savePackage("B", "Delay", "Synth");
    savePackage("C", "Delay");

    assertEquals(List.of("Delay", "Synth", "Reverb"), exploreService.getDistinctTags());
  }

  @Test
  public void testGetDistinctTagsShouldGroupCaseAndWhitespaceVariants() {
    savePackage("A", "EQ");
    savePackage("B", "eq");
    savePackage("C", "EQ ");
    savePackage("D", " Reverb");
    savePackage("E", "Reverb");

    // "EQ" is the most frequent spelling once trimmed (2 of 3 usages)
    assertEquals(List.of("EQ", "Reverb"), exploreService.getDistinctTags());
  }

  @Test
  public void testGetDistinctTagsShouldExcludeBlankTags() {
    savePackage("A", "Synth", "", "   ", null);

    assertEquals(List.of("Synth"), exploreService.getDistinctTags());
  }

  @Test
  public void testGetDistinctTagsShouldBeEmptyWithoutPackages() {
    assertTrue(exploreService.getDistinctTags().isEmpty());
  }

  @Test
  public void testBundleTypeCriteriaShouldHideInstallerOnlyPackages() {
    savePackageWithBundles("Archive only", BundleType.ARCHIVE);
    savePackageWithBundles("Installer only", BundleType.INSTALLER);
    savePackageWithBundles("Both", BundleType.ARCHIVE, BundleType.INSTALLER);

    assertEquals(Set.of("Archive only", "Both"), searchPackageNames(List.of(BundleType.ARCHIVE)));
    assertEquals(Set.of("Installer only", "Both"), searchPackageNames(List.of(BundleType.INSTALLER)));
    assertEquals(Set.of("Archive only", "Installer only", "Both"),
        searchPackageNames(List.of(BundleType.ARCHIVE, BundleType.INSTALLER)));
  }

  @Test
  public void testFindBestBundleShouldPreferArchives() {
    when(applicationDefaults.getRuntimePlatform())
        .thenReturn(new RuntimePlatform("win-x64", OperatingSystem.WIN, "x64"));
    RemotePackage remotePackage = new RemotePackage();
    PackageBundle installer = bundle(BundleType.INSTALLER, 0);
    PackageBundle archive = bundle(BundleType.ARCHIVE, 1);
    remotePackage.setBundles(new HashSet<>(List.of(installer, archive)));

    assertSame(archive, exploreService.findBestBundle(remotePackage, EnumSet.allOf(BundleType.class)));
    assertSame(installer, exploreService.findBestBundle(remotePackage, EnumSet.of(BundleType.INSTALLER)));

    remotePackage.setBundles(new HashSet<>(List.of(installer)));
    assertNull(exploreService.findBestBundle(remotePackage, EnumSet.of(BundleType.ARCHIVE)));
  }

  private Set<String> searchPackageNames(List<BundleType> bundleTypes) {
    List<ExploreFilterCriteria> criteria = List.of(
        new ExploreFilterCriteria(bundleTypes, ExploreFilterCriteriaType.BUNDLE_TYPE_LIST));
    return exploreService.getRemotePackages(criteria, PageRequest.of(0, 10)).getContent().stream()
        .map(RemotePackage::getName)
        .collect(Collectors.toSet());
  }

  private void savePackageWithBundles(String name, BundleType... bundleTypes) {
    RemotePackage remotePackage = new RemotePackage();
    remotePackage.setName(name);
    remotePackage.setRemoteSource(remoteSource);
    Set<PackageBundle> bundles = new HashSet<>();
    for (int i = 0; i < bundleTypes.length; i++) {
      PackageBundle bundle = bundle(bundleTypes[i], i);
      bundle.setRemotePackage(remotePackage);
      bundles.add(bundle);
    }
    remotePackage.setBundles(bundles);
    remotePackageRepository.save(remotePackage);
  }

  private PackageBundle bundle(BundleType type, int order) {
    PackageBundle bundle = new PackageBundle();
    bundle.setType(type);
    bundle.setOrder(order);
    bundle.setTargets(List.of("win-x64"));
    bundle.setFormats(List.of("vst3"));
    return bundle;
  }

  // Tags are added as is, bypassing registry adapters normalization.
  private void savePackage(String name, String... tagNames) {
    RemotePackage remotePackage = new RemotePackage();
    remotePackage.setName(name);
    remotePackage.setRemoteSource(remoteSource);
    Set<PackageTag> tags = new HashSet<>();
    for (String tagName : tagNames) {
      tags.add(new PackageTag(tagName, remotePackage));
    }
    remotePackage.setTags(tags);
    remotePackageRepository.save(remotePackage);
  }

}
