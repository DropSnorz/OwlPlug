package com.dropsnorz.owlplug.core.engine.repositories.googledrive;

import com.dropsnorz.owlplug.ApplicationDefaults;
import com.dropsnorz.owlplug.core.engine.repositories.IRepositoryStrategy;
import com.dropsnorz.owlplug.core.engine.repositories.RepositoryStrategyException;
import com.dropsnorz.owlplug.core.engine.repositories.RepositoryStrategyParameters;
import com.dropsnorz.owlplug.core.model.GoogleDriveRepository;
import com.dropsnorz.owlplug.core.model.PluginRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.FileList;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GoogleDrivePullingStrategy implements IRepositoryStrategy {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	
	@Override
	public void execute(PluginRepository repository, RepositoryStrategyParameters parameters) throws RepositoryStrategyException {

		log.debug("Start pulling reposiroty " + repository.getId());
		
		GoogleDriveRepository googleDriveRepository = (GoogleDriveRepository) repository;
		GoogleCredential credential = (GoogleCredential) parameters.getOject("google-credential");
		Drive drive = new Drive.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance(), credential)
				.setApplicationName(ApplicationDefaults.APPLICATION_NAME).build();

		File targetDir = new File(parameters.get("target-dir"));

		if (!targetDir.exists() && !targetDir.mkdirs()) {
			throw new RepositoryStrategyException("Pulling repository " 
					+ repository.getName() + " - Unable to create parent directory");
		}
		
		String safeFolderId = parameters.get("target-dir").replaceAll("/[^A-Za-z0-9]/", "");
		try {
			downloadFolder(drive, safeFolderId, googleDriveRepository.getRemoteRessourceId());
		} catch (IOException e) {
			throw new RepositoryStrategyException("Repository " + repository.getName()
				+ " - Unable to acces google drive folder. Check you network connectivity or access rights.");
		}

	}
	
	//Recursive call to Google drive API, not really cool but Drive do not support folder/batch downloading.
	private void downloadFolder(Drive drive, String parentPath, String folderId) throws IOException {
		
		log.debug("Exporing Google Drive folderId: " + folderId);
		new File(parentPath).mkdirs();
		
		FileList result = drive.files().list()   
				.setQ("'" + folderId + "' in parents and trashed = false")
				.setSpaces("drive")
				.setFields("nextPageToken, files(id, name, parents, mimeType)")
				.execute();

		List<com.google.api.services.drive.model.File> files = result.getFiles();
		if (files == null || files.isEmpty()) {
			log.debug("No files found in remote folder");
		} else {
			for (com.google.api.services.drive.model.File file : files) {				
				if (file.getMimeType().equals("application/vnd.google-apps.folder")) {
					downloadFolder(drive, parentPath + File.separator + file.getName(), file.getId());
				} else {
					log.debug("Downloading file " + file.getName());
					File outputFile = new File(parentPath + File.separator + file.getName());
					OutputStream out = new FileOutputStream(outputFile);
					Files.Get request = drive.files().get(file.getId());
					request.executeMediaAndDownloadTo(out);
				}
			}
		}
		
	}

}
