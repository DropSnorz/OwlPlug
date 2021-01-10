package com.owlplug.core.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FileUtilsTest {
	
	@Test
	public void testSanitizeFileNameWithRegularsChars() {
		String sanitizedFileName = FileUtils.sanitizeFileName("File-name0_.test");
		assertEquals("File-name0_.test", sanitizedFileName);
	}
	
	@Test
	public void testSanitizeFileNameWithExtraSpaces() {
		String sanitizedFileName = FileUtils.sanitizeFileName(" file   name ");
		assertEquals("file name", sanitizedFileName);
	}
	
	@Test
	public void testSanitizeFileNameWithWhitespacesChars() {
		String sanitizedFileName = FileUtils.sanitizeFileName("\tfile\n\nname");
		assertEquals("filename", sanitizedFileName);
	}
	
	@Test
	public void testSanitizeFileNameWithIllegalChars() {
		String sanitizedFileName = FileUtils.sanitizeFileName("fi/len%am[e]");
		assertEquals("filename", sanitizedFileName);
	}

}
