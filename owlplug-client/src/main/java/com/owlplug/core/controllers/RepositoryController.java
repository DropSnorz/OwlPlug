package com.owlplug.core.controllers;

import com.owlplug.core.utils.PlatformUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import org.springframework.stereotype.Controller;

/**
 * Legacy repository controller.
 * @deprecated - legacy
 */
@Controller
public class RepositoryController {

  @FXML
  Hyperlink roadmapLink;

  /**
   * FXML initialize.
   */
  public void initialize() {

    roadmapLink.setOnAction(e -> {
      PlatformUtils.openDefaultBrowser("https://owlplug.com/roadmap");
    });    

  }


}
