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

package com.owlplug.core.services;


import com.owlplug.core.components.ApplicationDefaults.Prefs;
import com.owlplug.core.components.telemetry.TelemetryReporter;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import org.springframework.stereotype.Service;

@Service
public class TelemetryService extends BaseService {

  private TelemetryReporter reporter;
  private String userId = null;

  private static final List<String> allowedEvents = List.of(
      "/Startup",
      "/Error/PluginScanIncomplete",
      "/Error/TaskExecution",
      "/Error/StartupFailure"
  );

  @PostConstruct
  private void initialize() {
    userId = this.getPreferences().get(Prefs.Telemetry.USER_ID, UUID.randomUUID().toString());
    this.getPreferences().put(Prefs.Telemetry.USER_ID, userId);

    reporter = new TelemetryReporter("https://api-eu.mixpanel.com/track",
        "https://api-eu.mixpanel.com/engage",
        this.getApplicationDefaults().getEnvProperty("owlplug.telemetry.code"), userId);
  }

  public void event(String name) {
    event(name, p -> { });
  }

  public void event(String name, Consumer<Map<String, String>> builder) {
    if (!this.getPreferences().getBoolean(Prefs.Telemetry.ENABLED, false)) {
      return;
    }
    if (!allowedEvents.contains(name)) {
      return;
    }

    Thread.ofVirtual().start(() -> {
      Map<String, String> params = new HashMap<>();
      builder.accept(params);

      TelemetryReporter.sanitize(params);
      params.put("appVersion", this.getApplicationDefaults().getVersion());
      params.put("systemTag", this.getApplicationDefaults().getRuntimePlatform().getTag());

      reporter.send(name, params);
    });

  }

}
