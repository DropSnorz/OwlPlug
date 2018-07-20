package com.dropsnorz.owlplug.core.controllers.dialogs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dropsnorz.owlplug.core.controllers.MainController;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;

import javafx.scene.Node;
import javafx.scene.text.Text;

@Controller
public class DialogController {
	
	@Autowired
	private MainController mainController;
	
	private JFXDialog dialog;
	
	public JFXDialog newDialog() {
		dialog = new JFXDialog();
		dialog.setDialogContainer(mainController.getRootPane());
				
		return dialog;
	}
	
	public JFXDialog newDialog(Node node) {
		
		JFXDialogLayout layout = new JFXDialogLayout();
		layout.setBody(node);
		return newDialog(layout);
	}
	
	public JFXDialog newDialog(double sizeX, double sizeY, Node body) {
		
		JFXDialogLayout layout = new JFXDialogLayout();
		layout.setMaxSize(sizeX, sizeY);
		layout.setPrefSize(sizeX, sizeY);
		layout.setBody(body);
		
		return newDialog(layout);

		
	}
	
	public JFXDialog newDialog(double sizeX, double sizeY, Node body, Node heading) {
		
		JFXDialogLayout layout = new JFXDialogLayout();
		layout.setMaxSize(sizeX, sizeY);
		layout.setPrefSize(sizeX, sizeY);
		layout.setBody(body);
		layout.setHeading(heading);
		
		return newDialog(layout);

		
	}
	
	public JFXDialog newDialog(JFXDialogLayout layout) {
		
		newDialog();
		dialog.setContent(layout);
		return dialog;
	}
	
	public JFXDialog newSimpleInfoDialog(String title, String body) {
		
		return newSimpleInfoDialog(new Text(title), new Text(body));
	}
	
	public JFXDialog newSimpleInfoDialog(Node title, Node body) {
		JFXDialogLayout layout = new JFXDialogLayout();
		
		layout.setHeading(title);
		layout.setBody(body);
		
		JFXButton button = new JFXButton("Close");
		
		button.setOnAction(e -> {
				dialog.close();
		});	
		
		layout.setActions(button);
		return newDialog(layout);

	}
	
	public JFXDialog getDialog() {
		return dialog;
	}

}
