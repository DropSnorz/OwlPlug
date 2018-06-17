package com.dropsnorz.owlplug.core.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dropsnorz.owlplug.core.controllers.PluginsController;
import com.dropsnorz.owlplug.core.dao.PluginDAO;
import com.dropsnorz.owlplug.core.dao.PluginRepositoryDAO;
import com.dropsnorz.owlplug.core.engine.repositories.IRepositoryStrategy;
import com.dropsnorz.owlplug.core.engine.repositories.RepositoryStrategyParameters;
import com.dropsnorz.owlplug.core.engine.repositories.RepositoryStrategyResolver;
import com.dropsnorz.owlplug.core.engine.tasks.AbstractTask;
import com.dropsnorz.owlplug.core.engine.tasks.DirectoryRemoveTask;
import com.dropsnorz.owlplug.core.engine.tasks.PluginRemoveTask;
import com.dropsnorz.owlplug.core.engine.tasks.RepositoryRemoveTask;
import com.dropsnorz.owlplug.core.engine.tasks.RepositoryTask;
import com.dropsnorz.owlplug.core.engine.tasks.SyncPluginTask;
import com.dropsnorz.owlplug.core.engine.tasks.TaskResult;
import com.dropsnorz.owlplug.core.model.Plugin;
import com.dropsnorz.owlplug.core.model.PluginDirectory;
import com.dropsnorz.owlplug.core.model.PluginRepository;

import javafx.concurrent.Task;

@Service
public class TaskFactory {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PluginService pluginService;
	@Autowired
	private TaskManager taskManager;
	@Autowired
	private PluginDAO pluginDAO;
	@Autowired 
	private PluginRepositoryDAO pluginRepositoryDAO;
	@Autowired
	protected RepositoryStrategyResolver repositoryStrategyResolver;
	@Autowired
	PluginsController pluginsController;

	
	public void run(AbstractTask task) {
		taskManager.addTask(task);
	}

	public SyncPluginTask createSyncPluginTask() {

		SyncPluginTask task = new SyncPluginTask(pluginService, pluginDAO);
		task.setOnSucceeded(e -> {
			pluginsController.refreshPlugins();
		});
		bindOnFailHandler(task);

		return task;
	}

	public PluginRemoveTask createPluginRemoveTask(Plugin plugin) {

		PluginRemoveTask task = new PluginRemoveTask(plugin, pluginDAO);
		task.setOnSucceeded(e -> {
			pluginsController.refreshPlugins();
		});

		return task;
	}

	public DirectoryRemoveTask createDirectoryRemoveTask(PluginDirectory pluginDirectory) {

		DirectoryRemoveTask task = new DirectoryRemoveTask(pluginDirectory);
		task.setOnSucceeded(e -> {
			taskManager.addTask(createSyncPluginTask());
		});

		return task;
	}

	public RepositoryRemoveTask createRepositoryRemoveTask(PluginRepository repository, String path) {

		RepositoryRemoveTask task = new RepositoryRemoveTask(pluginRepositoryDAO, repository, path);
		task.setOnSucceeded(e -> {
			taskManager.addTask(createSyncPluginTask());
		});

		return task;
	}


	public RepositoryTask createRepositoryTask(PluginRepository repository, RepositoryStrategyParameters parameters) {

		IRepositoryStrategy strategy = repositoryStrategyResolver.getStrategy(repository, parameters);

		RepositoryTask task = new RepositoryTask() {
			@Override
			protected TaskResult call() throws Exception {

				this.updateProgress(0, 1);
				strategy.execute(repository, parameters);
				this.updateProgress(1, 1);

				return null;
			}
		};
		task.setOnSucceeded(e -> {
			taskManager.addTask(createSyncPluginTask());
		});

		return task;

	}

	private void bindOnFailHandler(Task task) {

		task.setOnFailed(e -> {
			taskManager.triggerOnError();
		});
	}

}
