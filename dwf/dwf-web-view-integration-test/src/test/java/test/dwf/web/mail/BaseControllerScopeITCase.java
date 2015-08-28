package test.dwf.web.mail;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.WebApplicationContext;

import test.dwf.web.application.DwfWebRestTestApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {DwfWebRestTestApplication.class})
@WebIntegrationTest()
public class BaseControllerScopeITCase {
	
	@Autowired
	private ConfigurableListableBeanFactory wac;
	
	@Test
	public void testExampleControllerScope() throws Exception {
		BeanDefinition bd = wac.getBeanDefinition("exampleController");
		Assert.assertEquals(WebApplicationContext.SCOPE_REQUEST, bd.getScope());
	}
}
