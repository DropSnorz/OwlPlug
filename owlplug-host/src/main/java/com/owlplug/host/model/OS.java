package com.owlplug.host.model;

import java.util.Objects;

public enum OS {

  AIX,
  FREEBSD,
  LINUX,
  MAC,
  OPENBSD,
  SOLARIS,
  WINDOWS,
  OTHER;

  private static final OS CURRENT_OS = parse(System.getProperty("os.name"));

  public static OS current() {
    return CURRENT_OS;
  }

  static OS parse(String osName) {
    if (Objects.isNull(osName) || osName.isBlank()) {
      return null;
    }
    osName = osName.toLowerCase();

    if (osName.contains("aix")) {
      return AIX;
    }
    if (osName.contains("freebsd")) {
      return FREEBSD;
    }
    if (osName.contains("linux")) {
      return LINUX;
    }
    if (osName.contains("mac")) {
      return MAC;
    }
    if (osName.contains("openbsd")) {
      return OPENBSD;
    }
    if (osName.contains("sunos") || osName.contains("solaris")) {
      return SOLARIS;
    }
    if (osName.contains("win")) {
      return WINDOWS;
    }
    return OTHER;
  }

  public boolean isCurrentOs() {
    return this == CURRENT_OS;
  }
}

