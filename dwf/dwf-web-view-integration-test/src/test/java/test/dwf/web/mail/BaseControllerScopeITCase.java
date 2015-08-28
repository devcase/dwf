package test.dwf.web.mail;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;

import dwf.web.controller.BaseController;
import test.dwf.web.DwfWebRestTestApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {DwfWebRestTestApplication.class, BaseControllerScopeITCase.class})
@WebIntegrationTest()
@Configuration
@ComponentScan(basePackageClasses=test.dwf.web.mail.BaseControllerScopeITCase.class)
public class BaseControllerScopeITCase {
	
	@Controller("exampleController")
	@RequestMapping("/example")
	public static class ExampleController extends BaseController {
		
	}
	
	@Autowired
	private ConfigurableListableBeanFactory wac;
	
	@Test
	public void testExampleControllerScope() throws Exception {
		BeanDefinition bd = wac.getBeanDefinition("exampleController");
		Assert.assertEquals(WebApplicationContext.SCOPE_REQUEST, bd.getScope());
	}
}
