package com.owlplug.plugin.ui;

import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.plugin.model.IPlugin;
import com.owlplug.plugin.model.PluginComponent;
import javafx.geometry.Pos;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;

public class PluginKindBadgeView extends HBox {

  public PluginKindBadgeView(IPlugin plugin, ApplicationDefaults applicationDefaults) {
    super(4);
    setAlignment(Pos.CENTER_LEFT);

    if (plugin instanceof PluginComponent component) {
      getChildren().add(new PluginFormatBadgeView(component.asPlugin().getFormat(), applicationDefaults));
      FontIcon icon = new FontIcon("mdi2t-toy-brick-outline");
      Tooltip.install(icon, new Tooltip("Component"));
      getChildren().add(icon);
    } else {
      getChildren().add(new PluginFormatBadgeView(plugin.asPlugin().getFormat(), applicationDefaults));
    }
  }
}