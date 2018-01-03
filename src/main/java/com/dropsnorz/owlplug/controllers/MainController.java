package com.dropsnorz.owlplug.controllers;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dropsnorz.owlplug.model.Plugin;
import com.dropsnorz.owlplug.services.PluginExplorer;
import com.jfoenix.controls.JFXTreeView;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

@Controller
public class MainController {
	
	@Autowired
	PluginExplorer pluginExplorer;
	
	@FXML
	JFXTreeView<Object> treeView;
	
	@FXML
	BorderPane mainPane;
	
	
	List<Plugin> pluginList;
	
	
	FileTree pluginTree;
	
	
    @FXML
    public void initialize() {  
    	
    	
    	TreeItem<Object> treeRootNode = 
    	        new TreeItem<Object>("(all)");
    	
    	List<Plugin> pluginList = pluginExplorer.explore();
    	this.pluginList = pluginList;
    	
    	for(Plugin plugin : pluginList){
    		
    		treeRootNode.getChildren().add(new TreeItem<Object>(plugin.getName()));
    		
    	}
    	
    	TreeItem<Object> treeFileRootNode = new TreeItem<Object>("(all)");
    	
    	generatePluginTree();
    	buildChildren(pluginTree, treeFileRootNode);
    	
		treeView.setRoot(treeFileRootNode);

    	
    }
    
    public void generatePluginTree(){
    	
    	pluginTree = new FileTree();
    	
    	for (Plugin plug : pluginList) {
    	    FileTree node = pluginTree;
    	    
    	    String[] subDirs = plug.getPath().split("/");
    	    for (int i = 0; i < subDirs.length; i ++) {
    	    	String segment = subDirs[i];
    	    	FileTree ft = new FileTree();
    	    	
    	    	if(i == subDirs.length - 1){
    	    		ft.setNodeValue(plug);
    	    	}
    	        node = node.computeIfAbsent(segment, s -> ft);
    	    }
    	}
    	
    	
    }
    
    public void buildChildren(FileTree pluginTree, TreeItem<Object> node){
    	
	    	for(String dir : pluginTree.keySet()){
	    		
	    		
	    		if(pluginTree.get(dir).values().size() == 0){
		    		TreeItem<Object> plug = new TreeItem<Object>(pluginTree.get(dir).getNodeValue());
	    			node.getChildren().add(plug);
	    			
	    		}
	    		else{
		    		TreeItem<Object> item = new TreeItem<Object>(dir);
		    		
		    		node.getChildren().add(item);
		    		
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
