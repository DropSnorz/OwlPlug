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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
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

    ExecutorService executor = Executors.newFixedThreadPool(1);

    Callable<String> stdoutReader = new StreamReader(process.getInputStream());
    Future<String> stdoutFuture = executor.submit(stdoutReader);

    boolean finished;
    try {
      if (timeoutActivated) {
        finished = process.waitFor(timeout, TimeUnit.MILLISECONDS);
      } else {
        process.waitFor();
        finished = true;
      }

    } catch (InterruptedException e) {
      log.error("Interrupted while waiting for process");
      process.destroy();
      throw new IOException("Interrupted while waiting for process", e);
    }

    if (!finished) {
      log.error("Forcibly destroying process after timeout {}ms exceeded.", timeout);
      process.destroyForcibly();
      throw new IOException("Process timeout exceeded: " + timeout + "ms");
    }

    try {
      // Let 1 seconds for gracefully read and complete process
      String stdout = stdoutFuture.get(1, TimeUnit.SECONDS);
      return new CommandResult(process.exitValue(), stdout);
    } catch (InterruptedException e) {
      throw new IOException("Interrupted while reading process output", e);
    } catch (ExecutionException | TimeoutException e) {
      throw new IOException("Failed to read process output", e);
    } finally {
      executor.shutdownNow();
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

  // Helper class to read from InputStream
  public static class StreamReader implements Callable<String> {
    private final InputStream inputStream;

    public StreamReader(InputStream inputStream) {
      this.inputStream = inputStream;
    }

    @Override
    public String call() throws Exception {
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
          output.append(line).append(System.lineSeparator());
        }
        return output.toString();
      }
    }
  }
}
