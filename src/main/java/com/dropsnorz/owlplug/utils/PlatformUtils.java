package com.dropsnorz.owlplug.utils;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

public class PlatformUtils {
	
	public static void openDirectoryExplorer(String path){
		try {
			Desktop.getDesktop().open(new File(path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void openDirectoryExplorer(File file){
		try {
			Desktop.getDesktop().open(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
