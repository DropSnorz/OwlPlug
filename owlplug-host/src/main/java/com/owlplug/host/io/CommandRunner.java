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

package com.owlplug.host.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandRunner {

  private static final String NEWLINE = System.getProperty("line.separator");

  private CommandRunner() {

  }

  /**
   * Run a system command.
   * @param command the command to run
   * @return the {@link CommandResult}
   * @throws IOException if an I/O error occurs
   */
  public static CommandResult run(String... command) throws IOException {
    ProcessBuilder pb = new ProcessBuilder(command).redirectErrorStream(true);
    Process process = pb.start();
    StringBuilder result = new StringBuilder(80);
    try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
      while (true) {
        String line = in.readLine();
        if (line == null) {
          break;
        }
        result.append(line).append(NEWLINE);
      }
    }
    return new CommandResult(process.exitValue(), result.toString());
  }

}
