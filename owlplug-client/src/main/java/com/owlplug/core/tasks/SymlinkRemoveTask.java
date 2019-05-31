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
