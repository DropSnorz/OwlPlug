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

package com.owlplug.core.components.telemetry;

import com.mixpanel.mixpanelapi.ClientDelivery;
import com.mixpanel.mixpanelapi.MessageBuilder;
import com.mixpanel.mixpanelapi.MixpanelAPI;
import java.io.IOException;
import java.util.Map;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Plain (non-Spring) helper encapsulating Mixpanel message construction, sanitization and
 * delivery. Used both by the DI-backed TelemetryService and by StartupFailureTelemetry, which
 * runs before/without a live Spring context.
 */
public class TelemetryReporter {

  private static final Logger log = LoggerFactory.getLogger(TelemetryReporter.class);

  private static final int MAX_PROPS_LENGTH = 2000;

  private final MixpanelAPI mixpanel;
  private final MessageBuilder messageBuilder;
  private final String userId;

  public TelemetryReporter(String eventsEndpoint, String peopleEndpoint, String projectToken, String userId) {
    this.mixpanel = new MixpanelAPI(eventsEndpoint, peopleEndpoint);
    this.messageBuilder = new MessageBuilder(projectToken);
    this.userId = userId;
  }

  /**
   * Sends a single event. This call is blocking (synchronous network delivery).
   */
  public void send(String name, Map<String, String> params) {
    JSONObject props = new JSONObject(params);
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

  public static void sanitize(Map<String, String> params) {
    sanitize(params, MAX_PROPS_LENGTH);
  }

  public static void sanitize(Map<String, String> params, int maxLength) {
    for (Map.Entry<String, String> entry : params.entrySet()) {
      String value = entry.getValue();
      if (value == null) {
        continue;
      }

      if (value.length() > maxLength) {
        value = value.substring(0, maxLength) + "…";
      }

      // Redact absolute paths (Unix & Windows)
      value = value.replaceAll("([A-Za-z]:\\\\\\\\[^\\s]+)|(/[^\\s]+)", "<path>");
      entry.setValue(value);

    }
  }

}
