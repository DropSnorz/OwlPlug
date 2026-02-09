/* OwlPlug
 * Copyright (C) 2021 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3
 * as published by the Free Software Foundation.
 *
 * OwlPlug is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OwlPlug.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.owlplug.core.controllers.fragments;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.owlplug.core.controllers.fragments.PluginPathFragmentController.DirectoryCheck;
import com.owlplug.core.controllers.fragments.PluginPathFragmentController.DirectoryChecks;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class PluginPathFragmentControllerTest {

  @Test
  public void testDirectoryCheckConstructor() {
    DirectoryCheck check = new DirectoryCheck(true, "Test message");
    assertTrue(check.getStatus());
    assertEquals("Test message", check.getMessage());
  }

  @Test
  public void testDirectoryCheckWithFalseStatus() {
    DirectoryCheck check = new DirectoryCheck(false, "Error message");
    assertFalse(check.getStatus());
    assertEquals("Error message", check.getMessage());
  }

  @Test
  public void testDirectoryChecksGettersAndSetters() {
    DirectoryChecks checks = new DirectoryChecks();
    DirectoryCheck existsCheck = new DirectoryCheck(true, "Exists");
    DirectoryCheck readCheck = new DirectoryCheck(true, "Readable");
    DirectoryCheck writeCheck = new DirectoryCheck(false, "Not writable");

    checks.setExists(existsCheck);
    checks.setCanRead(readCheck);
    checks.setCanWrite(writeCheck);

    assertEquals(existsCheck, checks.getExists());
    assertEquals(readCheck, checks.getCanRead());
    assertEquals(writeCheck, checks.getCanWrite());
  }

  @Test
  public void testCheckDirectoryWithNullPath() throws Exception {
    Method checkDirectoryMethod = getCheckDirectoryMethod();

    DirectoryChecks checks = (DirectoryChecks) checkDirectoryMethod.invoke(null, (String) null);

    assertNotNull(checks);
    assertFalse(checks.getExists().getStatus());
    assertFalse(checks.getCanRead().getStatus());
    assertFalse(checks.getCanWrite().getStatus());
    assertTrue(checks.getExists().getMessage().contains("blank"));
  }

  @Test
  public void testCheckDirectoryWithBlankPath() throws Exception {
    Method checkDirectoryMethod = getCheckDirectoryMethod();

    DirectoryChecks checks = (DirectoryChecks) checkDirectoryMethod.invoke(null, "   ");

    assertNotNull(checks);
    assertFalse(checks.getExists().getStatus());
    assertFalse(checks.getCanRead().getStatus());
    assertFalse(checks.getCanWrite().getStatus());
    assertTrue(checks.getExists().getMessage().contains("blank"));
  }

  @Test
  public void testCheckDirectoryWithEmptyPath() throws Exception {
    Method checkDirectoryMethod = getCheckDirectoryMethod();

    DirectoryChecks checks = (DirectoryChecks) checkDirectoryMethod.invoke(null, "");

    assertNotNull(checks);
    assertFalse(checks.getExists().getStatus());
    assertFalse(checks.getCanRead().getStatus());
    assertFalse(checks.getCanWrite().getStatus());
  }

  @Test
  public void testCheckDirectoryWithNonExistentPath() throws Exception {
    Method checkDirectoryMethod = getCheckDirectoryMethod();

    DirectoryChecks checks = (DirectoryChecks) checkDirectoryMethod.invoke(null, "/nonexistent/path/to/directory");

    assertNotNull(checks);
    assertFalse(checks.getExists().getStatus());
    assertFalse(checks.getCanRead().getStatus());
    assertFalse(checks.getCanWrite().getStatus());
    assertTrue(checks.getExists().getMessage().contains("does not exist"));
    assertTrue(checks.getCanRead().getMessage().contains("does not exist"));
    assertTrue(checks.getCanWrite().getMessage().contains("does not exist"));
  }

  @Test
  public void testCheckDirectoryWithExistingReadableDirectory(@TempDir Path tempDir) throws Exception {
    Method checkDirectoryMethod = getCheckDirectoryMethod();

    DirectoryChecks checks = (DirectoryChecks) checkDirectoryMethod.invoke(null, tempDir.toString());

    assertNotNull(checks);
    assertTrue(checks.getExists().getStatus());
    assertTrue(checks.getCanRead().getStatus());
    assertTrue(checks.getExists().getMessage().contains("exists"));
    assertTrue(checks.getCanRead().getMessage().contains("readable"));
  }

  @Test
  public void testCheckDirectoryWithExistingWritableDirectory(@TempDir Path tempDir) throws Exception {
    Method checkDirectoryMethod = getCheckDirectoryMethod();

    // Ensure directory is writable
    File dir = tempDir.toFile();
    dir.setWritable(true);

    DirectoryChecks checks = (DirectoryChecks) checkDirectoryMethod.invoke(null, tempDir.toString());

    assertNotNull(checks);
    assertTrue(checks.getExists().getStatus());
    assertTrue(checks.getCanWrite().getStatus());
    assertTrue(checks.getCanWrite().getMessage().contains("writable"));
  }

  @Test
  public void testCheckDirectoryWithFileInsteadOfDirectory(@TempDir Path tempDir) throws Exception {
    Method checkDirectoryMethod = getCheckDirectoryMethod();

    // Create a file instead of directory
    Path filePath = tempDir.resolve("testfile.txt");
    Files.createFile(filePath);

    DirectoryChecks checks = (DirectoryChecks) checkDirectoryMethod.invoke(null, filePath.toString());

    assertNotNull(checks);
    assertFalse(checks.getExists().getStatus());
    assertTrue(checks.getExists().getMessage().contains("does not exist"));
  }

  @Test
  public void testCheckDirectoryWithReadOnlyDirectory(@TempDir Path tempDir) throws Exception {
    Method checkDirectoryMethod = getCheckDirectoryMethod();

    File dir = tempDir.toFile();
    dir.setWritable(false);

    DirectoryChecks checks = (DirectoryChecks) checkDirectoryMethod.invoke(null, tempDir.toString());

    assertNotNull(checks);
    assertTrue(checks.getExists().getStatus());
    // Note: Read-only check behavior may vary by OS and file system
    assertNotNull(checks.getCanWrite());

    // Restore write permission for cleanup
    dir.setWritable(true);
  }

  @Test
  public void testCheckDirectoryConsistency(@TempDir Path tempDir) throws Exception {
    Method checkDirectoryMethod = getCheckDirectoryMethod();

    // Check the same directory twice
    DirectoryChecks checks1 = (DirectoryChecks) checkDirectoryMethod.invoke(null, tempDir.toString());
    DirectoryChecks checks2 = (DirectoryChecks) checkDirectoryMethod.invoke(null, tempDir.toString());

    assertEquals(checks1.getExists().getStatus(), checks2.getExists().getStatus());
    assertEquals(checks1.getCanRead().getStatus(), checks2.getCanRead().getStatus());
    assertEquals(checks1.getCanWrite().getStatus(), checks2.getCanWrite().getStatus());
  }

  @Test
  public void testCheckDirectoryMessagesAreInformative() throws Exception {
    Method checkDirectoryMethod = getCheckDirectoryMethod();

    DirectoryChecks checks = (DirectoryChecks) checkDirectoryMethod.invoke(null, "/invalid/path");

    // Messages should be helpful and descriptive
    assertNotNull(checks.getExists().getMessage());
    assertNotNull(checks.getCanRead().getMessage());
    assertNotNull(checks.getCanWrite().getMessage());

    assertFalse(checks.getExists().getMessage().isEmpty());
    assertFalse(checks.getCanRead().getMessage().isEmpty());
    assertFalse(checks.getCanWrite().getMessage().isEmpty());
  }

  /**
   * Helper method to access the private checkDirectory method via reflection.
   */
  private Method getCheckDirectoryMethod() throws Exception {
    // Create a minimal instance for testing the checkDirectory method
    // We need to use reflection since checkDirectory is a private instance method
    Class<?> innerClass = PluginPathFragmentController.class;
    Method method = innerClass.getDeclaredMethod("checkDirectory", String.class);
    method.setAccessible(true);

    // Create a minimal mock instance - note this won't fully initialize the controller
    // but is sufficient for testing the checkDirectory logic
    PluginPathFragmentController controller = new TestablePluginPathFragmentController();

    // Return a wrapper that invokes on the test instance
    return new Method() {
      @Override
      public Object invoke(Object obj, Object... args) throws Exception {
        return method.invoke(controller, args);
      }

      @Override public String getName() { return method.getName(); }
      @Override public Class<?> getDeclaringClass() { return method.getDeclaringClass(); }
      @Override public Class<?> getReturnType() { return method.getReturnType(); }
      @Override public Class<?>[] getParameterTypes() { return method.getParameterTypes(); }
      @Override public int getModifiers() { return method.getModifiers(); }
      @Override public java.lang.annotation.Annotation[] getAnnotations() { return method.getAnnotations(); }
      @Override public java.lang.annotation.Annotation[] getDeclaredAnnotations() { return method.getDeclaredAnnotations(); }
      @Override public <T extends java.lang.annotation.Annotation> T getAnnotation(Class<T> annotationClass) { return method.getAnnotation(annotationClass); }
    };
  }

  /**
   * Minimal testable subclass that doesn't require FXML initialization.
   */
  private static class TestablePluginPathFragmentController extends PluginPathFragmentController {
    public TestablePluginPathFragmentController() {
      super("Test", "TEST_KEY", "DIR_KEY", "EXTRA_KEY", null, null, null);
    }

    @Override
    public void init() {
      // Skip FXML initialization for testing
    }
  }
}