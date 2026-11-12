package com.owlplug.explore.controllers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

import com.owlplug.core.utils.PlatformUtils;
import com.owlplug.explore.model.PackageBundle;
import com.owlplug.explore.model.RemotePackage;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

public class ExploreControllerTest {

  static class TestableController extends ExploreController {
    boolean errorShown;
    boolean downloadInvoked;

    @Override
    void showDownloadError(RemotePackage remotePackage) {
      errorShown = true;
    }

    @Override
    public boolean downloadInstaller(PackageBundle bundle) {
      downloadInvoked = true;
      return true;
    }
  }

  @Test
  public void testHandleInstallerDownloadOpensResolvedUrl() {
    try (MockedStatic<PlatformUtils> platform = mockStatic(PlatformUtils.class)) {
      new TestableController().handleInstallerDownload(new PackageBundle(), "https://example.test/plugin.dmg");
      platform.verify(() -> PlatformUtils.openDefaultBrowser("https://example.test/plugin.dmg"));
    }
  }

  @Test
  public void testHandleInstallerDownloadShowsErrorWhenUnresolved() {
    TestableController controller = new TestableController();

    controller.handleInstallerDownload(new PackageBundle(), null);

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
