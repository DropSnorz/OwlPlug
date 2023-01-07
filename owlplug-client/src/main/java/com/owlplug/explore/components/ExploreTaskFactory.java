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
 
package com.owlplug.explore.components;

import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.components.BaseTaskFactory;
import com.owlplug.core.components.CoreTaskFactory;
import com.owlplug.core.tasks.TaskExecutionContext;
import com.owlplug.core.utils.FileUtils;
import com.owlplug.core.utils.SimpleEventListener;
import com.owlplug.explore.dao.RemotePackageDAO;
import com.owlplug.explore.dao.RemoteSourceDAO;
import com.owlplug.explore.model.PackageBundle;
import com.owlplug.explore.tasks.BundleInstallTask;
import com.owlplug.explore.tasks.SourceSyncTask;
import java.io.File;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExploreTaskFactory extends BaseTaskFactory {

  @Autowired
  private ApplicationDefaults applicationDefaults;
  @Autowired
  private CoreTaskFactory coreTaskFactory;
  @Autowired
  private RemoteSourceDAO remoteSourceDAO;
  @Autowired
  private RemotePackageDAO remotePackageDAO;

  private ArrayList<SimpleEventListener> syncSourcesListeners = new ArrayList<>();


  /**
   * Creates a {@link SourceSyncTask} and binds listeners to the success callback.
   * 
   * @return
   */
  public TaskExecutionContext createSourceSyncTask() {

    SourceSyncTask task = new SourceSyncTask(remoteSourceDAO, remotePackageDAO);
    task.setOnSucceeded(e -> {
      notifyListeners(syncSourcesListeners);
    });
    return create(task);
  }


  /**
   * Creates a task to download and installs a product in a directory.
   * @param bundle - store bundle to retrieve
   * @param targetDirectory - store bundle to retrieve
   * @return task execution context
   */
  public TaskExecutionContext createBundleInstallTask(PackageBundle bundle, File targetDirectory) {
    String path = FileUtils.convertPath(targetDirectory.getAbsolutePath());
    return create(new BundleInstallTask(bundle, targetDirectory, applicationDefaults))
        .setOnSucceeded(e -> coreTaskFactory.createPluginSyncTask(path).scheduleNow());
  }



  public void addSyncSourcesListener(SimpleEventListener eventListener) {
    syncSourcesListeners.add(eventListener);
  }

  public void removeSyncSourcesListener(SimpleEventListener eventListener) {
    syncSourcesListeners.remove(eventListener);
  }

}
