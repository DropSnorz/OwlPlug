package com.owlplug.core.components;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("workspaceDirectoryInitializer")
public class WorkspaceDirectoryInitializer {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	ApplicationDefaults applicationDefaults;


	@PostConstruct
	public void cleanup() {
	
		File workingDirectory = new File(ApplicationDefaults.getUserDataDirectory());
		
		if(!workingDirectory.exists()) {
			workingDirectory.mkdirs();
		}
		
		File versionFile = new File(workingDirectory, ".version");
		if(versionFile.exists()) {
			
			try {
				String currentVersion = applicationDefaults.getVersion();
				String workspaceVersion = FileUtils.readFileToString(versionFile);
				
				File dbFile = new File(workingDirectory, "owlplug.mv.db");
				/*
				 * TODO: Database file should removed only if workspace version
				 * is not compatible with currentVersion
				 */
				dbFile.delete();

			} catch (IOException e) {
				log.error("Workspace version can't be retrieved from file", e);
			}
						
			versionFile.delete();
		}
		
		try {
			String currentVersion = applicationDefaults.getVersion();
			versionFile.createNewFile();
			FileUtils.writeStringToFile(versionFile, currentVersion);
		} catch (IOException e) {
			log.error("Version file can't be created in workspace directory", e);
		}
						
	}

}
