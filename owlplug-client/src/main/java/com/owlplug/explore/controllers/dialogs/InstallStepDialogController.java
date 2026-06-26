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
import com.owlplug.core.components.ApplicationDefaults.Prefs;
import com.owlplug.core.components.ImageCache;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import org.kordamp.ikonli.javafx.FontIcon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class InstallStepDialogController extends AbstractDialogController {

  @Autowired
  private ExploreService exploreService;
  @Autowired
  private LazyViewRegistry lazyViewRegistry;
  @Autowired
  private ExploreTaskFactory exploreTaskFactory;
  @Autowired
  private ImageCache imageCache;

  @FXML
  private Pane screenshotBackgroundPane;
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
  private Label formatText;
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
  private Label installationDirectoryText;
  @FXML
  private Label directoryValidText;
  @FXML
  private Label directoryOverrideText;
  @FXML
  private Button directoryChooserButton;
  @FXML
  private CheckBox directoryOverrideCheckBox;
  @FXML
  private Button continueButton;
  @FXML
  private Button closeButton;

  private final ToggleGroup toggleGroup = new ToggleGroup();

  private InstallationParameters installParams;

  public InstallStepDialogController() {
    super(580,630);
  }

  public void initialize() {
    vst2ToggleButton.setToggleGroup(toggleGroup);
    vst3ToggleButton.setToggleGroup(toggleGroup);
    auToggleButton.setToggleGroup(toggleGroup);
    lv2ToggleButton.setToggleGroup(toggleGroup);
    toggleGroup.selectedToggleProperty().addListener((obs,o,n) -> {
      if (installParams == null) {
        return;
      }
      String baseDir = null;
      if (vst2ToggleButton.equals(obs.getValue())) {
        baseDir = this.getPreferences().get(Prefs.Plugins.VST2_DIRECTORY, "");
      } else if (vst3ToggleButton.equals(obs.getValue())) {
        baseDir = this.getPreferences().get(Prefs.Plugins.VST3_DIRECTORY, "");
      } else if (auToggleButton.equals(obs.getValue())) {
        baseDir = this.getPreferences().get(Prefs.Plugins.AU_DIRECTORY, "");
      } else if (lv2ToggleButton.equals(obs.getValue())) {
        baseDir = this.getPreferences().get(Prefs.Plugins.LV2_DIRECTORY, "");
      }
      if (baseDir != null) {
        File installationDirectory = generateInstallationDirectoryFromBasePath(baseDir);
        installParams.setInstallationDirectory(installationDirectory);
        if (installationDirectory != null) {
          installationDirectoryTextField.setText(installationDirectory.getAbsolutePath());
        }

      } else {
        installParams.setInstallationDirectory(null);
        installationDirectoryTextField.setText("");
      }
    });

    directoryChooserButton.setOnAction(e -> {
      if (installParams != null) {
        // Open dialog chooser to define store installation target
        DirectoryChooser directoryChooser = new DirectoryChooser();
        // Open directory chooser on top of the current windows
        Window mainWindow = directoryChooserButton.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(mainWindow);
        if (selectedDirectory != null) {
          installParams.setInstallationDirectory(selectedDirectory);
          // reset the confirmation flag if directory changed
          installParams.setInstallationConfirmed(false);
        }
        // revalidate installation
        this.resumeInstall();
      }
    });

    directoryOverrideCheckBox.selectedProperty().addListener((obs,o,n) -> {
      if (installParams != null) {
        installParams.setDirectoryOverrideAllowed(n);
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

    // Screenshot display
    Image screenshot = imageCache.get(bundle.getRemotePackage().getScreenshotUrl());
    if (screenshot != null) {
      BackgroundImage bgImg = new BackgroundImage(screenshot, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
          BackgroundPosition.CENTER,
          new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true));
      screenshotBackgroundPane.setBackground(new Background(bgImg));
    }
    screenshotBackgroundPane.setEffect(new InnerShadow(25, Color.BLACK));

    resumeInstall();
  }

  private void resumeInstall() {
    stepAccordion.setExpandedPane(prepareStep);
    if (prepareInstallation()) {
      prepareStep.setGraphic(createCheckIcon());
      prepareStep.setDisable(true);
      progressBar.setProgress((double) 1 / 3);
    } else {
      prepareStep.setDisable(false);
      installParams.setInstallationConfirmed(false);
      this.show();
      return;
    }

    stepAccordion.setExpandedPane(selectDirectoryStep);
    if (selectInstallationDirectory()) {
      selectDirectoryStep.setGraphic(createCheckIcon());
      selectDirectoryStep.setDisable(true);
      progressBar.setProgress((double) 2 / 3);
    } else {
      selectDirectoryStep.setDisable(false);
      installParams.setInstallationConfirmed(false);
      this.show();
      return;
    }

    stepAccordion.setExpandedPane(verifyStep);
    if (verifyInstallationDirectory()) {
      verifyStep.setGraphic(createCheckIcon());
      verifyStep.setDisable(true);
      progressBar.setProgress(1);
    } else {
      verifyStep.setDisable(false);
      this.show();
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

    if (installParams != null && installParams.getBundle() != null) {
      formatText.setText("Bundle formats: " + String.join(", ", installParams.getBundle().getFormats()).toUpperCase());
    }

    return false;
  }

  /**
   * Verify final predicates for bundle installation:
   *  - Installation directory must be a valid target file, otherwise
   *  the user can choose another directory.
   *  - Verify that folder does not already exist, otherwise ask the user
   *  for permission to overwrite existing file in this folder.
   * @return true if installation predicates are met.
   */
  private boolean verifyInstallationDirectory() {
    if (installParams == null || installParams.getInstallationDirectory() == null) {
      return false;
    }

    boolean result = true;

    File installationDirectory = installParams.getInstallationDirectory();
    installationDirectoryText.setText(installationDirectory.getAbsolutePath());

    // Reset validation styles
    directoryValidText.getStyleClass().removeAll("label-success", "label-danger");
    directoryOverrideText.getStyleClass().remove("label-warning");
    directoryChooserButton.setVisible(false);
    directoryChooserButton.setManaged(false);

    if (!installParams.isInstallationConfirmed()) {
      installParams.setInstallationConfirmed(true);
      result = false;
    }

    if (installationDirectory.exists() && !installationDirectory.isDirectory()) {
      directoryValidText.setText(installationDirectory.getName() + " is not a valid installation directory.");
      directoryValidText.getStyleClass().add("label-danger");
      directoryChooserButton.setVisible(true);
      directoryChooserButton.setManaged(true);
      result = false;
    } else {
      directoryValidText.setText(installationDirectory.getName() + " directory is a valid installation path.");
      directoryValidText.getStyleClass().add("label-success");
    }

    if (installationDirectory.exists()) {
      directoryOverrideText.setText("Directory " + installationDirectory.getName()
          + " already exists. Existing files may be overwritten. Please confirm below to continue.");
      directoryOverrideText.getStyleClass().add("label-warning");
      directoryOverrideCheckBox.setDisable(false);
      if (!installParams.isDirectoryOverrideAllowed()) {
        result = false;
      }
    } else {
      directoryOverrideText.setText("A new directory will be created: " + installationDirectory.getName());
      directoryOverrideCheckBox.setDisable(true);
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
              Prefs.Explore.STORE_DIRECTORY_ENABLED, false);
      //if the user wishes to group plugins in a dedicated directory
      //then we need to include the subdirectory as well.
      if (shouldStoreInDirectory) {
        String relativeDirectoryPath = this.getPreferences().get(Prefs.Explore.STORE_DIRECTORY, "");
        selectedDirectory = new File(selectedDirectory, relativeDirectoryPath);
      }

      boolean shouldGroupByCreator = this.getPreferences().getBoolean(Prefs.Explore.STORE_BY_CREATOR_ENABLED, false);
      //if the user wishes to group plugins by their creator,
      //then we need to include the subdirectory as well.
      if (shouldGroupByCreator) {
        String creator = FileUtils.sanitizeFileName(installParams.getBundle().getRemotePackage().getCreator());
        selectedDirectory = new File(selectedDirectory, creator);
      }

      boolean shouldWrapSubDirectory = this.getPreferences().getBoolean(Prefs.Explore.STORE_SUBDIRECTORY_ENABLED, true);
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
    prepareStep.setDisable(true);
    prepareStep.setGraphic(null);
    selectDirectoryStep.setDisable(true);
    selectDirectoryStep.setGraphic(null);
    verifyStep.setDisable(true);
    verifyStep.setGraphic(null);

    vst2ToggleButton.setDisable(!this.getPreferences().getBoolean(
            Prefs.Plugins.VST2_DISCOVERY_ENABLED, false));
    vst3ToggleButton.setDisable(!this.getPreferences().getBoolean(
            Prefs.Plugins.VST3_DISCOVERY_ENABLED, false));
    auToggleButton.setDisable(!this.getPreferences().getBoolean(
            Prefs.Plugins.AU_DISCOVERY_ENABLED, false));
    lv2ToggleButton.setDisable(!this.getPreferences().getBoolean(
            Prefs.Plugins.LV2_DISCOVERY_ENABLED, false));

    toggleGroup.selectToggle(null);

    directoryChooserButton.setVisible(false);
    directoryChooserButton.setManaged(false);
    directoryOverrideCheckBox.setDisable(true);
    directoryOverrideCheckBox.setSelected(false);

    installationDirectoryTextField.setText("");
    installationDirectoryText.setText("");
    directoryValidText.setText("");
    directoryValidText.getStyleClass().removeAll("label-success", "label-danger");
    directoryOverrideText.setText("");
    directoryOverrideText.getStyleClass().remove("label-warning");
    formatText.setText("");
  }


  private FontIcon createCheckIcon() {
    FontIcon icon = new FontIcon("mdi2c-check");
    icon.setIconSize(16);
    return icon;
  }

  @Override
  protected DialogLayout getLayout() {
    DialogLayout layout = new DialogLayout();
    Label title = new Label("Install Plugin");
    title.setGraphic(new FontIcon("mdi2d-download"));
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
