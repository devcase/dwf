package dwf.config;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.HandlesTypes;
import javax.sql.DataSource;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.orm.hibernate4.support.OpenSessionInViewFilter;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import dwf.web.DefaultLocaleResolver;
import dwf.web.filter.AppPathFilter;
import dwf.web.filter.SetUtf8EncodingFilter;
import dwf.web.filter.SetupLocaleFilter;

/**
 * Configura o framework
 * 
 * @author Hirata
 * 
 */
@HandlesTypes(DwfConfig.class)
public class DwfInitializer implements ServletContainerInitializer {
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
		filterReg.addMappingForServletNames(EnumSet.allOf(DispatcherType.class), false, dwfConfig.getApplicationName());
		filterReg.setAsyncSupported(true);
		filterReg = servletContext.addFilter("appPathFilter", AppPathFilter.class);
		filterReg.addMappingForServletNames(EnumSet.allOf(DispatcherType.class), false, dwfConfig.getApplicationName());
		filterReg.setAsyncSupported(true);
		filterReg = servletContext.addFilter("setLocaleFilter", SetupLocaleFilter.class);
		filterReg.addMappingForServletNames(EnumSet.allOf(DispatcherType.class), false, dwfConfig.getApplicationName());
		filterReg.setAsyncSupported(true);
		//Filtro que abre a sessão do hibernate a cada request
		filterReg = servletContext.addFilter("openSessionInViewFilter", OpenSessionInViewFilter.class);
		filterReg.addMappingForServletNames(EnumSet.allOf(DispatcherType.class), false, dwfConfig.getApplicationName());
		filterReg.setAsyncSupported(true);
	}

	/**
	 * 
	 * @param servletContext
	 * @param dwfConfig
	 * @return
	 */
	private XmlWebApplicationContext createWebApplicationContext(ServletContext servletContext, final DwfConfig dwfConfig) {
		final DwfInitializer dwfInitializer = this;
		

		// configure the webapplicationcontext
		XmlWebApplicationContext wac = new XmlWebApplicationContext() {

			@Override
			protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
				super.loadBeanDefinitions(beanFactory);

				beanFactory.registerSingleton("dwfInitializer", dwfInitializer);
				beanFactory.registerSingleton("dwfConfig", dwfConfig);
				
				//Datasource: search into JNDI
				DataSource dataSource;
				try {
					InitialContext initialContext = new InitialContext();
					try {
						//Search for DataSource
						dataSource = (DataSource) initialContext.lookup(dwfConfig.getDataSourceJNDIName());
					} catch (NamingException ex) {
						throw new FatalBeanException("Could not find the datasource named " + dwfConfig.getDataSourceJNDIName(), ex);
					}
				} catch (NamingException ex) {
					throw new FatalBeanException("Could not create the InitialContext", ex);
				}
				beanFactory.registerSingleton("dataSource", dataSource);
				
				//SessionFactory setup
				LocalSessionFactoryBean localSessionFactory = new LocalSessionFactoryBean();
				localSessionFactory.setDataSource(dataSource);
				localSessionFactory.setPackagesToScan("dwf.user.domain", "dwf.activitylog.domain", dwfConfig.getEntityPackage());
				localSessionFactory.afterPropertiesSet();
				beanFactory.registerSingleton("sessionFactory", localSessionFactory);
				
				beanFactory.registerSingleton("entityPackage", dwfConfig.getEntityPackage());
				
				//Scan for components!
				ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(beanFactory);
				scanner.setEnvironment(getEnvironment());
				//dwf default components
				scanner.scan(new String[] {"dwf.persistence", "dwf.activitylog.service", "dwf.utils", "dwf.web"});
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
