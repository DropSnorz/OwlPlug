package com.owlplug.explore.controllers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import com.owlplug.core.services.TelemetryService;
import com.owlplug.core.utils.PlatformUtils;
import com.owlplug.explore.model.PackageBundle;
import com.owlplug.explore.model.RemotePackage;
import com.owlplug.explore.model.RemoteSource;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.test.util.ReflectionTestUtils;

public class ExploreControllerTest {

  static class TestableController extends ExploreController {
    boolean errorShown;

    @Override
    void showDownloadError(RemotePackage remotePackage) {
      errorShown = true;
    }
  }

  static class RoutingController extends TestableController {
    boolean downloadInvoked;

    @Override
    public boolean downloadInstaller(PackageBundle bundle) {
      downloadInvoked = true;
      return true;
    }
  }

  static class SyncController extends TestableController {
    String resolvedUrl;

    @Override
    void resolveInstallerDownload(PackageBundle bundle, Consumer<String> onResolved) {
      onResolved.accept(resolvedUrl);
    }
  }

  private PackageBundle installerBundle() {
    RemoteSource source = new RemoteSource();
    source.setName("source");
    RemotePackage remotePackage = new RemotePackage();
    remotePackage.setName("package");
    remotePackage.setRemoteSource(source);
    PackageBundle bundle = new PackageBundle();
    bundle.setInstaller(true);
    bundle.setRemotePackage(remotePackage);
    return bundle;
  }

  private SyncController syncController(String url) {
    SyncController controller = new SyncController();
    controller.resolvedUrl = url;
    ReflectionTestUtils.setField(controller, "telemetryService", mock(TelemetryService.class));
    return controller;
  }

  @Test
  public void testDownloadInstallerResolvesAndOpensUrl() {
    try (MockedStatic<PlatformUtils> platform = mockStatic(PlatformUtils.class)) {
      assertTrue(syncController("https://example.test/plugin.dmg").downloadInstaller(installerBundle()));
      platform.verify(() -> PlatformUtils.openDefaultBrowser("https://example.test/plugin.dmg"));
    }
  }

  @Test
  public void testDownloadInstallerReportsUnresolvedUrl() {
    SyncController controller = syncController(null);

    assertTrue(controller.downloadInstaller(installerBundle()));
    assertTrue(controller.errorShown);
  }

  @Test
  public void testInstallBundleRoutesInstallerToDownload() {
    RoutingController controller = new RoutingController();
    PackageBundle bundle = new PackageBundle();
    bundle.setInstaller(true);

    assertTrue(controller.installBundle(bundle));
    assertTrue(controller.downloadInvoked);
  }
}
