package com.dropsnorz.owlplug.core.services;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import com.dropsnorz.owlplug.core.controllers.TaskBarController;
import com.dropsnorz.owlplug.core.engine.tasks.AbstractTask;
import com.dropsnorz.owlplug.core.engine.tasks.TaskResult;

import javafx.application.Platform;


@Service
public class TaskManager {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	TaskBarController taskBarController;

	AsyncListenableTaskExecutor  exec = new SimpleAsyncTaskExecutor();
	private CopyOnWriteArrayList<AbstractTask> pendingTasks = new CopyOnWriteArrayList<AbstractTask>();
	private AbstractTask currentTask= null;

	TaskManager(){

	}

	public void addTask(AbstractTask task) {

		log.debug("Task submited to queue - " + task.getClass().getName());
		pendingTasks.add(task);
		refresh(false);
		
	}

	private synchronized void refresh(boolean deleteCurrentTask) {

		if(deleteCurrentTask) {
			log.debug("Remove task from queue -  " + currentTask.getClass().getName());
			pendingTasks.remove(currentTask);
			currentTask = null;
			taskBarController.taskProgressBar.progressProperty().unbind();
			taskBarController.taskLabel.textProperty().unbind();
		}

		if(!pendingTasks.isEmpty() && currentTask == null) {
			disableError();

			this.currentTask = pendingTasks.get(0);

			log.debug("Task submitted to executor - " + currentTask.getClass().getName());
			taskBarController.taskProgressBar.progressProperty().bind(currentTask.progressProperty());
			taskBarController.taskLabel.textProperty().bind(currentTask.messageProperty());
						
			ListenableFuture<TaskResult> future = (ListenableFuture<TaskResult>) exec.submitListenable(currentTask);

			future.addCallback(new ListenableFutureCallback<TaskResult>() {

				@Override
				public void onSuccess(TaskResult result) {
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							refresh(true);
						}

					});
				}

				@Override
				public void onFailure(Throwable ex) {
					Platform.runLater(new Runnable() {

						@Override
						public void run() {
							refresh(true);
						}
					});

				}});
		}
	}

	public List<AbstractTask> getPendingTasks() {
		return Collections.unmodifiableList(pendingTasks);
	}
	
	public void triggerOnError() {
		taskBarController.taskProgressBar.getStyleClass().add("progress-bar-error");

	}
	
	public void disableError() {
		taskBarController.taskProgressBar.getStyleClass().remove("progress-bar-error");

	}


}
