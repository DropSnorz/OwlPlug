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

package com.owlplug.controls.transitions;

import java.util.concurrent.atomic.AtomicBoolean;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.layout.Region;

/**
 * Cache a node as image.
 * Based on CacheMemento from JFoenix Library.
 *
 */
public class CacheMemento {
  private boolean cache;
  private boolean cacheShape;
  private boolean snapToPixel;
  private CacheHint cacheHint = CacheHint.DEFAULT;
  private Node node;
  private AtomicBoolean isCached = new AtomicBoolean(false);

  public CacheMemento(Node node) {
    this.node = node;
  }

  /**
   * Cache the node only if it wasn't cached before.
   */
  public void cache() {
    if (!isCached.getAndSet(true)) {
      this.cache = node.isCache();
      this.cacheHint = node.getCacheHint();
      node.setCache(true);
      node.setCacheHint(CacheHint.SPEED);
      if (node instanceof Region) {
        this.cacheShape = ((Region) node).isCacheShape();
        this.snapToPixel = ((Region) node).isSnapToPixel();
        ((Region) node).setCacheShape(true);
        ((Region) node).setSnapToPixel(true);
      }
    }
  }

  public void restore() {
    if (isCached.getAndSet(false)) {
      node.setCache(cache);
      node.setCacheHint(cacheHint);
      if (node instanceof Region) {
        ((Region) node).setCacheShape(cacheShape);
        ((Region) node).setSnapToPixel(snapToPixel);
      }
    }
  }
}