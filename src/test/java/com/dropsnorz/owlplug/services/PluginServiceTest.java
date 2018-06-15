package com.dropsnorz.owlplug.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.List;
import java.util.prefs.Preferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.dropsnorz.owlplug.App;
import com.dropsnorz.owlplug.ApplicationDefaults;
import com.dropsnorz.owlplug.core.model.Plugin;
import com.dropsnorz.owlplug.core.services.PluginService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class PluginServiceTest {

	@Autowired
	private PluginService pluginService;

	@MockBean
	private Preferences preferences;
	
	
	@TestConfiguration
    static class EmployeeServiceImplTestContextConfiguration {
  
        @Bean
        public PluginService employeeService() {
            return new PluginService();
        }
    }

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

				if(((String) args[0]).equals(ApplicationDefaults.VST2_DISCOVERY_ENABLED_KEY)) {
					return true;
				}
				if(((String) args[0]).equals(ApplicationDefaults.VST3_DISCOVERY_ENABLED_KEY)) {
					return true;
				}
				if(((String) args[0]).equals(ApplicationDefaults.SYNC_PLUGINS_STARTUP_KEY)) {
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

				if(((String) args[0]).equals(ApplicationDefaults.VST_DIRECTORY_KEY)) {
					return vstDirectoryTestPath;
				}
				
				return null;
			}
		});
	}


	@Test
	public void test(){

		List<Plugin> plugins = pluginService.explore();
		
		assertNotNull(plugins);
		assertEquals(1, plugins.size());
		assertEquals("test", plugins.get(0).getName());
	}

}
