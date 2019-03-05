package com.owlplug;

import static org.junit.Assert.assertEquals;

import com.owlplug.core.utils.ComparableVersion;
import org.junit.Test;

public class ComparableVersionTest {
  
  @Test
  public void equalsVersionTest() {
    
    ComparableVersion ver1 = new ComparableVersion("1.0.0");
    ComparableVersion ver2 = new ComparableVersion("1.0.0");
    
    assertEquals(ver1, ver2);
    assertEquals(0, ver1.compareTo(ver2));
    assertEquals(0, ver2.compareTo(ver1));

  }
  
  @Test
  public void differentVersionsTest() {
    
    ComparableVersion ver1 = new ComparableVersion("2.4.1");
    ComparableVersion ver2 = new ComparableVersion("1.0.2");
    
    assertEquals(1, ver1.compareTo(ver2));
    assertEquals(-1, ver2.compareTo(ver1));
    
  }
  
  @Test
  public void equalsCanonicalTest() {
    
    ComparableVersion ver1 = new ComparableVersion("1.0.0-SNAPSHOT");
    ComparableVersion ver2 = new ComparableVersion("1.0.0-sNapshoT");
    
    assertEquals(ver1, ver2);
    assertEquals(0, ver1.compareTo(ver2));
    assertEquals(0, ver2.compareTo(ver1));

  }
  
  @Test
  public void differentVersionSameQualifierTest() {
    
    ComparableVersion ver1 = new ComparableVersion("1.0.2-SNAPSHOT");
    ComparableVersion ver2 = new ComparableVersion("1.0.3-SNAPSHOT");
    
    assertEquals(-1, ver1.compareTo(ver2));
    assertEquals(1, ver2.compareTo(ver1));

  }
  
  @Test
  public void taggedVersionTest() {
    
    ComparableVersion ver1 = new ComparableVersion("1.0.0-SNAPSHOT");
    ComparableVersion ver2 = new ComparableVersion("1.0.0");
    
    assertEquals(-1, ver1.compareTo(ver2));
    assertEquals(1, ver2.compareTo(ver1));

  }
  

}
