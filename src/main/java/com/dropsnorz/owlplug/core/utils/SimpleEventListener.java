package com.dropsnorz.owlplug.core.utils;

import java.util.EventListener;

@FunctionalInterface
public interface SimpleEventListener extends EventListener {
	void onAction();

}
