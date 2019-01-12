package com.owlplug.core.ui;

import com.jfoenix.controls.JFXListCell;
import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.model.Plugin;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

public class PluginListCellFactory implements Callback<ListView<Plugin>, ListCell<Plugin>> {

  private ApplicationDefaults applicationDefaults;

  public PluginListCellFactory(ApplicationDefaults applicationDefaults) {

    this.applicationDefaults = applicationDefaults;
  }

  @Override
  public JFXListCell<Plugin> call(ListView<Plugin> arg0) {
    return new JFXListCell<Plugin>() {
      private ImageView imageView = new ImageView();

      @Override
      public void updateItem(Plugin plugin, boolean empty) {
        super.updateItem(plugin, empty);
        if (empty) {
          setText(null);
          setGraphic(null);
        } else {
          imageView.setImage(applicationDefaults.getPluginFormatIcon(plugin));
          setText(plugin.getName());
          setGraphic(imageView);
        }
      }
    };
  }

}
