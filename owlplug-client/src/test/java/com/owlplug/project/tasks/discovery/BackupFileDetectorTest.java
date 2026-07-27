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

package com.owlplug.project.tasks.discovery;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import org.junit.jupiter.api.Test;

public class BackupFileDetectorTest {

  @Test
  public void fileNameContainingBackupIsBackup() {
    File file = new File("/projects/MySong_Backup.als");
    assertTrue(BackupFileDetector.isGenericBackupFile(file));
  }

  @Test
  public void fileNameContainingBackupMixedCaseIsBackup() {
    File file = new File("/projects/MySong_BACKUP_copy.als");
    assertTrue(BackupFileDetector.isGenericBackupFile(file));
  }

  @Test
  public void fileNameEndingWithBakExtensionIsBackup() {
    File file = new File("/projects/MySong.als.bak");
    assertTrue(BackupFileDetector.isGenericBackupFile(file));
  }

  @Test
  public void pathContainingBackupFolderIsBackup() {
    File file = new File("/projects/MySong/Backup/MySong [1].als");
    assertTrue(BackupFileDetector.isGenericBackupFile(file));
  }

  @Test
  public void pathContainingBackupsFolderCaseInsensitiveIsBackup() {
    File file = new File("/projects/MySong/BACKUPS/MySong-001.rpp");
    assertTrue(BackupFileDetector.isGenericBackupFile(file));
  }

  @Test
  public void normalProjectFileIsNotBackup() {
    File file = new File("/projects/MySong/MySong.als");
    assertFalse(BackupFileDetector.isGenericBackupFile(file));
  }
}
