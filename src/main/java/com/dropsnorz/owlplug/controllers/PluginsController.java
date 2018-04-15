package com.dropsnorz.owlplug.controllers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.prefs.Preferences;

import javafx.beans.binding.Bindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dropsnorz.owlplug.ApplicationDefaults;
import com.dropsnorz.owlplug.components.FilterableTreeItem;
import com.dropsnorz.owlplug.components.TreeItemPredicate;
import com.dropsnorz.owlplug.controllers.dialogs.DialogController;
import com.dropsnorz.owlplug.dao.PluginDAO;
import com.dropsnorz.owlplug.dao.PluginRepositoryDAO;
import com.dropsnorz.owlplug.model.IDirectory;
import com.dropsnorz.owlplug.model.Plugin;
import com.dropsnorz.owlplug.model.PluginDirectory;
import com.dropsnorz.owlplug.model.PluginRepository;
import com.dropsnorz.owlplug.services.PluginExplorer;
import com.dropsnorz.owlplug.services.TaskFactory;
import com.dropsnorz.owlplug.services.TaskManager;
import com.dropsnorz.owlplug.utils.FileUtils;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXDrawer;
import com.jfoenix.controls.JFXTabPane;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeView;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.control.TreeItem;

@Controller
public class PluginsController {

	@Autowired
	TaskFactory taskFactory;

	@Autowired
	MainController mainController;

	@Autowired
	DialogController dialogController;

	@Autowired 
	PluginDAO pluginDAO;

	@Autowired
	NodeInfoController nodeInfoController;

	@Autowired
	PluginRepositoryDAO repositoryDAO;

	@Autowired
	protected Preferences prefs;


	@FXML
	JFXButton syncButton;

	@FXML
	JFXTreeView<Object> treeView;

	@FXML
	JFXTabPane treeViewTabPane;

	@FXML
	JFXTextField searchTextField;

	@FXML
	JFXButton newRepositoryButton;


	Iterable<Plugin> pluginList;

	FileTree pluginTree;

	FilterableTreeItem<Object> treeRootNode;

	FilterableTreeItem<Object> treeFileRootNode; 

	FilterableTreeItem<Object> treeRepositoryRootNode; 



	private Image folderImage = new Image(getClass().getResourceAsStream("/icons/folder-grey-16.png"));
	private Image brickImage  = new Image(getClass().getResourceAsStream("/icons/soundwave-blue-16.png"));;

