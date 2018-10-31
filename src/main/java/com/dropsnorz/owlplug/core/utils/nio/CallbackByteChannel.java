package com.dropsnorz.owlplug.core.utils.nio;

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
			progress = size > 0 ? (double) sizeRead / (double) size
					* 100.0 : -1.0;
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