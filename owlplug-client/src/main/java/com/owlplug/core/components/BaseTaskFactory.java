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
