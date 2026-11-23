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
import com.owlplug.core.components.ImageCache;
import com.owlplug.core.components.LazyViewRegistry;
import com.owlplug.core.controllers.dialogs.AbstractDialogController;
import com.owlplug.core.tasks.TaskExecutionContext;
import com.owlplug.core.utils.FX;
import com.owlplug.core.utils.FileUtils;
import com.owlplug.core.utils.PlatformUtils;
import com.owlplug.explore.components.ExploreTaskFactory;
import com.owlplug.explore.model.PackageBundle;
import com.owlplug.explore.model.RemotePackage;
import com.owlplug.explore.tasks.InstallerDownloadTask;
import com.owlplug.plugin.components.PluginTaskFactory;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javafx.concurrent.WorkerStateEvent;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Guides the user through a package installer installation. OwlPlug downloads the installer
 * and verifies its checksum, then hands it over to the operating system. Since OwlPlug can't
 * follow what the installer does, the user reports when the installation is complete, which
 * triggers a plugin scan.
 */
@Controller
public class InstallerDialogController extends AbstractDialogController {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  /** Width of a step column in the stepper, wide enough for the longest step label. */
  private static final double STEP_WIDTH = 64;
  /** Diameter of a step circle, must match .installer-step-circle in owlplug.css. */
  private static final double STEP_CIRCLE_SIZE = 28;

  private static final PseudoClass ACTIVE = PseudoClass.getPseudoClass("active");
  private static final PseudoClass DONE = PseudoClass.getPseudoClass("done");
  private static final PseudoClass ERROR = PseudoClass.getPseudoClass("error");

  /** Steps displayed in the stepper, in order. */
  private enum Step {
    DOWNLOAD("Download"),
    VERIFY("Verify"),
    INSTALL("Install"),
    SCAN("Scan");

    private final String label;

    Step(String label) {
      this.label = label;
    }
  }

  /** Dialog states, each one displaying a single card. */
  private enum State {
    DOWNLOADING, READY, WAITING, SCANNING, DONE, ERROR
  }

  @Autowired
  private LazyViewRegistry lazyViewRegistry;
  @Autowired
  private ExploreTaskFactory exploreTaskFactory;
  @Autowired
  private PluginTaskFactory pluginTaskFactory;
  @Autowired
  private ImageCache imageCache;

  @FXML
  private StackPane thumbnailPane;
  @FXML
  private ImageView thumbnailImageView;
  @FXML
  private FontIcon thumbnailPlaceholder;
  @FXML
  private Label packageNameLabel;
  @FXML
  private Label packageMetaLabel;
  @FXML
  private FlowPane targetsPane;
  @FXML
  private HBox stepper;
  @FXML
  private VBox downloadCard;
  @FXML
  private Label downloadFileLabel;
  @FXML
  private ProgressBar downloadProgressBar;
  @FXML
  private Label downloadStatusLabel;
  @FXML
  private VBox readyCard;
  @FXML
  private Label hashLabel;
  @FXML
  private Button copyHashButton;
  @FXML
  private Label warningLabel;
  @FXML
  private VBox waitingCard;
  @FXML
  private Hyperlink relaunchLink;
  @FXML
  private Hyperlink showInFolderLink;
  @FXML
  private VBox doneCard;
  @FXML
  private FontIcon doneIcon;
  @FXML
  private ProgressIndicator scanIndicator;
  @FXML
  private Label doneTitleLabel;
  @FXML
  private Label doneMessageLabel;
  @FXML
  private VBox errorCard;
  @FXML
  private Label errorTitleLabel;
  @FXML
  private Label errorMessageLabel;
  @FXML
  private Label errorDetailLabel;
  @FXML
  private Button secondaryButton;
  @FXML
  private Button primaryButton;

  /** Nodes of a step in the stepper. */
  private record StepView(VBox node, Label number, FontIcon check, FontIcon cross) {
  }

  private final Map<Step, StepView> stepViews = new LinkedHashMap<>();
  private final List<Region> stepConnectors = new ArrayList<>();

  private PackageBundle bundle;
  private TaskExecutionContext downloadContext;
  private Path installerFile;
  private State state;
  private Step failedStep;

  public InstallerDialogController() {
    // No fixed size: the width comes from the view and the height follows the content, so
    // wrapped text is never clipped on small or low resolution screens.
    super();
    setOverlayClose(false);
  }

  /**
   * FXML initialize.
   */
  public void initialize() {
    buildStepper();

    Rectangle thumbnailClip = new Rectangle(56, 56);
    thumbnailClip.setArcWidth(14);
    thumbnailClip.setArcHeight(14);
    thumbnailPane.setClip(thumbnailClip);

    copyHashButton.setOnAction(e -> {
      ClipboardContent content = new ClipboardContent();
      content.putString(bundle.getDownloadSha256());
      Clipboard.getSystemClipboard().setContent(content);
    });
    relaunchLink.setOnAction(e -> launchInstaller());
    showInFolderLink.setOnAction(e -> {
      if (installerFile != null) {
        PlatformUtils.openFromDesktop(installerFile.getParent().toFile());
      }
    });
  }

