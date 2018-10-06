package com.dropsnorz.owlplug.core.tasks;

import com.dropsnorz.owlplug.core.dao.PluginDAO;
import com.dropsnorz.owlplug.core.model.Plugin;
import com.dropsnorz.owlplug.core.model.PluginFormat;
import com.dropsnorz.owlplug.core.tasks.plugins.discovery.NativePluginBuilder;
import com.dropsnorz.owlplug.core.tasks.plugins.discovery.NativePluginBuilderFactory;
import com.dropsnorz.owlplug.core.tasks.plugins.discovery.NativePluginCollector;
import com.dropsnorz.owlplug.core.tasks.plugins.discovery.NativePluginCollectorFactory;
import com.dropsnorz.owlplug.core.tasks.plugins.discovery.PluginSyncTaskParameters;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class PluginSyncTask extends AbstractTask {

	protected PluginDAO pluginDAO;
	private PluginSyncTaskParameters parameters;
	

	/**
	 * Creates a new SyncPluginTask.
	 * @param parameters Task Parameters
	 * @param pluginDAO pluginDAO
	 */
	public PluginSyncTask(PluginSyncTaskParameters parameters, PluginDAO pluginDAO) {
		this.parameters = parameters;
		this.pluginDAO = pluginDAO;
		
		setName("Sync Plugins");
	}


	@Override
	protected TaskResult call() throws Exception {

		this.updateMessage("Syncing Plugins...");
		this.updateProgress(0, 2);

		try {
			ArrayList<Plugin> discoveredPlugins = new ArrayList<Plugin>();
			if (parameters.isFindVST2()) {

				List<File> vst2files = new ArrayList<>();
				NativePluginCollector collector = NativePluginCollectorFactory.getPluginFinder(
						parameters.getPlatform(), PluginFormat.VST2);
				vst2files = collector.collect(parameters.getPluginDirectory());
				NativePluginBuilder builder = NativePluginBuilderFactory.createPluginBuilder(
						parameters.getPlatform(), PluginFormat.VST2);

				for (File file: vst2files) {
					discoveredPlugins.add(builder.build(file));
				}
			}
			
			this.updateProgress(1, 3);
			
			if (parameters.isFindVST3()) {

				List<File> vst3files = new ArrayList<>();
				NativePluginCollector collector = NativePluginCollectorFactory
						.getPluginFinder(parameters.getPlatform(), PluginFormat.VST3);
				vst3files = collector.collect(parameters.getPluginDirectory());
				NativePluginBuilder builder = NativePluginBuilderFactory
						.createPluginBuilder(parameters.getPlatform(), PluginFormat.VST3);

				for (File file: vst3files) {
					discoveredPlugins.add(builder.build(file));
				}
			}
			this.updateProgress(2, 3);
			
			pluginDAO.deleteAll();
			pluginDAO.saveAll(discoveredPlugins);

			this.updateProgress(3, 3);
			this.updateMessage("Plugins synchronized");
			
			return success();

		} catch (Exception e) {
			this.updateMessage("Plugins synchronization failed. Check your plugin directory.");
			throw new TaskException("Plugins synchronization failed");

		}

	}
}
