package com.dropsnorz.owlplug.core.tasks.repositories.filesystem;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import com.dropsnorz.owlplug.core.model.FileSystemRepository;
import com.dropsnorz.owlplug.core.model.PluginRepository;
import com.dropsnorz.owlplug.core.tasks.repositories.IRepositoryStrategy;
import com.dropsnorz.owlplug.core.tasks.repositories.RepositoryStrategyParameters;

public class FileSystemRepositoryPushingStrategy implements IRepositoryStrategy {

  @Override
  public void execute(PluginRepository repository, RepositoryStrategyParameters parameters) {

    FileSystemRepository fileSystemRepository = (FileSystemRepository) repository;

    File targetDir = new File(fileSystemRepository.getRemotePath());
    File sourceDir = new File(parameters.get("target-url"));

    targetDir.mkdirs();

    try {
      FileUtils.copyDirectory(sourceDir, targetDir);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

  }

}
