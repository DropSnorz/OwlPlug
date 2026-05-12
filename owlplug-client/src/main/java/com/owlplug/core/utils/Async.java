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

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Async {

  private static final Logger log = LoggerFactory.getLogger(Async.class);

  // One virtual thread per task — safe for blocking I/O and database calls.
  private static final Executor VIRTUAL = Executors.newVirtualThreadPerTaskExecutor();

  private Async() {
  }

  /**
   * Run a task on a virtual thread. Exceptions are logged as a side-effect;
   * the returned future still completes exceptionally so callers can chain
   * their own {@code .exceptionally()} if needed.
   */
  public static CompletableFuture<Void> run(Runnable task) {
    CompletableFuture<Void> cf = CompletableFuture.runAsync(task, VIRTUAL);
    cf.whenComplete((result, ex) -> {
      if (ex != null) {
        log.error("Unhandled async exception", ex);
      }
    });
    return cf;
  }

  /**
   * Supply a value on a virtual thread. Exceptions are logged as a side-effect;
   * the returned future still completes exceptionally so callers can chain
   * their own {@code .exceptionally()} if needed.
   */
  public static <T> CompletableFuture<T> supply(Supplier<T> task) {
    CompletableFuture<T> cf = CompletableFuture.supplyAsync(task, VIRTUAL);
    cf.whenComplete((result, ex) -> {
      if (ex != null) {
        log.error("Unhandled async exception", ex);
      }
    });
    return cf;
  }

  /**
   * Raw virtual-thread {@code runAsync} with no default handler — use when the
   * caller owns exception handling entirely via {@code .exceptionally()}.
   */
  public static CompletableFuture<Void> runAsync(Runnable task) {
    return CompletableFuture.runAsync(task, VIRTUAL);
  }

  /**
   * Raw virtual-thread {@code supplyAsync} with no default handler — use when
   * the caller owns exception handling entirely via {@code .exceptionally()}.
   */
  public static <T> CompletableFuture<T> supplyAsync(Supplier<T> task) {
    return CompletableFuture.supplyAsync(task, VIRTUAL);
  }

}
