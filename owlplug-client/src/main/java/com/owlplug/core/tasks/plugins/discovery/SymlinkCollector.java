package com.owlplug.core.tasks.plugins.discovery;

import com.owlplug.core.model.Symlink;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SymlinkCollector {

  private final Logger log = LoggerFactory.getLogger(this.getClass());
  
  private boolean uniqueReferences;
  private Set<String> collectedSymlinks;

  public SymlinkCollector(boolean uniqueReferences) {
    this.uniqueReferences = uniqueReferences;
    collectedSymlinks = new HashSet<String>();
  }

  public List<Symlink> collect(String directoryPath){

    File dir = new File(directoryPath);
    ArrayList<Symlink> linkList = new ArrayList<>();
    if (dir.isDirectory()) {
      List<File> baseFiles = (List<File>) FileUtils.listFilesAndDirs(dir, TrueFileFilter.TRUE, TrueFileFilter.TRUE);

      for(File file : baseFiles) {
        if (Files.isSymbolicLink(file.toPath())) {
          Symlink link = new Symlink(com.owlplug.core.utils.FileUtils.convertPath(file.getAbsolutePath()), file.getName(), true);
          Path targetPath;
          try {
            targetPath = Files.readSymbolicLink(file.toPath());
            link.setTargetPath(com.owlplug.core.utils.FileUtils.convertPath(targetPath.toString()));
          } catch (IOException e) {
            log.error("Error reading symlink properties: " + file.getPath(), e);
          }
          if(uniqueReferences && !collectedSymlinks.contains(file.getAbsolutePath())) {
            collectedSymlinks.add(file.getAbsolutePath());
            linkList.add(link);
          } else if (!uniqueReferences) {
            linkList.add(link);
          }

        }
      }
    }

    return linkList;
  }

}
