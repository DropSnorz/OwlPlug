package com.dropsnorz.owlplug.core.ui;

import com.dropsnorz.owlplug.core.model.IDirectory;
import com.dropsnorz.owlplug.core.model.Plugin;
import com.jfoenix.controls.JFXTreeCell;

import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class CustomTreeCell extends JFXTreeCell<Object>{

	protected HBox hbox;
	protected TextFlow textFlow;

	@Override
	public void updateItem(Object item, boolean empty) {
		super.updateItem(item, empty);

		if (empty) {
			setText(null);
			setGraphic(null);
		} else {

			if(item instanceof Plugin) {
				setText(item.toString());
				setGraphic(getTreeItem().getGraphic());
			}
			else if (item instanceof IDirectory){
				setText(null);
				textFlow = new TextFlow();

				IDirectory dir = (IDirectory) item;
				Text name;

				if(dir.getDisplayName() != null && !dir.getName().equals(dir.getDisplayName())) {

					String preText = dir.getDisplayName().replaceAll( "/" + dir.getName() + "$","");
					Text pre = new Text(preText);
					pre.getStyleClass().add("text-disabled");
					textFlow.getChildren().add(pre);
					name = new Text("/" + dir.getName());

				}
				else {
					name = new Text(dir.getName());
				}

				textFlow.getChildren().add(name);

				Node icon = getTreeItem().getGraphic();
				hbox = new HBox(5);
				hbox.getChildren().add(icon);
				hbox.getChildren().add(textFlow);

				setGraphic(hbox);
			}
			else {
				setText(item.toString());
				setGraphic(getTreeItem().getGraphic());
			}
		}
	}

}
