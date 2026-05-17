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
import java.util.concurrent.atomic.AtomicLong;
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

  /**
   * Ensures that only the result of the <em>latest</em> async call is ever
   * delivered to the caller, discarding results from superseded invocations.
   *
   * <h3>Problem</h3>
   * When the same async operation is triggered multiple times in quick
   * succession (e.g. a UI selection change firing {@code refresh()}), results
   * can arrive out of order: an older, slower query may resolve after a newer
   * one, overwriting fresh data with stale data.
   *
   * <h3>Solution</h3>
   * Each call to {@link #supply} or {@link #run} atomically claims a new
   * <em>generation stamp</em>. When the result arrives, it is only forwarded
   * if the stamp still matches the current generation — i.e. no newer call has
   * been made in the meantime. Stale futures are silently left incomplete, so
   * any chained {@code .thenAccept()} simply never fires. No extra logic is
   * required at the call site.
   *
   * <h3>Usage</h3>
   * Declare one {@code Sequence} field per independent refresh slot on the
   * controller, then replace {@code Async.supply(...)} with
   * {@code mySequence.supply(...)}:
   * <pre>{@code
   * private final Async.Sequence refreshSeq = new Async.Sequence();
   *
   * void refresh() {
   *     refreshSeq.supply(() -> repository.findAll())
   *               .thenAccept(data -> FX.run(() -> listView.setItems(data)));
   * }
   * }</pre>
   *
   * <h3>Notes</h3>
   * <ul>
   *   <li>Exceptions are always logged, even for stale results, because a DB
   *       error is worth knowing about regardless of whether it was superseded.</li>
   *   <li>Stale futures are never completed, so they carry no memory overhead
   *       beyond normal GC eligibility once the chain is unreachable.</li>
   *   <li>Use one {@code Sequence} per independent data slot. A controller with
   *       two unrelated async loads should use two separate instances.</li>
   * </ul>
   */
  public static final class Sequence {

    private final AtomicLong generation = new AtomicLong();

    /**
     * Submits {@code task} on a virtual thread and returns a guarded future.
     * The future completes normally only if no newer call to this method has
     * been made by the time the task finishes; otherwise it is left incomplete
     * and downstream stages are never executed.
     *
     * @param task the blocking supplier to run off the FX thread
     * @param <T>  the result type
     * @return a future that delivers the result only when it is still current
     */
    public <T> CompletableFuture<T> supply(Supplier<T> task) {
      // Claim this invocation's stamp before launching the task so that any
      // call arriving concurrently gets a strictly higher generation number.
      long stamp = generation.incrementAndGet();
      CompletableFuture<T> inner = CompletableFuture.supplyAsync(task, VIRTUAL);
      CompletableFuture<T> guarded = new CompletableFuture<>();
      inner.whenComplete((result, ex) -> {
        // Always log errors — a DB failure is worth knowing about even if a
        // newer request has already superseded this one.
        if (ex != null) {
          log.error("Unhandled async exception", ex);
        }
        // Drop the result if a newer invocation has already claimed the slot.
        // The guarded future is intentionally left incomplete; any chained
        // .thenAccept() / .thenApply() will simply never fire.
        if (generation.get() != stamp) {
          return;
        }
        if (ex != null) {
          guarded.completeExceptionally(ex);
        } else {
          guarded.complete(result);
        }
      });
      return guarded;
    }

    /**
     * Submits {@code task} on a virtual thread and returns a guarded future.
     * Behaves identically to {@link #supply} but for fire-and-forget tasks
     * that produce no value.
     *
     * @param task the blocking runnable to run off the FX thread
     * @return a future that completes only when this invocation is still current
     */
    public CompletableFuture<Void> run(Runnable task) {
      long stamp = generation.incrementAndGet();
      CompletableFuture<Void> inner = CompletableFuture.runAsync(task, VIRTUAL);
      CompletableFuture<Void> guarded = new CompletableFuture<>();
      inner.whenComplete((result, ex) -> {
        if (ex != null) {
          log.error("Unhandled async exception", ex);
        }
        if (generation.get() != stamp) {
          return;
        }
        if (ex != null) {
          guarded.completeExceptionally(ex);
        } else {
          guarded.complete(null);
        }
      });
      return guarded;
    }
  }

}
