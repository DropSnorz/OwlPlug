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
import com.dropsnorz.owlplug.AppTestContext;
import com.dropsnorz.owlplug.ApplicationDefaults;
import com.dropsnorz.owlplug.core.model.Plugin;
import com.dropsnorz.owlplug.core.services.PluginService;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class PluginServiceTest extends AppTestContext {

	@Autowired
	private PluginService pluginService;

	
	@TestConfiguration
    static class EmployeeServiceImplTestContextConfiguration {
  
        @Bean
        public PluginService employeeService() {
            return new PluginService();
        }
    }

	
	@Test
	public void testPluginExploring(){

		List<Plugin> plugins = pluginService.explore();
		
		assertNotNull(plugins);
		assertEquals(1, plugins.size());
		assertEquals("test", plugins.get(0).getName());
	}

}
