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
 
package com.owlplug.core.components;

import com.owlplug.core.tasks.AbstractTask;
import com.owlplug.core.tasks.TaskExecutionContext;
import com.owlplug.core.utils.SimpleEventListener;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseTaskFactory {
  
  @Autowired
  private TaskRunner taskRunner;
  
  public TaskExecutionContext create(AbstractTask task) {
    return buildContext(task);
  }
  
  protected TaskExecutionContext buildContext(AbstractTask task) {
    return new TaskExecutionContext(task, taskRunner);
  }
  
  protected void notifyListeners(List<SimpleEventListener> listeners) {
    for (SimpleEventListener listener : listeners) {
      listener.onAction();
    }
  }

}
