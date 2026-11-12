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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import com.owlplug.AppTestContext;
import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.components.ApplicationPreferences;
import com.owlplug.core.model.OperatingSystem;
import com.owlplug.core.model.RuntimePlatform;
import com.owlplug.explore.model.PackageBundle;
import com.owlplug.explore.model.PackageTag;
import com.owlplug.explore.model.RemotePackage;
import com.owlplug.explore.model.RemoteSource;
import com.owlplug.explore.model.SourceType;
import com.owlplug.explore.model.mappers.oas.OASFile;
import com.owlplug.explore.repositories.RemotePackageRepository;
import com.owlplug.explore.repositories.RemoteSourceRepository;
import com.owlplug.plugin.services.PluginService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

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

  @MockitoSpyBean
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

  private RuntimePlatform macArm64() {
    return new RuntimePlatform("mac-arm64", OperatingSystem.MAC, "arm64");
  }

  private PackageBundle bundle(boolean installer, String target) {
    PackageBundle bundle = new PackageBundle();
    bundle.setInstaller(installer);
    bundle.setTargets(List.of(target));
    return bundle;
  }

  private RemotePackage packageWith(PackageBundle... bundles) {
    RemotePackage remotePackage = new RemotePackage();
    remotePackage.setBundles(new HashSet<>(List.of(bundles)));
    return remotePackage;
  }

  @Test
  public void testFindBestBundleShouldPreferArchiveThenFallBackToInstaller() {
    when(applicationDefaults.getRuntimePlatform()).thenReturn(macArm64());
    PackageBundle installer = bundle(true, "mac-arm64");
    PackageBundle archive = bundle(false, "mac-arm64");

    assertSame(archive, exploreService.findBestBundle(packageWith(installer, archive)));
    assertSame(installer, exploreService.findBestBundle(packageWith(installer)));
  }

  @Test
  public void testFindBestBundleShouldReturnNullWhenNothingMatches() {
    when(applicationDefaults.getRuntimePlatform()).thenReturn(macArm64());

    assertNull(exploreService.findBestBundle(packageWith(bundle(false, "linux-x64"), bundle(true, "linux-x64"))));
  }

  @Test
  public void testBundleDownloadUrlShouldUseDirectUrlOrResolveOASDetailEndpoint() {
    PackageBundle direct = new PackageBundle();
    direct.setDownloadUrl("https://example.test/direct.dmg");
    assertEquals("https://example.test/direct.dmg", exploreService.getBundleDownloadUrl(direct));

    RemoteSource source = new RemoteSource();
    source.setType(SourceType.OAS_REGISTRY);
    RemotePackage remotePackage = new RemotePackage();
    remotePackage.setRemoteSource(source);
    PackageBundle lazy = new PackageBundle();
    lazy.setRemotePackage(remotePackage);
    lazy.setOrder(2);
    OASFile file = new OASFile();
    file.setUrl("https://example.test/lazy.dmg");
    doReturn(file).when(exploreService).fetchOASFile(source, remotePackage, lazy);

    assertEquals("https://example.test/lazy.dmg", exploreService.getBundleDownloadUrl(lazy));
  }

  @Test
  public void testBundleDownloadUrlShouldReturnNullWhenUnresolvable() {
    PackageBundle bundle = new PackageBundle();
    bundle.setRemotePackage(new RemotePackage());

    assertNull(exploreService.getBundleDownloadUrl(bundle));
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
