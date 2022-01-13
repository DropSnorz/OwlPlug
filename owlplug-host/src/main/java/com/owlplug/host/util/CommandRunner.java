package com.owlplug.host.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandRunner {

  private static final String NEWLINE = System.getProperty("line.separator");

  private CommandRunner() {

  }

  /**
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
        if (line == null)
          break;
        result.append(line).append(NEWLINE);
      }
    }
    return new CommandResult(process.exitValue(), result.toString());
  }

}
