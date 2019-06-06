/* OwlPlug
 * Copyright (C) 2019 Arthur <dropsnorz@gmail.com>
 *
 * This file is part of OwlPlug.
 *
 * OwlPlug is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OwlPlug is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OwlPlug.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package com.owlplug;

import com.owlplug.core.components.ApplicationDefaults;
import java.io.File;
import java.util.prefs.Preferences;

import org.junit.Before;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.boot.test.mock.mockito.MockBean;

public class AppTestContext {

  @MockBean
  protected Preferences preferences;

  @Before
  public void setUp() {

    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource("test-data").getFile());
    String vstDirectoryTestPath = file.getAbsolutePath();


    Mockito.when(preferences.getBoolean(Mockito.any(String.class), Mockito.any(Boolean.class)))
        .thenAnswer(new Answer() {
          public Object answer(InvocationOnMock invocation) {
            Object[] args = invocation.getArguments();
            Object mock = invocation.getMock();
    
            if (((String) args[0]).equals(ApplicationDefaults.VST2_DISCOVERY_ENABLED_KEY)) {
              return true;
            }
            if (((String) args[0]).equals(ApplicationDefaults.VST3_DISCOVERY_ENABLED_KEY)) {
              return true;
            }
            if (((String) args[0]).equals(ApplicationDefaults.SYNC_PLUGINS_STARTUP_KEY)) {
              return false;
            }
            return false;
          }
        });

    Mockito.when(preferences.get(Mockito.any(String.class), Mockito.any(String.class)))
        .thenAnswer(new Answer() {
          public Object answer(InvocationOnMock invocation) {
            Object[] args = invocation.getArguments();
            Object mock = invocation.getMock();
    
            if (((String) args[0]).equals(ApplicationDefaults.VST_DIRECTORY_KEY)) {
              return vstDirectoryTestPath;
            }
    
            return null;
          }
        });
  }

}
