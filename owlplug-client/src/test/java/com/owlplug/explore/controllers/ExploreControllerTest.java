package com.owlplug.explore.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.owlplug.explore.model.PackageBundle;
import com.owlplug.explore.model.RemotePackage;
import com.owlplug.explore.services.ExploreService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

public class ExploreControllerTest {

  static class TestableController extends ExploreController {
    String openedUrl;
    boolean errorShown;
    boolean downloadInvoked;

    @Override
    void openUrl(String url) {
      openedUrl = url;
    }

    @Override
    void showDownloadError(RemotePackage remotePackage) {
      errorShown = true;
    }

    @Override
    void runOnFxThread(Runnable task) {
      task.run();
    }

    @Override
    public boolean downloadInstaller(PackageBundle bundle) {
      downloadInvoked = true;
      return true;
    }
  }

  @Test
  public void testInstallerBundleOpensResolvedUrl() {
    ExploreService exploreService = mock(ExploreService.class);
    PackageBundle bundle = new PackageBundle();
    bundle.setInstaller(true);
    when(exploreService.getBundleDownloadUrl(bundle)).thenReturn("https://example.test/plugin.dmg");

    TestableController controller = new TestableController();
    ReflectionTestUtils.setField(controller, "exploreService", exploreService);

    controller.openInstallerDownload(bundle);

    assertEquals("https://example.test/plugin.dmg", controller.openedUrl);
    assertFalse(controller.errorShown);
  }

  @Test
  public void testUnresolvedInstallerUrlShowsError() {
    ExploreService exploreService = mock(ExploreService.class);
    PackageBundle bundle = new PackageBundle();
    bundle.setInstaller(true);
    when(exploreService.getBundleDownloadUrl(bundle)).thenReturn(null);

    TestableController controller = new TestableController();
    ReflectionTestUtils.setField(controller, "exploreService", exploreService);

    controller.openInstallerDownload(bundle);

    assertNull(controller.openedUrl);
    assertTrue(controller.errorShown);
  }

  @Test
  public void testInstallBundleRoutesInstallerToDownload() {
    PackageBundle bundle = new PackageBundle();
    bundle.setInstaller(true);

    TestableController controller = new TestableController();

    assertTrue(controller.installBundle(bundle));
    assertTrue(controller.downloadInvoked);
  }
}