  /**
   * Starts the installer flow for the given bundle: opens the dialog and downloads the
   * installer in the background.
   *
   * @param bundle installer bundle
   */
  public void start(PackageBundle bundle) {
    if (state == State.DOWNLOADING || state == State.READY || state == State.WAITING) {
      // An installer flow is already in progress, bring it back instead of starting another one
      this.show();
      return;
    }
    this.bundle = bundle;
    configureHeader(bundle.getRemotePackage());
    this.show();
    startDownload();
  }

  private void startDownload() {
    installerFile = null;
    failedStep = null;
    downloadProgressBar.progressProperty().unbind();
    downloadProgressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
    downloadFileLabel.setText(bundle.getFileSize() > 0
        ? "Size: " + FileUtils.humanReadableByteCount(bundle.getFileSize(), true)
        : "Preparing download...");

    downloadContext = exploreTaskFactory.createInstallerDownloadTask(bundle);
    InstallerDownloadTask task = (InstallerDownloadTask) downloadContext.getTask();
    TaskExecutionContext context = downloadContext;

    // Listeners ignore events from a previous download, cancelled or retried in the meantime
    task.progressProperty().addListener((obs, oldValue, progress) -> {
      if (downloadContext == context && state == State.DOWNLOADING && progress.doubleValue() >= 0) {
        downloadProgressBar.setProgress(progress.doubleValue());
        // The last part of the task progress is the checksum verification
        setActiveStep(progress.doubleValue() >= 100d / 110d ? Step.VERIFY : Step.DOWNLOAD);
        downloadStatusLabel.setText(progress.doubleValue() >= 100d / 110d
            ? "Verifying file integrity..."
            : Math.round(progress.doubleValue() * 100) + "% downloaded");
      }
    });
    task.addEventHandler(WorkerStateEvent.WORKER_STATE_RUNNING, e -> {
      if (downloadContext == context && state == State.DOWNLOADING) {
        downloadStatusLabel.setText("Starting download...");
      }
    });
    task.addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, e -> {
      if (downloadContext == context) {
        installerFile = task.getInstallerFile();
        setState(State.READY);
      }
    });
    task.addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, e -> {
      if (downloadContext == context) {
        Throwable ex = task.getException();
        showError(failedStepFromProgress(task.getProgress()), "The installer can't be used",
            ex != null ? ex.getMessage() : "The installer could not be downloaded.",
            ex != null && ex.getCause() != null ? ex.getCause().getMessage() : null);
      }
    });

    setState(State.DOWNLOADING);
    downloadContext.schedule();

    downloadStatusLabel.setText(downloadContext.isPending()
        ? "Waiting for other tasks to finish..."
        : "Starting download...");
  }

  private Step failedStepFromProgress(double progress) {
    return progress >= 100d / 110d ? Step.VERIFY : Step.DOWNLOAD;
  }

  private void launchInstaller() {
    if (installerFile == null) {
      return;
    }
    setState(State.WAITING);
    PlatformUtils.openFile(installerFile.toFile()).exceptionally(ex -> {
      log.error("Installer can't be opened: {}", installerFile, ex);
      Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
      FX.run(() -> showError(Step.INSTALL, "The installer can't be opened",
          "Your system refused to open the installer. You can open it manually from its folder.",
          cause.getMessage()));
      return null;
    });
  }

  private void completeInstallation() {
    setState(State.SCANNING);
    TaskExecutionContext scanContext = pluginTaskFactory.createPluginScanTask(true);
    // addEventHandler keeps the factory's own completion handlers in place
    scanContext.getTask().addEventHandler(WorkerStateEvent.WORKER_STATE_SUCCEEDED, e -> {
      if (state == State.SCANNING) {
        setState(State.DONE);
      }
    });
    scanContext.getTask().addEventHandler(WorkerStateEvent.WORKER_STATE_FAILED, e -> {
      if (state == State.SCANNING) {
        showError(Step.SCAN, "Plugin scan failed",
            "The installation is complete, but scanning your plugins failed. "
                + "You can start a new scan from the Plugins view.", null);
      }
    });
    scanContext.scheduleNow();
    deleteInstallerFile();
  }

  /**
   * Cancels the flow at any stage and closes the dialog.
   */
  private void cancel() {
    if (downloadContext != null && state == State.DOWNLOADING) {
      downloadContext.cancel();
    }
    deleteInstallerFile();
    state = null;
    this.close();
  }

  private void deleteInstallerFile() {
    if (downloadContext != null) {
      // Best effort: the OS may still lock the installer while it runs
      ((InstallerDownloadTask) downloadContext.getTask()).cleanup();
    }
  }

  private void setState(State newState) {
    this.state = newState;
    downloadCard.setVisible(newState == State.DOWNLOADING);
    readyCard.setVisible(newState == State.READY);
    waitingCard.setVisible(newState == State.WAITING);
    doneCard.setVisible(newState == State.SCANNING || newState == State.DONE);
    errorCard.setVisible(newState == State.ERROR);

    primaryButton.setVisible(true);
    primaryButton.setManaged(true);
    primaryButton.setDisable(false);
    secondaryButton.setText("Cancel");
    secondaryButton.setOnAction(e -> cancel());

    switch (newState) {
      case DOWNLOADING -> {
        setActiveStep(Step.DOWNLOAD);
        primaryButton.setText("Launch installer");
        primaryButton.setDisable(true);
      }
      case READY -> {
        setActiveStep(Step.INSTALL);
        hashLabel.setText(bundle.getDownloadSha256().toLowerCase());
        hashLabel.setTooltip(new Tooltip(bundle.getDownloadSha256().toLowerCase()));
        RemotePackage remotePackage = bundle.getRemotePackage();
        String creator = remotePackage.getCreator() != null ? remotePackage.getCreator() : "its creator";
        warningLabel.setText("This installer is provided by " + creator + " and runs outside of OwlPlug "
            + "with your system permissions. Only continue if you trust it.");
        primaryButton.setText("Launch installer");
        primaryButton.setOnAction(e -> launchInstaller());
      }
      case WAITING -> {
        setActiveStep(Step.INSTALL);
        primaryButton.setText("Installation complete");
        primaryButton.setOnAction(e -> completeInstallation());
      }
      case SCANNING -> {
        setActiveStep(Step.SCAN);
        doneIcon.setVisible(false);
        scanIndicator.setVisible(true);
        doneTitleLabel.setText("Looking for your new plugins");
        doneMessageLabel.setText("OwlPlug is scanning your plugin directories. You can close this window, "
            + "the scan continues in the background.");
        secondaryButton.setText("Close");
        secondaryButton.setOnAction(e -> this.close());
        primaryButton.setVisible(false);
        primaryButton.setManaged(false);
      }
      case DONE -> {
        setActiveStep(null);
        doneIcon.setVisible(true);
        scanIndicator.setVisible(false);
        doneTitleLabel.setText("All set!");
        doneMessageLabel.setText(bundle.getRemotePackage().getName() + " has been installed and your plugins "
            + "have been scanned. New plugins are available in the Plugins view.");
        secondaryButton.setText("Close");
        secondaryButton.setOnAction(e -> this.close());
        primaryButton.setVisible(false);
        primaryButton.setManaged(false);
      }
      case ERROR -> {
        secondaryButton.setText("Close");
        secondaryButton.setOnAction(e -> cancel());
        if (failedStep == Step.SCAN) {
          primaryButton.setVisible(false);
          primaryButton.setManaged(false);
        } else if (failedStep == Step.INSTALL) {
          primaryButton.setText("Show in folder");
          primaryButton.setOnAction(e -> {
            PlatformUtils.openFromDesktop(installerFile.getParent().toFile());
            setState(State.WAITING);
          });
        } else {
          primaryButton.setText("Try again");
          primaryButton.setOnAction(e -> startDownload());
        }
      }
      default -> { }
    }
  }

  private void showError(Step step, String title, String message, String detail) {
    failedStep = step;
    errorTitleLabel.setText(title);
    errorMessageLabel.setText(message);
    errorDetailLabel.setText(detail);
    errorDetailLabel.setVisible(detail != null && !detail.isBlank());
    errorDetailLabel.setManaged(errorDetailLabel.isVisible());
    setState(State.ERROR);
    updateStepper(step, true);
  }

  private void configureHeader(RemotePackage remotePackage) {
    packageNameLabel.setText(remotePackage.getName());

    List<String> meta = new ArrayList<>();
    if (remotePackage.getVersion() != null) {
      meta.add("v" + remotePackage.getVersion());
    }
    if (remotePackage.getCreator() != null) {
      meta.add(remotePackage.getCreator());
    }
    if (remotePackage.getRemoteSource() != null) {
      meta.add(remotePackage.getRemoteSource().getName());
    }
    packageMetaLabel.setText(String.join("  ·  ", meta));

    targetsPane.getChildren().clear();
    if (bundle.getTargets() != null) {
      for (String target : bundle.getTargets()) {
        Label chip = new Label(target);
        chip.getStyleClass().add("package-bundle-target");
        targetsPane.getChildren().add(chip);
      }
    }

    // The placeholder stays visible until the screenshot is actually available, since the cache
    // may still be loading it in the background, or fail to load it.
    thumbnailImageView.setImage(null);
    thumbnailPlaceholder.setVisible(true);
    Image screenshot = imageCache.get(remotePackage.getScreenshotUrl());
    if (screenshot != null) {
      if (screenshot.getProgress() >= 1) {
        showThumbnail(screenshot);
      } else {
        screenshot.progressProperty().addListener((obs, oldValue, progress) -> {
          if (progress.doubleValue() >= 1 && this.bundle != null
              && this.bundle.getRemotePackage() == remotePackage) {
            FX.run(() -> showThumbnail(screenshot));
          }
        });
      }
    }
  }

  /**
   * Displays the image cropped to a centered square, like a CSS "cover" background.
   */
  private void showThumbnail(Image image) {
    if (image.isError() || image.getWidth() <= 0 || image.getHeight() <= 0) {
      return;
    }
    double side = Math.min(image.getWidth(), image.getHeight());
    thumbnailImageView.setViewport(new Rectangle2D((image.getWidth() - side) / 2,
        (image.getHeight() - side) / 2, side, side));
    thumbnailImageView.setImage(image);
    thumbnailPlaceholder.setVisible(false);
  }

  private void buildStepper() {
    stepper.getChildren().clear();
    // Connectors extend under the neighbor steps up to their circles, whatever the label
    // widths, and are drawn behind the circles (opaque) so the line always meets them.
    double connectorOverlap = (STEP_WIDTH - STEP_CIRCLE_SIZE) / 2;
    int index = 1;
    for (Step step : Step.values()) {
      if (index > 1) {
        Region connector = new Region();
        connector.getStyleClass().add("installer-step-connector");
        connector.setViewOrder(1);
        HBox.setMargin(connector, new Insets(0, -connectorOverlap, 0, -connectorOverlap));
        HBox.setHgrow(connector, Priority.ALWAYS);
        stepConnectors.add(connector);
        stepper.getChildren().add(connector);
      }
      Label number = new Label(String.valueOf(index));
      number.getStyleClass().add("installer-step-number");
      FontIcon check = new FontIcon("mdi2c-check");
      check.setIconSize(14);
      check.getStyleClass().add("installer-step-check");
      FontIcon cross = new FontIcon("mdi2c-close");
      cross.setIconSize(14);
      cross.getStyleClass().add("installer-step-cross");
      StackPane circle = new StackPane(number, check, cross);
      circle.getStyleClass().add("installer-step-circle");

      Label label = new Label(step.label);
      label.getStyleClass().add("installer-step-label");

      VBox node = new VBox(6, circle, label);
      node.setAlignment(Pos.TOP_CENTER);
      // Same width for every step, so circles are evenly spaced whatever their label
      node.setMinWidth(STEP_WIDTH);
      node.setPrefWidth(STEP_WIDTH);
      node.setMaxWidth(STEP_WIDTH);
      node.getStyleClass().add("installer-step");
      stepViews.put(step, new StepView(node, number, check, cross));
      stepper.getChildren().add(node);
      index++;
    }
  }

  /**
   * Marks the given step as active, previous ones as done and next ones as pending.
   * A null step marks every step as done.
   */
  private void setActiveStep(Step activeStep) {
    updateStepper(activeStep, false);
  }

  private void updateStepper(Step currentStep, boolean error) {
    int current = currentStep == null ? Step.values().length : currentStep.ordinal();
    for (Map.Entry<Step, StepView> entry : stepViews.entrySet()) {
      int ordinal = entry.getKey().ordinal();
      StepView view = entry.getValue();
      view.node().pseudoClassStateChanged(DONE, ordinal < current);
      view.node().pseudoClassStateChanged(ACTIVE, ordinal == current && !error);
      view.node().pseudoClassStateChanged(ERROR, ordinal == current && error);
      view.check().setVisible(ordinal < current);
      view.cross().setVisible(ordinal == current && error);
      view.number().setVisible(!view.check().isVisible() && !view.cross().isVisible());
    }
    for (int i = 0; i < stepConnectors.size(); i++) {
      // Connector i links step i and step i + 1
      stepConnectors.get(i).pseudoClassStateChanged(DONE, i < current);
    }
  }

  @Override
  protected void onDialogClose() {
    // Closing the dialog while downloading behaves like a cancellation
    if (state == State.DOWNLOADING || state == State.READY || state == State.WAITING || state == State.ERROR) {
      cancel();
    }
  }

  @Override
  protected DialogLayout getLayout() {
    DialogLayout layout = new DialogLayout();
    Label title = new Label("Run Installer");
    title.setGraphic(new FontIcon("mdi2a-application-export"));
    title.getStyleClass().add("heading-3");
    layout.setHeading(title);
    layout.setBody(lazyViewRegistry.get(LazyViewRegistry.INSTALLER_DIALOG_VIEW));
    return layout;
  }
}
