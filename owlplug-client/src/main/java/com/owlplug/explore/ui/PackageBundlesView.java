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

package com.owlplug.explore.ui;

import atlantafx.base.theme.Styles;
import com.owlplug.core.components.ApplicationDefaults;
import com.owlplug.core.utils.FileUtils;
import com.owlplug.explore.model.PackageBundle;
import com.owlplug.plugin.ui.PluginFormatBadgeView;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

public class PackageBundlesView extends VBox {

  private static final Map<String, String> OS_LABELS = Map.of(
      "win", "Windows",
      "mac", "macOS",
      "linux", "Linux");

  /**
   * Legacy platform tags published by some sources, mapped to their canonical
   * {@code os-arch} form so they render like any other target.
   */
  private static final Map<String, String> TARGET_ALIASES = Map.of(
      "win32", "win-x32",
      "win64", "win-x64",
      "linux32", "linux-x32",
      "linux64", "linux-x64");

  private final ApplicationDefaults applicationDefaults;

  public PackageBundlesView(ApplicationDefaults applicationDefaults) {
    super(6);
    this.applicationDefaults = applicationDefaults;
  }

  public void clear() {
    this.getChildren().clear();
  }

  /**
   * Appends a bundle row: supported platforms on the first line, formats and download size on
   * the second one, install action on the right. Both lines wrap, so bundles with many targets
   * or formats grow in height instead of squeezing the install button out of the row.
   *
   * @param bundle         bundle to display
   * @param installHandler action triggered by the row install button
   */
  public void addPackageBundle(PackageBundle bundle, EventHandler<ActionEvent> installHandler) {

    HBox row = new HBox(10);
    row.getStyleClass().add("package-bundle-row");
    row.setAlignment(Pos.CENTER_LEFT);
    // Keeps the install button at its natural height instead of stretching it over the
    // full row height when the details column wraps on several lines.
    row.setFillHeight(false);

    VBox details = new VBox(5);
    details.setAlignment(Pos.CENTER_LEFT);
    // Allows the details column to shrink under its computed width, so its content wraps
    // instead of pushing the install button out of the row.
    details.setMinWidth(0);
    HBox.setHgrow(details, Priority.ALWAYS);
    details.getChildren().add(buildTargetsPane(bundle));

    FlowPane metaPane = buildMetaPane(bundle);
    if (!metaPane.getChildren().isEmpty()) {
      details.getChildren().add(metaPane);
    }
    row.getChildren().add(details);

    Button installButton = new Button("Install");
    installButton.getStyleClass().addAll(Styles.SMALL, "package-bundle-install");
    installButton.setOnAction(installHandler);
    installButton.setMinWidth(USE_PREF_SIZE);
    row.getChildren().add(installButton);

    this.getChildren().add(row);
  }

  /**
   * Builds the platform line, one chip per operating system, listing the architectures it is
   * available for.
   */
  private FlowPane buildTargetsPane(PackageBundle bundle) {

    FlowPane pane = new FlowPane(4, 4);
    pane.setAlignment(Pos.CENTER_LEFT);

    for (Map.Entry<String, List<String>> target : groupTargetsByOs(bundle.getTargets()).entrySet()) {
      pane.getChildren().add(buildTargetChip(target.getKey(), target.getValue()));
    }

    // Bundles without any declared target are not restricted to a given platform.
    if (pane.getChildren().isEmpty()) {
      pane.getChildren().add(buildTargetChip(null, List.of()));
    }
    return pane;
  }

  private HBox buildTargetChip(String os, List<String> architectures) {

    HBox chip = new HBox(4);
    chip.setAlignment(Pos.CENTER_LEFT);
    chip.getStyleClass().add("package-bundle-target");

    FontIcon icon = new FontIcon(osIconLiteral(os));
    icon.setIconSize(13);
    chip.getChildren().add(icon);

    String osLabel = os == null ? "Any platform" : OS_LABELS.getOrDefault(os, os);
    Label osNode = new Label(osLabel);
    osNode.getStyleClass().add("package-bundle-target-os");
    chip.getChildren().add(osNode);

    if (!architectures.isEmpty()) {
      Label archNode = new Label(String.join(" ", architectures));
      archNode.getStyleClass().add("package-bundle-target-arch");
      chip.getChildren().add(archNode);
      Tooltip.install(chip, new Tooltip(osLabel + " " + String.join(", ", architectures)));
    } else {
      Tooltip.install(chip, new Tooltip(osLabel));
    }

    return chip;
  }

  /**
   * Builds the secondary line, holding the plugin formats packed in the bundle and its
   * download size.
   */
  private FlowPane buildMetaPane(PackageBundle bundle) {

    FlowPane pane = new FlowPane(4, 4);
    pane.setAlignment(Pos.CENTER_LEFT);

    if (bundle.getFormats() != null) {
      for (String format : bundle.getFormats()) {
        pane.getChildren().add(new PluginFormatBadgeView(format, applicationDefaults));
      }
    }

    if (bundle.getFileSize() != 0) {
      Label sizeLabel = new Label(FileUtils.humanReadableByteCount(bundle.getFileSize(), true));
      sizeLabel.getStyleClass().add("package-bundle-size");
      pane.getChildren().add(sizeLabel);
    }

    return pane;
  }

  /**
   * Groups platform tags by operating system, keeping the source order.
   * {@code [win-x64, mac-arm64, win-x32]} becomes {@code {win: [x64, x32], mac: [arm64]}}.
   */
  private Map<String, List<String>> groupTargetsByOs(List<String> targets) {

    Map<String, List<String>> groupedTargets = new LinkedHashMap<>();
    if (targets == null) {
      return groupedTargets;
    }

    for (String rawTarget : targets) {
      if (rawTarget == null || rawTarget.isBlank()) {
        continue;
      }
      String target = TARGET_ALIASES.getOrDefault(rawTarget, rawTarget);
      int separatorIndex = target.indexOf('-');
      String os = separatorIndex > 0 ? target.substring(0, separatorIndex) : target;
      String architecture = separatorIndex > 0 ? target.substring(separatorIndex + 1) : null;

      List<String> architectures = groupedTargets.computeIfAbsent(os, key -> new ArrayList<>());
      if (architecture != null && !architectures.contains(architecture)) {
        architectures.add(architecture);
      }
    }
    return groupedTargets;
  }

  private String osIconLiteral(String os) {
    if (os == null) {
      return "mdi2d-devices";
    }
    return switch (os) {
      case "win" -> "mdi2m-microsoft-windows";
      case "mac" -> "mdi2a-apple";
      case "linux" -> "mdi2l-linux";
      default -> "mdi2d-devices";
    };
  }

}
