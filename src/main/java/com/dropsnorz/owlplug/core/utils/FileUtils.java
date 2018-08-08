package com.dropsnorz.owlplug.core.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtils {

	private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

	private FileUtils() {}

	public static String convertPath(String path) {
		return path.replace("\\", "/");
	}

	public static boolean isFilenameValid(String file) {

		return file != null && !file.equals("") && file.matches("[-_.A-Za-z0-9]*");
	}

	public static void unzip(String source, String dest) throws IOException {

		//Open the file
		try (ZipFile file = new ZipFile(source)) {
			FileSystem fileSystem = FileSystems.getDefault();
			//Get file entries
			Enumeration<? extends ZipEntry> entries = file.entries();

			//We will unzip files in this folder
			String uncompressedDirectory = dest;
			Files.createDirectory(fileSystem.getPath(uncompressedDirectory));

			//Iterate over entries
			while (entries.hasMoreElements()) {
				ZipEntry entry = entries.nextElement();
				//If directory then create a new directory in uncompressed folder
				if (entry.isDirectory()) {
					log.debug("Creating Directory:" + uncompressedDirectory + File.separator + entry.getName());
					Files.createDirectories(fileSystem.getPath(uncompressedDirectory + File.separator + entry.getName()));
					
					//Else create the file
				} else {
					InputStream is = file.getInputStream(entry);
					BufferedInputStream bis = new BufferedInputStream(is);
					String uncompressedFileName = uncompressedDirectory + File.separator + entry.getName();

					new File(uncompressedFileName).getParentFile().mkdirs();

					Path uncompressedFilePath = fileSystem.getPath(uncompressedFileName);
					Files.createFile(uncompressedFilePath);

					FileOutputStream fileOutput = new FileOutputStream(uncompressedFileName);

					byte[] buffer = new byte[2048]; 
					int read = 0;
					while ((read = bis.read(buffer)) > 0) {
						fileOutput.write(buffer, 0, read);
					}

					fileOutput.close();
					log.debug("Written :" + entry.getName());
				}
			}
		} catch (IOException e) {
			throw e;
		}
	}


	public static void copyDirectory(File source, File target) throws IOException {
		org.apache.commons.io.FileUtils.copyDirectory(source, target);
	}
	
	public static void deleteDirectory(File source) throws IOException {
		org.apache.commons.io.FileUtils.deleteDirectory(source);
	}
}
