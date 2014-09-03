package dwf.config;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Properties;
import java.util.Set;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.HandlesTypes;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.orm.hibernate4.support.OpenSessionInViewFilter;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import dwf.persistence.utils.DwfNamingStrategy;
import dwf.web.DefaultLocaleResolver;
import dwf.web.filter.AppPathFilter;
import dwf.web.filter.SetUtf8EncodingFilter;
import dwf.web.filter.SetupLocaleFilter;
import dwf.web.filter.TimestampFilter;

/**
 * Configura o framework
 * 
 * @author Hirata
 * 
 */
@HandlesTypes(DwfConfig.class)
public class DwfInitializer implements ServletContainerInitializer {
	private Log log = LogFactory.getLog(getClass());
	
	protected DispatcherServlet dispatcherServlet;
	protected XmlWebApplicationContext webApplicationContext;
	protected ServletContext servletContext;
	protected DwfConfig dwfConfig;

	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onStartup(Set<Class<?>> configImplementations, ServletContext servletContext) throws ServletException {
		log.info("DWF Initialization - start");
		try {
			this.servletContext = servletContext;
			
			//Searching for DwfConfig
			if (configImplementations == null || configImplementations.isEmpty()) {
				throw new ServletException("No DwfConfig implementation found");
			}
			Class<DwfConfig> _dwfConfigImplementation = null;
			for (Class<?> clazz : configImplementations) {
				if(DwfConfig.class.isAssignableFrom(clazz)) {
					_dwfConfigImplementation = (Class<DwfConfig>) clazz;
				}
			}
			if(_dwfConfigImplementation == null) {
				throw new ServletException("No DwfConfig implementation found");
			}
			this.dwfConfig = (DwfConfig) BeanUtils.instantiate(_dwfConfigImplementation);;
			
			webApplicationContext = createWebApplicationContext(servletContext, dwfConfig);
			
			// configure the spring mvc servlet
			dispatcherServlet = new DispatcherServlet(webApplicationContext);
			ServletRegistration.Dynamic servletReg = servletContext.addServlet(dwfConfig.getApplicationName(), dispatcherServlet);
			servletReg.setAsyncSupported(true);
			servletReg.setLoadOnStartup(1);
			servletReg.addMapping("/");
			
			//filters não anotados
			FilterRegistration.Dynamic filterReg = servletContext.addFilter("setUtf8EncodingFilter", SetUtf8EncodingFilter.class);
			filterReg.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");
			filterReg.setAsyncSupported(true);
			filterReg = servletContext.addFilter("timestampFilter", TimestampFilter.class);
			filterReg.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");
			filterReg.setAsyncSupported(true);
			filterReg = servletContext.addFilter("appPathFilter", AppPathFilter.class);
			filterReg.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");
			filterReg.setAsyncSupported(true);
			filterReg = servletContext.addFilter("setupLocaleFilter", SetupLocaleFilter.class);
			filterReg.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");
			filterReg.setAsyncSupported(true);
			//Filtro que abre a sessão do hibernate a cada request
			filterReg = servletContext.addFilter("openSessionInViewFilter", OpenSessionInViewFilter.class);
			filterReg.addMappingForServletNames(EnumSet.allOf(DispatcherType.class), false, dwfConfig.getApplicationName());
			filterReg.setAsyncSupported(true);
			//filtro de segurança
			filterReg = servletContext.addFilter("springSecurityFilterChain", org.springframework.web.filter.DelegatingFilterProxy.class);
			filterReg.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), false, "/*");
			filterReg.setAsyncSupported(true);
		} catch(Exception ex) {
			ex.printStackTrace();
			log.fatal("DWF Initialization Error", ex);
		} finally {
			log.info("DWF Initialization - finished");
		}
			
	}

	/**
	 * 
	 * @param servletContext
	 * @param dwfConfig
	 * @return
	 */
	private XmlWebApplicationContext createWebApplicationContext(ServletContext servletContext, final DwfConfig dwfConfig) {
		final DwfInitializer dwfInitializer = this;
		servletContext.setAttribute("dwfInitializer", dwfInitializer);

		// configure the webapplicationcontext
		XmlWebApplicationContext wac = new XmlWebApplicationContext() {

			@Override
			protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
				super.loadBeanDefinitions(beanFactory);

				beanFactory.registerSingleton("dwfInitializer", dwfInitializer);
				beanFactory.registerSingleton("dwfConfig", dwfConfig);
				
				//Datasource: search into JNDI
				DataSource dataSource = dwfConfig.getDataSource();
				beanFactory.registerSingleton("dataSource", dataSource);
				
				//SessionFactory setup
				GenericBeanDefinition localSessionFactoryDefinition =  new GenericBeanDefinition();
				localSessionFactoryDefinition.setBeanClass(LocalSessionFactoryBean.class);
				localSessionFactoryDefinition.setPropertyValues(new MutablePropertyValues());
				localSessionFactoryDefinition.getPropertyValues().add("packagesToScan", new String [] {"dwf.user.domain", "dwf.activitylog.domain", dwfConfig.getEntityPackage()});
				localSessionFactoryDefinition.getPropertyValues().add("namingStrategy", new DwfNamingStrategy(dwfConfig));
				localSessionFactoryDefinition.getPropertyValues().add("dataSource", dataSource);
				localSessionFactoryDefinition.setScope(SCOPE_APPLICATION);
				Properties hibernateProperties = new Properties();
				hibernateProperties = dwfConfig.changeHibernateProperties(hibernateProperties);
				localSessionFactoryDefinition.getPropertyValues().add("hibernateProperties", hibernateProperties);
				beanFactory.registerBeanDefinition("sessionFactory", localSessionFactoryDefinition);
				
				//Scan for components!
				ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(beanFactory);
				scanner.setEnvironment(getEnvironment());
				//dwf default components
				scanner.scan(new String[] {"dwf.persistence", "dwf.activitylog.service", "dwf.utils", "dwf.web", "dwf.security", "dwf.validation"});
				//application components
				scanner.scan(dwfConfig.getApplicationComponentPackages());
				
				//Default Locale Resolver, if application did not define a custom one
				if(!beanFactory.containsBean("localeResolver")) {
					beanFactory.registerSingleton("localeResolver", new DefaultLocaleResolver());
				}
			}
		};
		//basic configuration from xml
		wac.setConfigLocations(new String[] { "classpath:dwf/config/dwf-applicationContext.xml" });
		wac.setServletContext(servletContext);
		wac.refresh();
		servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
		return wac;
	}
	
	/**
	 * Closes the current Spring Container and starts a new one.
	 */
	public void resetSpringContainer() {
		
		webApplicationContext.refresh();
		
		try {
			dispatcherServlet.init();
		} catch (ServletException e) {
			throw new RuntimeException(e);
		}
	}

}
