package com.dropsnorz.owlplug.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.beans.binding.Bindings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dropsnorz.owlplug.components.FilterableTreeItem;
import com.dropsnorz.owlplug.components.TreeItemPredicate;
import com.dropsnorz.owlplug.engine.tasks.SyncPluginTask;
import com.dropsnorz.owlplug.model.Plugin;
import com.dropsnorz.owlplug.model.PluginDirectory;
import com.dropsnorz.owlplug.repositories.PluginRepository;
import com.dropsnorz.owlplug.services.PluginExplorer;
import com.dropsnorz.owlplug.services.TaskFactory;
import com.dropsnorz.owlplug.services.TaskManager;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
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
	PluginExplorer pluginExplorer;
	
	@Autowired 
	PluginRepository pluginRepository;

	@Autowired
	NodeInfoController nodeInfoController;
	
	@FXML
	JFXButton syncButton;

	@FXML
	JFXTreeView<Object> treeView;

	@FXML
	JFXTabPane treeViewTabPane;
	
	@FXML
	JFXTextField searchTextField;

	Iterable<Plugin> pluginList;

	FileTree pluginTree;

	FilterableTreeItem<Object> treeRootNode;

	FilterableTreeItem<Object> treeFileRootNode; 


	private Image folderImage = new Image(getClass().getResourceAsStream("/icons/folder-grey-16.png"));
	private Image brickImage  = new Image(getClass().getResourceAsStream("/icons/soundwave-blue-16.png"));;

	@FXML
	public void initialize() {  

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
		treeRootNode = 
				new FilterableTreeItem<Object>("(all)");

		Iterable<Plugin> pluginList = pluginRepository.findAll();
		this.pluginList = pluginList;

		for(Plugin plugin : pluginList){

			TreeItem<Object> item = new FilterableTreeItem<Object>(plugin);
			item.setGraphic(new ImageView(brickImage));
			treeRootNode.getInternalChildren().add(item);
			

		}

		treeRootNode.setExpanded(true);
		

		treeFileRootNode = new FilterableTreeItem<Object>("(all)");

		generatePluginTree();
		buildChildren(pluginTree, treeFileRootNode);
		
		treeView.setRoot(treeRootNode);
		
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
					if(i == subDirs.length - 1){
						ft.setNodeValue(plug);
					}
					else{

						PluginDirectory directory = new PluginDirectory();
						directory.setName(segment);
						directory.setPath(currentPath);
						
						//Should be optimized
						List<Plugin> localPluginList = new ArrayList<Plugin>();
						
						for(Plugin p : pluginList){
							if(p.getPath().startsWith(currentPath)){
								localPluginList.add(p);
							}
						}
						
						directory.setPluginList(localPluginList);
						ft.setNodeValue(directory);
						
					}
					node.put(segment, ft);
					
				}
				node = node.get(segment);
			}
		}


	}

	public void buildChildren(FileTree pluginTree, FilterableTreeItem<Object> node){

		node.setGraphic(new ImageView(folderImage));
		node.setExpanded(true);

		for(String dir : pluginTree.keySet()){

			if(pluginTree.get(dir).values().size() == 0){
				FilterableTreeItem<Object> plug = new FilterableTreeItem<Object>(pluginTree.get(dir).getNodeValue());
				plug.setGraphic(new ImageView(brickImage));


				node.getInternalChildren().add(plug);

			}
			else{
				FilterableTreeItem<Object> item = new FilterableTreeItem<Object>(pluginTree.get(dir).getNodeValue());
				node.getInternalChildren().add(item);

				buildChildren(pluginTree.get(dir), item);

			}

		}


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
