package com.owlplug.core.controllers.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.owlplug.core.model.Plugin;
import com.owlplug.core.services.PluginService;

@RestController
public class PluginRestController {
	
	@Autowired
	private PluginService pluginService;
	
	@GetMapping("/plugins")
	public Iterable<Plugin> plugins() {
		return pluginService.findAll();
	}

}
