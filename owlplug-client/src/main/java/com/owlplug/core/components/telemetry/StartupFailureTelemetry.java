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

import com.owlplug.core.components.RuntimePlatformResolver;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.prefs.Preferences;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.NestedExceptionUtils;

/**
 * Reports application startup / bean-initialization failures to telemetry.
 *
 * <p>Registered directly on the SpringApplicationBuilder (not as a Spring bean), so it is
 * notified of {@link ApplicationFailedEvent} even when the Spring context never finishes
 * building. Because of that, it can't rely on any DI-managed bean (TelemetryService included)
 * and independently reproduces the minimal bits it needs: the opt-in flag and user id via
 * the same Preferences node the app already uses, and the Mixpanel project token/app version
 * read directly from the packaged application.properties.
 */
public class StartupFailureTelemetry implements ApplicationListener<ApplicationFailedEvent> {

  private static final Logger log = LoggerFactory.getLogger(StartupFailureTelemetry.class);

  private static final String PREFS_NODE = "com.owlplug.user";
  private static final String PREF_TELEMETRY_ENABLED = "telemetry.enabled";
  private static final String PREF_TELEMETRY_USER_ID = "telemetry.user_id";

  private static final String EVENT_NAME = "/Error/StartupFailure";
  private static final long TIMEOUT_SECONDS = 4;

  @Override
  public void onApplicationEvent(ApplicationFailedEvent event) {
    try {
      Throwable ex = event.getException();
      report(ex.getClass().getSimpleName(), rootCauseClassName(ex), rootCauseMessage(ex),
          determinePhase(ex), ex.getMessage());
    } catch (Exception e) {
      // Best-effort only; must never interfere with the startup failure being propagated.
      log.debug("Could not report startup failure telemetry", e);
    }
  }

  static String determinePhase(Throwable ex) {
    if (ex instanceof BeanCreationException) {
      Throwable rootCause = NestedExceptionUtils.getMostSpecificCause(ex);
      return rootCause instanceof HibernateException ? "already_running" : "bean_creation";
    }
    return "generic";
  }

  static String rootCauseClassName(Throwable ex) {
    Throwable rootCause = NestedExceptionUtils.getMostSpecificCause(ex);
    return rootCause != ex ? rootCause.getClass().getSimpleName() : null;
  }

  static String rootCauseMessage(Throwable ex) {
    Throwable rootCause = NestedExceptionUtils.getMostSpecificCause(ex);
    return rootCause != ex ? rootCause.getMessage() : null;
  }

  private void report(String errorClass, String rootCauseClass, String rootCauseMessage,
      String phase, String error) {
    Preferences prefs = Preferences.userRoot().node(PREFS_NODE);
    if (!prefs.getBoolean(PREF_TELEMETRY_ENABLED, false)) {
      return;
    }

    String userId = prefs.get(PREF_TELEMETRY_USER_ID, null);
    if (userId == null) {
      userId = UUID.randomUUID().toString();
      prefs.put(PREF_TELEMETRY_USER_ID, userId);
    }

    Properties appProps = loadApplicationProperties();
    String token = appProps.getProperty("owlplug.telemetry.code");
    if (token == null || token.isBlank()) {
      log.debug("Telemetry token unavailable, skipping startup failure report");
      return;
    }

    Map<String, String> params = new HashMap<>();
    params.put("errorClass", errorClass);
    params.put("phase", phase);
    if (error != null) {
      params.put("error", error);
    }
    if (rootCauseClass != null) {
      params.put("rootCauseClass", rootCauseClass);
    }
    if (rootCauseMessage != null) {
      params.put("rootCauseMessage", rootCauseMessage);
    }
    TelemetryReporter.sanitize(params);
    params.put("appVersion", appProps.getProperty("owlplug.version"));
    params.put("systemTag", new RuntimePlatformResolver().getCurrentPlatform().getTag());

    TelemetryReporter reporter = new TelemetryReporter(
        "https://api-eu.mixpanel.com/track", "https://api-eu.mixpanel.com/engage", token, userId);

    CompletableFuture<Void> future = CompletableFuture.runAsync(() -> reporter.send(EVENT_NAME, params));
    try {
      future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
    } catch (TimeoutException e) {
      log.debug("Startup failure telemetry timed out after {}s, continuing shutdown", TIMEOUT_SECONDS);
    } catch (Exception e) {
      log.debug("Startup failure telemetry could not be sent", e);
    }
  }

  private Properties loadApplicationProperties() {
    Properties props = new Properties();
    try (InputStream in = StartupFailureTelemetry.class.getResourceAsStream("/application.properties")) {
      if (in != null) {
        props.load(in);
      }
    } catch (IOException e) {
      log.debug("Could not read application.properties for telemetry", e);
    }
    return props;
  }

}
