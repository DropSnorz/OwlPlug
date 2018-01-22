package com.dropsnorz.owlplug.controllers.dialog;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.dropsnorz.owlplug.controllers.MainController;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.text.Text;

@Controller
public class DialogController {
	
	@Autowired
	MainController mainController;
	
	private JFXDialog dialog;
	
	public JFXDialog newDialog() {
		dialog = new JFXDialog();
		dialog.setDialogContainer(mainController.getRootPane());
				
		return dialog;
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
		
		button.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent e) {
				dialog.close();
			};
		});	
		
		layout.setActions(button);
		
		return newDialog(layout);

	}
	
	public JFXDialog getDialog() {
		return dialog;
	}

}
