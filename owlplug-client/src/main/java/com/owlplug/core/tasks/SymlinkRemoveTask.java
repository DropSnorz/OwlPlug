/* OwlPlug
 * Copyright (C) 2019 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OwlPlug is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OwlPlug.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package com.owlplug.core.tasks;

import com.owlplug.core.model.Symlink;
import java.io.File;

public class SymlinkRemoveTask extends AbstractTask {

  protected Symlink symlink;

  public SymlinkRemoveTask(Symlink symlink) {
    this.symlink = symlink;
    setName("Remove symlink");
  }

  @Override
  protected TaskResult call() throws Exception {

    this.updateProgress(-1, 1);
    this.updateMessage("Deleting directory " + symlink.getName() + " ...");

    File symlinkFile = new File(symlink.getPath());
    
    if (!symlinkFile.delete()) {
      this.updateProgress(1, 1);
      this.updateMessage("Error deleting symlink: " + symlink.getName());
      throw new TaskException("Error deleting symlink: " + symlink.getName());
    }
    
    this.updateProgress(1, 1);
    this.updateMessage("Symlink successfully deleted");

    return null;
  }
}
