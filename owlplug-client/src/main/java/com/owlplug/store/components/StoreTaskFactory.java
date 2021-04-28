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
 
package com.owlplug.store.components;

import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.components.BaseTaskFactory;
import com.owlplug.core.components.CoreTaskFactory;
import com.owlplug.core.tasks.TaskExecutionContext;
import com.owlplug.core.utils.FileUtils;
import com.owlplug.core.utils.SimpleEventListener;
import com.owlplug.store.dao.StoreDAO;
import com.owlplug.store.dao.StoreProductDAO;
import com.owlplug.store.model.ProductBundle;
import com.owlplug.store.tasks.ProductInstallTask;
import com.owlplug.store.tasks.StoreSyncTask;
import java.io.File;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StoreTaskFactory extends BaseTaskFactory {

  @Autowired
  private ApplicationDefaults applicationDefaults;
  @Autowired
  private CoreTaskFactory coreTaskFactory;
  @Autowired
  private StoreDAO pluginStoreDAO;
  @Autowired
  private StoreProductDAO storeProductDAO;

  private ArrayList<SimpleEventListener> syncStoresListeners = new ArrayList<>();


  /**
   * Creates a {@link StoreSyncTask} and binds listeners to the success callback.
   * 
   * @return
   */
  public TaskExecutionContext createStoreSyncTask() {

    StoreSyncTask task = new StoreSyncTask(pluginStoreDAO, storeProductDAO);
    task.setOnSucceeded(e -> {
      notifyListeners(syncStoresListeners);
    });
    return create(task);
  }


  /**
   * Creates a task to download and installs a product in a directory.
   * @param bundle - store bundle to retrieve
   * @param targetDirectory - store bundle to retrieve
   * @return task execution context
   */
  public TaskExecutionContext createBundleInstallTask(ProductBundle bundle, File targetDirectory) {
    String path = FileUtils.convertPath(targetDirectory.getAbsolutePath());
    return create(new ProductInstallTask(bundle, targetDirectory, applicationDefaults))
        .setOnSucceeded(e -> coreTaskFactory.createPluginSyncTask(path).scheduleNow());
  }



  public void addSyncStoresListener(SimpleEventListener eventListener) {
    syncStoresListeners.add(eventListener);
  }

  public void removeSyncStoresListener(SimpleEventListener eventListener) {
    syncStoresListeners.remove(eventListener);
  }

}
