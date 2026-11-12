package com.owlplug.explore.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.owlplug.explore.model.PackageBundle;
import org.junit.jupiter.api.Test;

public class PackageActionsTest {

  @Test
  public void testInstallerBundleUsesDownloadLabel() {
    PackageBundle bundle = new PackageBundle();
    bundle.setInstaller(true);
    assertEquals("Download", PackageActions.labelFor(bundle));
  }

  @Test
  public void testArchiveBundleUsesInstallLabel() {
    PackageBundle bundle = new PackageBundle();
    bundle.setInstaller(false);
    assertEquals("Install", PackageActions.labelFor(bundle));
  }

  @Test
  public void testNullBundleUsesInstallLabel() {
    assertEquals("Install", PackageActions.labelFor(null));
  }
}
