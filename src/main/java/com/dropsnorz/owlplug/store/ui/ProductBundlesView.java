package com.dropsnorz.owlplug.store.ui;

import com.dropsnorz.owlplug.store.model.ProductBundle;
import com.jfoenix.controls.JFXButton;
import java.util.Collections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.text.Text;

public class ProductBundlesView extends GridPane {
	
	private String stringPlaceholder = String.join(" ", Collections.nCopies(30, "·"));

	public ProductBundlesView() {
		super();
		
		this.setHgap(5);
		this.setVgap(5);

		ColumnConstraints col0 = new ColumnConstraints();
		col0.setPrefWidth(USE_COMPUTED_SIZE);
		ColumnConstraints col1 = new ColumnConstraints();
		col1.setHgrow(Priority.ALWAYS);
		col1.setPrefWidth(1);
		ColumnConstraints col2 = new ColumnConstraints();
		col2.setPrefWidth(USE_COMPUTED_SIZE);
		this.getColumnConstraints().addAll(col0,col1,col2);
		

	}
	
	public void clear() {
		this.getChildren().clear();
		this.getRowConstraints().clear();
	}

	public void addProductBundle(ProductBundle bundle, EventHandler<ActionEvent> installHandler) {

		
		final Label bundleName = new Label(bundle.getName());
		final Text fillText = new Text(this.stringPlaceholder);
		fillText.getStyleClass().add("text-disabled");
		final JFXButton installButton = new JFXButton("Install");
		installButton.getStyleClass().add("button-action");
		installButton.setOnAction(installHandler);
		
		int bundlePosition = this.getRowCount();

		this.addRow(bundlePosition, bundleName, fillText, installButton);

		RowConstraints rowConstraint = new RowConstraints();
		rowConstraint.setValignment(VPos.CENTER);
		this.getRowConstraints().add(rowConstraint);
		
	}
	

}