	@FXML
	public void initialize() {  


		newRepositoryButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				mainController.setLeftDrawerFXML("/fxml/NewRepositoryMenu.fxml");
				mainController.getLeftDrawer().open();

			};
		});	

		treeRootNode = 
				new FilterableTreeItem<Object>("(all)");

		treeFileRootNode = new FilterableTreeItem<Object>("(all)");

		treeRepositoryRootNode = new FilterableTreeItem<Object>("Repositories");


		treeView.setRoot(treeRootNode);
		refreshPlugins();

		treeView.getSelectionModel().selectedItemProperty().addListener( new ChangeListener() {

			@Override
			public void changed(ObservableValue observable, Object oldValue,
					Object newValue){

				if(newValue != null){
					TreeItem<Object> selectedItem = (TreeItem<Object>) newValue;
					nodeInfoController.setNode(selectedItem.getValue());
				}

			}

		});



		treeViewTabPane.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<Tab>() {
					@Override
					public void changed(ObservableValue<? extends Tab> ov, Tab oldTab, Tab newTab) {
						if(newTab.getId().equals("treeTabAll")){
							treeView.setRoot(treeRootNode);
						}
						else if (newTab.getId().equals("treeTabRepositories")) {
							treeView.setRoot(treeRepositoryRootNode);
						}
						else{
							treeView.setRoot(treeFileRootNode);
						}
					}
				}
				);


		treeRootNode.predicateProperty().bind(Bindings.createObjectBinding(() -> {
			if (searchTextField.getText() == null || searchTextField.getText().isEmpty())
				return null;
			return TreeItemPredicate.create(actor -> actor.toString().toLowerCase().contains(searchTextField.getText().toLowerCase()));
		}, searchTextField.textProperty()));


		treeFileRootNode.predicateProperty().bind(Bindings.createObjectBinding(() -> {
			if (searchTextField.getText() == null || searchTextField.getText().isEmpty())
				return null;
			return TreeItemPredicate.create(actor -> actor.toString().toLowerCase().contains(searchTextField.getText().toLowerCase()));
		}, searchTextField.textProperty()));


		syncButton.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				taskFactory.run(taskFactory.createSyncPluginTask());
			};
		});	
	}

	public void refreshPlugins() {

		treeRootNode.getInternalChildren().clear();

		Iterable<Plugin> pluginList = pluginDAO.findAll();
		this.pluginList = pluginList;

		for(Plugin plugin : pluginList){

			TreeItem<Object> item = new FilterableTreeItem<Object>(plugin);
			item.setGraphic(new ImageView(brickImage));
			treeRootNode.getInternalChildren().add(item);


		}

		treeRootNode.setExpanded(true);

		treeFileRootNode.getInternalChildren().clear();

		Iterable<PluginRepository> pluginRepositories = repositoryDAO.findAll();

		for(PluginRepository repository : pluginRepositories) {
			TreeItem<Object> item = new FilterableTreeItem<Object>(repository);
			treeRepositoryRootNode.getInternalChildren().add(item);
		}

		generatePluginTree();
		buildDirectoryTree(pluginTree, treeFileRootNode, null);


		treeRepositoryRootNode.setExpanded(true);
		treeRepositoryRootNode.getInternalChildren().clear();

		buildRepositoryTree(pluginTree, treeRepositoryRootNode, pluginRepositories);
		


	}

	public void generatePluginTree(){

		pluginTree = new FileTree();

		for (Plugin plug : pluginList) {
			FileTree node = pluginTree;

			String[] subDirs = plug.getPath().split("/");
			String currentPath= "";
			for (int i = 0; i < subDirs.length; i ++) {
				currentPath = currentPath + subDirs[i] +"/";
				String segment = subDirs[i];
				FileTree ft = new FileTree();

				if(node.get(segment) == null){
					//Node is a plugin (End of branch)
					if(i == subDirs.length - 1){
						ft.setNodeValue(plug);
					}

					//Node is a directory or a repository)
					else{

						//TODO Should be optimized for large plugin set
						List<Plugin> localPluginList = new ArrayList<Plugin>();

						for(Plugin p : pluginList){
							if(p.getPath().startsWith(currentPath)){
								localPluginList.add(p);
							}
						}

						// Remove ending separator before getting the path
						PluginRepository repository = repositoryDAO.findByName(subDirs[i]);

						if(repository != null) {
							repository.setPluginList(localPluginList);
							ft.setNodeValue(repository);

						}
						else {

							PluginDirectory directory = new PluginDirectory();
							directory.setName(segment);
							directory.setPath(currentPath);

							directory.setPluginList(localPluginList);
							ft.setNodeValue(directory);

						}
					}
					node.put(segment, ft);

				}
				node = node.get(segment);
			}
		}


	}

	public void buildDirectoryTree(FileTree pluginTree, FilterableTreeItem<Object> node, String mergedParent){

		node.setGraphic(new ImageView(folderImage));
		node.setExpanded(true);
		
		if(mergedParent == null) {
			mergedParent = "";
		}

		for(String dir : pluginTree.keySet()){
			
			FileTree child = pluginTree.get(dir);

			// If child is empty then we have reached a plugin and we can't go deeper
			if(child.values().size() == 0){
				FilterableTreeItem<Object> plug = new FilterableTreeItem<Object>(child.getNodeValue());
				plug.setGraphic(new ImageView(brickImage));

				node.getInternalChildren().add(plug);

			}
			else{
				
				IDirectory directory;
								
				//If child node contains only one directory we can merge it with this directory
				if(child.size() == 1 && ( (FileTree)child.values().toArray()[0]).getNodeValue() instanceof PluginDirectory) {
										
					directory = (IDirectory) child.getNodeValue();
					mergedParent = mergedParent + directory.getName() + "/" ;

					buildDirectoryTree(child, node, mergedParent);
					mergedParent = "";

				}
				else {					
					directory = (IDirectory) child.getNodeValue();
					directory.setDisplayName(mergedParent + directory.getName());

					mergedParent = "";
					FilterableTreeItem<Object> item = new FilterableTreeItem<Object>(directory);
					node.getInternalChildren().add(item);
					buildDirectoryTree(child, item, mergedParent);

				}
				
			}

		}


	}

	public void buildRepositoryTree(FileTree pluginTree, FilterableTreeItem<Object> node, Iterable<PluginRepository> repositories) {
		node.setGraphic(new ImageView(folderImage));
		node.setExpanded(true);

		FileTree treeHead = pluginTree;

		String[] directories = getPluginRepositoryPath().split("/");

		for(String dir : directories) {
			treeHead = treeHead.get(dir);
		}
		
		//Searching for missing or empty repositories
		for(PluginRepository repository : repositories) {
			if(!treeHead.containsKey(repository.getName())) {
				FilterableTreeItem<Object> item = new FilterableTreeItem<Object>(repository);
				item.setGraphic(new ImageView(folderImage));
				node.getInternalChildren().add(item);
			}
		}


		for(String dir : treeHead.keySet()){


			if(treeHead.get(dir).values().size() == 0){

				FilterableTreeItem<Object> plug = new FilterableTreeItem<Object>(treeHead.get(dir).getNodeValue());
				plug.setGraphic(new ImageView(brickImage));


				node.getInternalChildren().add(plug);

			}
			else{

				FilterableTreeItem<Object> item = new FilterableTreeItem<Object>(treeHead.get(dir).getNodeValue());
				node.getInternalChildren().add(item);

				buildDirectoryTree(treeHead.get(dir), item, null);

			}

		}

	}

	private String getPluginRepositoryPath() {

		String path =prefs.get("VST2_DIRECTORY", null);
		if(path == null) return null;
		return FileUtils.convertPath(path + File.separator + ApplicationDefaults.REPOSITORY_FOLDER_NAME);



	}

	class FileTree extends HashMap<String, FileTree>{

		Object nodeValue;

		public void setNodeValue(Object nodeValue){
			this.nodeValue = nodeValue;
		}
		public Object getNodeValue(){
			return nodeValue;
		}
	}


}
