/* OwlPlug
 * Copyright (C) 2021 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OwlPlug is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OwlPlug.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.owlplug.explore.controllers.dialogs;

import com.owlplug.controls.DialogLayout;
import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.components.LazyViewRegistry;
import com.owlplug.core.controllers.dialogs.AbstractDialogController;
import com.owlplug.core.utils.FileUtils;
import com.owlplug.explore.components.ExploreTaskFactory;
import com.owlplug.explore.model.PackageBundle;
import com.owlplug.explore.services.ExploreService;
import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class InstallStepDialogController extends AbstractDialogController {

  @FXML
  private Accordion stepAccordion;
  @FXML
  private TitledPane verifyStep;
  @FXML
  private TitledPane selectDirectoryStep;
  @FXML
  private TitledPane prepareStep;
  @FXML
  private ProgressBar progressBar;
  @FXML
  private Text formatText;
  @FXML
  private TextField installationDirectoryTextField;
  @FXML
  private ToggleButton vst2ToggleButton;
  @FXML
  private ToggleButton vst3ToggleButton;
  @FXML
  private ToggleButton auToggleButton;
  @FXML
  private ToggleButton lv2ToggleButton;
  @FXML
  private Button continueButton;
  @FXML
  private Button closeButton;

  private final ToggleGroup toggleGroup = new ToggleGroup();

  @Autowired
  private ExploreService exploreService;
  @Autowired
  private LazyViewRegistry lazyViewRegistry;
  @Autowired
  private ExploreTaskFactory exploreTaskFactory;

  private InstallationParameters installParams;

  public void initialize() {
    vst2ToggleButton.setToggleGroup(toggleGroup);
    vst3ToggleButton.setToggleGroup(toggleGroup);
    auToggleButton.setToggleGroup(toggleGroup);
    lv2ToggleButton.setToggleGroup(toggleGroup);
    toggleGroup.selectedToggleProperty().addListener((obs,n,o) -> {
      if (installParams == null) {
        return;
      }
      String baseDir = null;
      if (vst2ToggleButton.equals(obs.getValue())) {
        baseDir = this.getPreferences().get(ApplicationDefaults.VST_DIRECTORY_KEY, "");
      } else if (vst3ToggleButton.equals(obs.getValue())) {
        baseDir = this.getPreferences().get(ApplicationDefaults.VST3_DIRECTORY_KEY, "");
      } else if (auToggleButton.equals(obs.getValue())) {
        baseDir = this.getPreferences().get(ApplicationDefaults.AU_DIRECTORY_KEY, "");
      } else if (lv2ToggleButton.equals(obs.getValue())) {
        baseDir = this.getPreferences().get(ApplicationDefaults.LV2_DIRECTORY_KEY, "");
      }
      if (baseDir != null) {
        File installationDirectory = generateInstallationDirectoryFromBasePath(baseDir);
        installParams.setInstallationDirectory(installationDirectory);
        installationDirectoryTextField.setText(installationDirectory.getAbsolutePath());
      } else {
        installParams.setInstallationDirectory(null);
        installationDirectoryTextField.setText("");
      }
    });

    closeButton.setOnAction(e -> {
      this.close();
    });

    continueButton.setOnAction(e -> {
      resumeInstall();
    });

  }

  public void install(PackageBundle bundle) {
    reset();
    installParams = new InstallationParameters();
    installParams.setBundle(bundle);
    resumeInstall();
  }

  // TODO add support of one click install with dynamic show support
  private void resumeInstall() {
    stepAccordion.setExpandedPane(prepareStep);
    if (prepareInstallation()) {
      prepareStep.setText("✅ Prepare installation");
      prepareStep.setDisable(true);
      progressBar.setProgress((double) 1 / 3);
    } else {
      installParams.setInstallationConfirmed(false);
      return;
    }

    stepAccordion.setExpandedPane(selectDirectoryStep);
    if (selectInstallationDirectory()) {
      selectDirectoryStep.setText("✅ Select installation directory");
      selectDirectoryStep.setDisable(true);
      progressBar.setProgress((double) 2 / 3);
    } else {
      installParams.setInstallationConfirmed(false);
      return;
    }

    stepAccordion.setExpandedPane(verifyStep);
    if (verifyInstallationDirectory()) {
      verifyStep.setText("✅ Verify plugin installation");
      verifyStep.setDisable(true);
      progressBar.setProgress(1);
    } else {
      return;
    }

    // Install bundle
    exploreTaskFactory.createBundleInstallTask(
            installParams.getBundle(),
            installParams.getInstallationDirectory()
            )
            .schedule();

    this.close();
  }

  /**
   * Initial bundle installation preparation step.
   * @return true if bundle is set
   */
  private boolean prepareInstallation() {
    return installParams.getBundle() != null;
  }

  /**
   * Returns true if an installation directory is set.
   * Otherwise, compute installation directory from bundle data.
   * For some bundle with multiple format, a unique installation
   * directory cannot be determined. This function returns false
   * to delegate the directory choice to the user based on the format.
   * @return true if installation directory is set
   */
  private boolean selectInstallationDirectory() {
    if (installParams.getInstallationDirectory() != null) {
      return true;
    }

    // Compute base directory using format if possible
    if (exploreService.canDeterminateBundleInstallFolder(installParams.getBundle())) {
      String baseDirectoryPath = exploreService.getBundleInstallFolder(installParams.getBundle());
      File selectedDirectory = generateInstallationDirectoryFromBasePath(baseDirectoryPath);

      if (selectedDirectory != null) {
        installParams.setInstallationDirectory(selectedDirectory);
        return true;
      }
    }

    return false;
  }

  /**
   * Verify final predicates for bundle installation:
   *  - Installation directory must be a valid target file, otherwise
   *  the user can choose another directory.
   *  - Verify that folder do not already exits, otherwise ask the user
   *  for permission to overwrite existing file in this folder.
   * @return true if installation predicates are met.
   */
  private boolean verifyInstallationDirectory() {
    if (installParams == null) {
      return false;
    }

    boolean result = true;

    if (!installParams.isInstallationConfirmed()) {
      installParams.setInstallationConfirmed(true);
      result = false;
    }

    File installationDirectory = installParams.getInstallationDirectory();

    // If any install target directory can be found, abort install
    if (installationDirectory != null
            && installationDirectory.exists() && !installationDirectory.isDirectory()) {
      // TODO : invalid installation target
      // Warn that directory cannot be used for installation
      // Allow to manually select a new one and use parent directory if it exists and if it's a directory.
      result = false;
    }

    if (installationDirectory != null && installationDirectory.exists()
            && !installParams.isDirectoryOverrideAllowed()) {
      // TODO warn that content might be overwritten.
      // Warn that content already exists, do not open a popup
      // but request a check box to be checked to proceed
      // and set directoryOverrideAllowed based on value
      // Refer to similar "If directory exists, asks the user for overwrite permission"
      // in the exploreController
      result = false;
    }

    return result;
  }


  private File generateInstallationDirectoryFromBasePath(String baseDirectoryPath) {

    File selectedDirectory = null;
    // A custom root directory to store plugin is defined and the base directory for
    // the bundle format is defined or not blank.
    if (baseDirectoryPath != null && !baseDirectoryPath.isBlank()) {

      selectedDirectory = new File(baseDirectoryPath);
      boolean shouldStoreInDirectory = this.getPreferences().getBoolean(
              ApplicationDefaults.STORE_DIRECTORY_ENABLED_KEY, false);
      //if the user wishes to group plugins in a dedicated directory
      //then we need to include the subdirectory as well.
      if (shouldStoreInDirectory) {
        String relativeDirectoryPath = this.getPreferences().get(ApplicationDefaults.STORE_DIRECTORY_KEY, "");
        selectedDirectory = new File(selectedDirectory, relativeDirectoryPath);
      }

      boolean shouldGroupByCreator = this.getPreferences().getBoolean(ApplicationDefaults.STORE_BY_CREATOR_ENABLED_KEY, false);
      //if the user wishes to group plugins by their creator,
      //then we need to include the subdirectory as well.
      if (shouldGroupByCreator) {
        String creator = FileUtils.sanitizeFileName(installParams.getBundle().getRemotePackage().getCreator());
        selectedDirectory = new File(selectedDirectory, creator);
      }

      boolean shouldWrapSubDirectory = this.getPreferences().getBoolean(ApplicationDefaults.STORE_SUBDIRECTORY_ENABLED, true);
      if (shouldWrapSubDirectory) {
        // If the plugin is wrapped into a subdirectory, checks for already existing
        // directory
        String subDirectory = FileUtils.sanitizeFileName(installParams.getBundle().getRemotePackage().getName());
        selectedDirectory = new File(selectedDirectory, subDirectory);
      }
    }
    return selectedDirectory;
  }

  public void reset() {
    prepareStep.setDisable(false);
    prepareStep.setText("Prepare installation");
    selectDirectoryStep.setDisable(false);
    selectDirectoryStep.setText("Select installation directory");
    prepareStep.setDisable(false);
    verifyStep.setText("Verify plugin installation");

    vst2ToggleButton.setDisable(!this.getPreferences().getBoolean(
            ApplicationDefaults.VST2_DISCOVERY_ENABLED_KEY, false));
    vst3ToggleButton.setDisable(!this.getPreferences().getBoolean(
            ApplicationDefaults.VST3_DISCOVERY_ENABLED_KEY, false));
    auToggleButton.setDisable(!this.getPreferences().getBoolean(
            ApplicationDefaults.AU_DISCOVERY_ENABLED_KEY, false));
    lv2ToggleButton.setDisable(!this.getPreferences().getBoolean(
            ApplicationDefaults.AU_DISCOVERY_ENABLED_KEY, false));

    toggleGroup.selectToggle(null);
    if (installParams != null && installParams.getBundle() != null) {
      formatText.setText(String.join(", ", installParams.getBundle().getFormats())
              .toUpperCase());
    }
  }


  @Override
  protected DialogLayout getLayout() {
    DialogLayout layout = new DialogLayout();
    Label title = new Label("Prepare installation");
    title.getStyleClass().add("heading-3");
    layout.setHeading(title);
    layout.setBody(lazyViewRegistry.get(LazyViewRegistry.INSTALL_STEP_VIEW));
    return layout;
  }

  public static class InstallationParameters {
    // Bundle being installed
    private PackageBundle bundle;
    // Target installation directory
    private File installationDirectory;
    // User confirmation for override
    private boolean directoryOverrideAllowed = false;
    // User confirmation for installation
    private boolean installationConfirmed = true;

    public PackageBundle getBundle() {
      return bundle;
    }

    public void setBundle(PackageBundle bundle) {
      this.bundle = bundle;
    }

    public File getInstallationDirectory() {
      return installationDirectory;
    }

    public void setInstallationDirectory(File installationDirectory) {
      this.installationDirectory = installationDirectory;
    }

    public boolean isDirectoryOverrideAllowed() {
      return directoryOverrideAllowed;
    }

    public void setDirectoryOverrideAllowed(boolean directoryOverrideAllowed) {
      this.directoryOverrideAllowed = directoryOverrideAllowed;
    }

    public boolean isInstallationConfirmed() {
      return installationConfirmed;
    }

    public void setInstallationConfirmed(boolean installationConfirmed) {
      this.installationConfirmed = installationConfirmed;
    }
  }
}
