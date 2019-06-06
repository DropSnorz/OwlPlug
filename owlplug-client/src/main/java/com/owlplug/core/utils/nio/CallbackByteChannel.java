/* OwlPlug
 * Copyright (C) 2019 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OwlPlug is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OwlPlug.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package com.owlplug.core.utils.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public class CallbackByteChannel implements ReadableByteChannel {
  private ProgressCallback callback;
  private long size;
  private ReadableByteChannel rbc;
  private long sizeRead;

  public CallbackByteChannel(ReadableByteChannel rbc, long expectedSize) {
    this.size = expectedSize;
    this.rbc = rbc;
  }

  public void close() throws IOException {
    rbc.close();
  }

  public long getReadSoFar() {
    return sizeRead;
  }

  public boolean isOpen() {
    return rbc.isOpen();
  }

  public int read(ByteBuffer bb) throws IOException {
    int n;
    double progress;
    if ((n = rbc.read(bb)) > 0) {
      sizeRead += n;
      progress = size > 0 ? (double) sizeRead / (double) size * 100.0 : -1.0;
      callback.onProgress(progress);
    }
    return n;
  }

  public ProgressCallback getCallback() {
    return callback;
  }

  public void setCallback(ProgressCallback callback) {
    this.callback = callback;
  }

}