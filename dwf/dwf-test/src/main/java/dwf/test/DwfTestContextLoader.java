package dwf.test;

import java.util.Set;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.reflections.Reflections;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.web.AbstractGenericWebContextLoader;
import org.springframework.test.context.web.WebMergedContextConfiguration;
import org.springframework.web.context.support.GenericWebApplicationContext;

import dwf.config.DwfConfig;
import dwf.config.DwfInitializer;

public class DwfTestContextLoader extends AbstractGenericWebContextLoader {
	private Log log = LogFactory.getLog(getClass());

	@Override
	protected void loadBeanDefinitions(GenericWebApplicationContext context, WebMergedContextConfiguration webMergedConfig) {
		System.out.println("HI!");
		DwfInitializer dwfInitializer = new DwfInitializer();
		
		//busca implementação de DwfConfig
		Reflections reflections = new Reflections();
	    Set<Class<? extends DwfConfig>> configImpls = reflections.getSubTypesOf(DwfConfig.class);
	    Assert.assertFalse("Implementação de DwfConfig não encontrada", configImpls.isEmpty());
	    DwfConfig dwfConfig = null;
	    for (Class<? extends DwfConfig> class1 : configImpls) {
	    	try {
				dwfConfig = class1.newInstance();
				log.info("DwfConfig implementation found: " + class1);
				break;
			} catch (InstantiationException e) {
				log.debug("Error instantiating " + class1, e);
			} catch (IllegalAccessException e) {
				log.debug("Error instantiating " + class1, e);
			}
		}
	    Assert.assertNotNull("Implementação de DwfConfig não encontrada", dwfConfig);

//		System.setProperty("JDBC_DRIVER_CLASS_NAME", "com.mysql.jdbc.Driver");
//		System.setProperty("JDBC_CONNECTION_STRING", "jdbc:mysql://localhost/smservices?user=systemagic&password=systemagic123");
		System.setProperty("JDBC_DRIVER_CLASS_NAME", "org.apache.derby.jdbc.EmbeddedDriver");
		System.setProperty("JDBC_CONNECTION_STRING", "jdbc:derby:dwfTestDB;create=true;user=" + dwfConfig.getDatabaseSchema() );

	    
		DefaultListableBeanFactory beanFactory = context.getDefaultListableBeanFactory();
		
//		context = new GenericWebApplicationContext();
		XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(context);
		ResourceLoader rl = new DefaultResourceLoader();
		context.setResourceLoader(rl);
		// Configure the bean definition reader with this context's
		// resource loading environment.
		beanDefinitionReader.setEnvironment(context.getEnvironment());
		beanDefinitionReader.setResourceLoader(rl);
		beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(context));

		beanDefinitionReader.loadBeanDefinitions("classpath:dwf/config/dwf-applicationContext.xml");
		DwfInitializer.registerDwfBeans(beanFactory, context.getEnvironment(), dwfInitializer, dwfConfig);

	}

	/**
	 * Returns {@code "-context.xml"} in order to support detection of a
	 * default XML config file.
	 */
	@Override
	protected String getResourceSuffix() {
		return "-context.xml";
	}


}
