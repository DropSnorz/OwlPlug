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
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandRunner {

  private static final Logger log = LoggerFactory.getLogger(CommandRunner.class);

  private static final String NEWLINE = System.getProperty("line.separator");

  private boolean timeoutActivated = false;
  private long timeout = 0;

  public CommandRunner() {

  }

  /**
   * Run a system command.
   *
   * @param command the command to run
   * @return the {@link CommandResult}
   * @throws IOException if an I/O error occurs
   */
  public CommandResult run(String... command) throws IOException {
    ProcessBuilder pb = new ProcessBuilder(command).redirectErrorStream(true);
    Process process = pb.start();
    Instant processStartedAt = Instant.now();
    StringBuilder result = new StringBuilder(80);
    try (BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
      boolean keepReading = true;
      while (keepReading) {
        // Read process stream output
        String line = in.readLine();
        if (line == null) {
          keepReading = false;
        } else {
          result.append(line).append(NEWLINE);
        }

        // Verify timeout status
        Instant currentInstant = Instant.now();
        if(keepReading && timeoutActivated && ChronoUnit.MILLIS.between(processStartedAt, currentInstant) > timeout) {
          log.error("Command line process not terminated after {} ms timeout", timeout);
          log.error("Destroying command line process");
          process.destroy();
          throw new IOException("Timeout exceeded for subprocess output retrieval");
        }
      }

    }

    // Wait for the subprocess to exit
    try {
      if (timeoutActivated) {
        Instant currentInstant = Instant.now();
        boolean exited = process.waitFor(ChronoUnit.MILLIS.between(processStartedAt, currentInstant), TimeUnit.MILLISECONDS);
        if (!exited) {
          log.error("Command line process not terminated after {} ms timeout", timeout);
          log.error("Destroying command line process");
          process.destroy();
          throw new IOException("Timeout exceeded for subprocess to exit");
        }
      } else {
        process.wait();
      }
      return new CommandResult(process.exitValue(), result.toString());

    } catch (InterruptedException e) {
      throw new IOException("Current thread has been interrupted while waiting subprocess", e);
    }

  }

  public boolean isTimeoutActivated() {
    return timeoutActivated;
  }

  public void setTimeoutActivated(boolean timeoutActivated) {
    this.timeoutActivated = timeoutActivated;
  }

  public long getTimeout() {
    return timeout;
  }

  public void setTimeout(long timeout) {
    this.timeout = timeout;
  }
}
