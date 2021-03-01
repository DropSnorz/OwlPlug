package com.owlplug.core.components;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WebServerContext {
	
	private int port;
	@Value("${server.host:@null}")
	private String host;
	@Value("${server.contextPath:/}")
	private String contextPath;
	
	private boolean serverStarted;
	
	public int getPort() {
		return port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}
	
	public String getHost() {
		return host;
	}
	
	public void setHost(String host) {
		this.host = host;
	}
	
	public String getContextPath() {
		return contextPath;
	}
	
	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}

	public boolean isServerStarted() {
		return serverStarted;
	}

	public void setServerStarted(boolean serverStarted) {
		this.serverStarted = serverStarted;
	}
	
	public String getUrl() {
		return "http://" + this.host + ":" + this.port + this.contextPath;
	}
	
}
