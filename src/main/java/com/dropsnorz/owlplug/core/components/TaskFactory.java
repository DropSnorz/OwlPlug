package com.dropsnorz.owlplug.core.components;

import com.dropsnorz.owlplug.ApplicationDefaults;
import com.dropsnorz.owlplug.core.controllers.PluginsController;
import com.dropsnorz.owlplug.core.dao.PluginDAO;
import com.dropsnorz.owlplug.core.dao.PluginRepositoryDAO;
import com.dropsnorz.owlplug.core.engine.repositories.IRepositoryStrategy;
import com.dropsnorz.owlplug.core.engine.repositories.RepositoryStrategyException;
import com.dropsnorz.owlplug.core.engine.repositories.RepositoryStrategyParameters;
import com.dropsnorz.owlplug.core.engine.repositories.RepositoryStrategyResolver;
import com.dropsnorz.owlplug.core.engine.tasks.AbstractTask;
import com.dropsnorz.owlplug.core.engine.tasks.DirectoryRemoveTask;
import com.dropsnorz.owlplug.core.engine.tasks.PluginRemoveTask;
import com.dropsnorz.owlplug.core.engine.tasks.PluginSyncTask;
import com.dropsnorz.owlplug.core.engine.tasks.RepositoryRemoveTask;
import com.dropsnorz.owlplug.core.engine.tasks.RepositoryTask;
import com.dropsnorz.owlplug.core.engine.tasks.TaskException;
import com.dropsnorz.owlplug.core.engine.tasks.TaskExecutionContext;
import com.dropsnorz.owlplug.core.engine.tasks.TaskResult;
import com.dropsnorz.owlplug.core.model.Plugin;
import com.dropsnorz.owlplug.core.model.PluginDirectory;
import com.dropsnorz.owlplug.core.model.PluginRepository;
import com.dropsnorz.owlplug.core.services.PluginService;
import com.dropsnorz.owlplug.store.controllers.StoreController;
import com.dropsnorz.owlplug.store.dao.PluginStoreDAO;
import com.dropsnorz.owlplug.store.dao.StoreProductDAO;
import com.dropsnorz.owlplug.store.model.StoreProduct;
import com.dropsnorz.owlplug.store.tasks.ProductInstallTask;
import com.dropsnorz.owlplug.store.tasks.StoreSyncTask;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskFactory {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ApplicationDefaults applicationDefaults;
	@Autowired
	private PluginService pluginService;
	@Autowired
	private TaskRunner taskManager;
	@Autowired
	private PluginDAO pluginDAO;
	@Autowired 
	private PluginRepositoryDAO pluginRepositoryDAO;
	@Autowired
	private RepositoryStrategyResolver repositoryStrategyResolver;
	@Autowired
	private PluginsController pluginsController;
	@Autowired
	private PluginStoreDAO pluginStoreDAO;
	@Autowired
	private StoreProductDAO storeProductDAO;
	@Autowired 
	private StoreController storeController;

	public TaskExecutionContext createPluginSyncTask() {

		PluginSyncTask task = new PluginSyncTask(pluginService, pluginDAO);
		task.setOnSucceeded(e -> {
			pluginsController.refreshPlugins();
		});
		bindOnFailHandler(task);

		return buildContext(task);
	}

	public TaskExecutionContext createPluginRemoveTask(Plugin plugin) {

		PluginRemoveTask task = new PluginRemoveTask(plugin, pluginDAO);
		task.setOnSucceeded(e -> {
			pluginsController.refreshPlugins();
		});
		bindOnFailHandler(task);

		return buildContext(task);
	}

	public TaskExecutionContext createDirectoryRemoveTask(PluginDirectory pluginDirectory) {

		DirectoryRemoveTask task = new DirectoryRemoveTask(pluginDirectory);
		task.setOnSucceeded(e -> {
			createPluginSyncTask().run();
		});
		bindOnFailHandler(task);

		return buildContext(task);
	}

	public TaskExecutionContext createRepositoryRemoveTask(PluginRepository repository, String path) {

		RepositoryRemoveTask task = new RepositoryRemoveTask(pluginRepositoryDAO, repository, path);
		task.setOnSucceeded(e -> {
			createPluginSyncTask().run();
		});
		bindOnFailHandler(task);
		return buildContext(task);
	}


	public TaskExecutionContext createRepositoryTask(PluginRepository repository, RepositoryStrategyParameters parameters) {

		IRepositoryStrategy strategy = repositoryStrategyResolver.getStrategy(repository, parameters);

		RepositoryTask task = new RepositoryTask() {
			@Override
			protected TaskResult call() throws Exception {

				this.updateProgress(0, 1);
				
				try {
					strategy.execute(repository, parameters);
					this.updateProgress(1, 1);

				} catch (RepositoryStrategyException e) {
					
					this.updateMessage(e.getMessage());
					this.updateProgress(1, 1);
					throw new TaskException(e);
				}
				return success();
			}
		};
		task.setOnSucceeded(e -> {
			createPluginSyncTask().run();
		});
		bindOnFailHandler(task);
		task.setName(parameters.get("task-name"));
		return buildContext(task);

	}

	
	public TaskExecutionContext createStoreSyncTask() {
		
		StoreSyncTask task = new StoreSyncTask(pluginStoreDAO, storeProductDAO);
		task.setOnSucceeded(e ->{
			storeController.refreshView();
		});
		return buildContext(task);
		
	}
	
	
	public TaskExecutionContext createProductInstallTask(StoreProduct product, File targetDirectory) {
		
		ProductInstallTask task = new ProductInstallTask(product, targetDirectory, applicationDefaults);
		bindOnFailHandler(task);
		return buildContext(task);
		
	}
	
	
	private void bindOnFailHandler(AbstractTask task) {
		task.setOnFailed(e -> {
			taskManager.triggerOnError();
		});
	}
	
	private TaskExecutionContext buildContext(AbstractTask task) {
		
		return new TaskExecutionContext(task, taskManager);
	}

}
