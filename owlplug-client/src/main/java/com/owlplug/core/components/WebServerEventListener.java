package com.owlplug.core.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class WebServerEventListener implements ApplicationListener<WebServerInitializedEvent> {
	
	@Autowired
	private WebServerContext webServerContext;

	@Override
	public void onApplicationEvent(WebServerInitializedEvent event) {
		webServerContext.setPort(event.getWebServer().getPort());
		webServerContext.setServerStarted(true);
		
		
	}

}
