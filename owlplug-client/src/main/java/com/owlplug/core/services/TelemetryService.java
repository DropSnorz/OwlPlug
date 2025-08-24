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


import com.mixpanel.mixpanelapi.ClientDelivery;
import com.mixpanel.mixpanelapi.MessageBuilder;
import com.mixpanel.mixpanelapi.MixpanelAPI;
import com.owlplug.core.components.ApplicationDefaults;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class TelemetryService extends BaseService {

  private final Logger log = LoggerFactory.getLogger(this.getClass());

  private MessageBuilder messageBuilder;

  private MixpanelAPI mixpanel;

  private String userId = null;

  private static final int MAX_PROPS_LENGTH = 2000;

  private static final List<String> allowedEvents = List.of(
      "/Startup",
      "/Error/PluginScanIncomplete",
      "/Error/TaskExecution"
  );

  @PostConstruct
  private void initialize() {
    mixpanel = new MixpanelAPI("https://api-eu.mixpanel.com/track",
        "https://api-eu.mixpanel.com/engage");
    messageBuilder = new MessageBuilder(this.getApplicationDefaults().getEnvProperty("owlplug.telemetry.code"));

    userId = this.getPreferences().get(ApplicationDefaults.TELEMETRY_USER_ID_KEY, UUID.randomUUID().toString());
    this.getPreferences().put(ApplicationDefaults.TELEMETRY_USER_ID_KEY, userId);

  }

  public void event(String name) {
    event(name, p -> { });
  }

  public void event(String name, Consumer<Map<String, String>> builder) {
    if (!this.getPreferences().getBoolean(ApplicationDefaults.TELEMETRY_ENABLED_KEY, false)) {
      return;
    }
    if (!allowedEvents.contains(name)) {
      return;
    }

    Thread.ofVirtual().start(() -> {
      Map<String, String> params = new HashMap<>();
      builder.accept(params);

      sanitize(params);
      params.put("appVersion", this.getApplicationDefaults().getVersion());
      params.put("systemTag", this.getApplicationDefaults().getRuntimePlatform().getTag());

      send(name, params);
    });

  }

  private void send(String name, Map<String, String> params) {
    JSONObject props = new JSONObject(params);

    // Create an event
    JSONObject event = messageBuilder.event(userId, name, props);

    ClientDelivery delivery = new ClientDelivery();
    delivery.addMessage(event);

    try {
      mixpanel.deliver(delivery);
    } catch (IOException e) {
      // Exception can be ignored (Network connection lost, backend offline, ...)
      log.debug("Telemetry event '{}' not sent: {}", name, e.getMessage());
    }
  }

  private void sanitize(Map<String, String> params) {
    for (Map.Entry<String, String> entry : params.entrySet()) {
      String value = entry.getValue();
      if (value.length() > MAX_PROPS_LENGTH) {
        value = value.substring(0, MAX_PROPS_LENGTH) + "…";
      }

      // Redact absolute paths (Unix & Windows)
      value = value.replaceAll("([A-Za-z]:\\\\\\\\[^\\s]+)|(/[^\\s]+)", "<path>");
      entry.setValue(value);

    }
  }

}
