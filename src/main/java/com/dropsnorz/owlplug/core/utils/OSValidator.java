package com.dropsnorz.owlplug.core.utils;

public class OSValidator {
	
	private OSValidator() {}

	private static String OS = System.getProperty("os.name").toLowerCase();

	/**
	 * Returns true if current operating system is windows.
	 * @return true if Windows, false otherwise
	 */
	public static boolean isWindows() {

		return (OS.indexOf("win") >= 0);

	}

	/**
	 * Returns true if current operating system is MacOs.
	 * @return true if MacOs, false otherwise
	 */
	public static boolean isMac() {

		return (OS.indexOf("mac") >= 0);

	}

	/**
	 * Returns true if current operating system is Unix.
	 * @return true if Unix, false otherwise
	 */
	public static boolean isUnix() {

		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);

	}

	/**
	 * Returns true if current operating system is solaris.
	 * @return true if Salaris, false otherwise
	 */
	public static boolean isSolaris() {

		return (OS.indexOf("sunos") >= 0);

	}

}
