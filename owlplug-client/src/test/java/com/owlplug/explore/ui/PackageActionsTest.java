package com.owlplug.explore.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.owlplug.explore.model.PackageBundle;
import org.junit.jupiter.api.Test;

public class PackageActionsTest {

  @Test
  public void testActionLabels() {
    PackageBundle installer = new PackageBundle();
    installer.setInstaller(true);
    PackageBundle archive = new PackageBundle();
    archive.setInstaller(false);

    assertEquals("Download", PackageActions.labelFor(installer));
    assertEquals("Install", PackageActions.labelFor(archive));
    assertEquals("Install", PackageActions.labelFor(null));
  }
}
