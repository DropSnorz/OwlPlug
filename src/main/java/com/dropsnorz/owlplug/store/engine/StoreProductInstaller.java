package com.dropsnorz.owlplug.store.engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dropsnorz.owlplug.ApplicationDefaults;
import com.dropsnorz.owlplug.core.utils.FileUtils;
import com.dropsnorz.owlplug.store.model.StoreProduct;


@Component
public class StoreProductInstaller {

	@Autowired
	private ApplicationDefaults applicationDefaults;

	public StoreProductInstaller() {

	}

	public void install(StoreProduct product, File targetDirectory) {

		try {
			File tempFile = downloadInTempDirectory(product);
			File tempFolder = new File(applicationDefaults.getUserDataDirectory() + "/" + "temp-" + tempFile.getName().replace(".owlpack", ""));
			FileUtils.unzip(tempFile.getAbsolutePath(),  tempFolder.getAbsolutePath());

			installToPluginDirectory(tempFolder, targetDirectory);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	private File downloadInTempDirectory(StoreProduct product) {

		try {
			URL website = new URL(product.getDownloadUrl());

			SimpleDateFormat horodateFormat = new SimpleDateFormat("ddMMyyhhmmssSSS");

			String outPutFileName =  horodateFormat.format(new Date()) + ".owlpack";
			String outputFilePath = outPutFileName;
			File outputFile = new File(outputFilePath);
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream(outputFile);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

			return outputFile;

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}


	private void installToPluginDirectory(File source, File target) throws IOException {

		FileUtils.copyDirectory(source, target);

	}

	private File getSubfileByName(File parent, String filename) {
		for(File f : parent.listFiles()) {
			if(f.getName().equals(filename)) {
				return f;
			}
		}

		return null;
	}
}
