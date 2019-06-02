package com.owlplug.core.components;

import java.io.IOException;
import java.util.HashMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class LazyViewRegistry {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  public static final String NEW_ACCOUNT_VIEW = "NEW_ACCOUNT_VIEW";
  public static final String NEW_LINK_VIEW = "NEW_LINK_VIEW";
  public static final String WELCOME_VIEW = "WELCOME_VIEW";
  public static final String STORE_MENU_VIEW = "STORE_MENU_VIEW";
  public static final String NEW_STORE_VIEW = "NEW_STORE_VIEW";

  @Autowired
  private ApplicationContext context;

  private HashMap<String, Parent> viewRegistry;

  LazyViewRegistry() {
    viewRegistry = new HashMap<>();
  }

  /**
   * Preload all detached views. Must be called after spring components setup to
   * allow fxml bindings on controllers
   */
  public void preload() {
    preloadFxml(NEW_ACCOUNT_VIEW, "/fxml/dialogs/NewAccount.fxml");
    preloadFxml(NEW_LINK_VIEW, "/fxml/dialogs/NewLinkView.fxml");
    preloadFxml(WELCOME_VIEW, "/fxml/dialogs/WelcomeView.fxml");
    preloadFxml(STORE_MENU_VIEW, "/fxml/menu/StoreMenu.fxml");
    preloadFxml(NEW_STORE_VIEW, "/fxml/dialogs/NewStoreView.fxml");

  }

  public Parent get(String key) {
    return viewRegistry.get(key);
  }

  public Node getAsNode(String key) {
    return viewRegistry.get(key);
  }

  private void preloadFxml(String key, String ressource) {
    viewRegistry.put(key, loadFxml(ressource));
  }

  private Parent loadFxml(String ressource) {
    FXMLLoader loader = new FXMLLoader(getClass().getResource(ressource));
    loader.setControllerFactory(context::getBean);
    try {
      return loader.load();
    } catch (IOException e) {
      log.error("View can't be loaded in registry", e);
    }
    return null;
  }

}
