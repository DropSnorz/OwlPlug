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

package com.owlplug.core.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CryptoUtils {

  /**
   * Computes the SHA-256 digest of a file.
   *
   * @deprecated use {@link #getFileSha256Digest(Path)}
   */
  @Deprecated
  public static String getFileSha256Digest(File file) throws IOException {
    return getFileSha256Digest(file.toPath());
  }

  public static String getFileSha256Digest(Path file) throws IOException {

    byte[] buffer = new byte[8192];
    int count;

    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      try (BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(file))) {
        while ((count = bis.read(buffer)) > 0) {
          digest.update(buffer, 0, count);
        }
      }
      return bytesToHex(digest.digest());

    } catch (NoSuchAlgorithmException e) {
      throw new IOException(e);
    }

  }

  private static String bytesToHex(byte[] hash) {
    StringBuilder hexString = new StringBuilder(2 * hash.length);
    for (byte b : hash) {
      String hex = Integer.toHexString(0xff & b);
      if (hex.length() == 1) {
        hexString.append('0');
      }
      hexString.append(hex);
    }
    return hexString.toString();
  }

}
