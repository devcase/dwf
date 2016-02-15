package dwf.web.rest.autoconfigure;

import javax.servlet.ServletContainerInitializer;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Catalina;
import org.apache.tomcat.util.scan.StandardJarScanner;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;

/**
 * Customizations that will improve the startup time
 * @author Hirata
 *
 */
@Configuration
@ConditionalOnWebApplication
@AutoConfigureBefore(EmbeddedServletContainerAutoConfiguration.class)
@ConditionalOnClass(Catalina.class)
@ConditionalOnProperty(prefix = "dwf.generated.jsp.configuration", name = "enabled", matchIfMissing = false) class DwfTomcatEmbeddedServletContainerFactoryConfiguration {
	@Bean
	public TomcatEmbeddedServletContainerFactory tomcatEmbeddedServletContainerFactory() {
		TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory() {

			@Override
			protected void configureContext(Context context, ServletContextInitializer[] initializers) {
				super.configureContext(context, initializers);
				
				//disables jar scanning - tld are already compiled
				context.getJarScanner();
				StandardJarScanner standardJarScanner = (StandardJarScanner) context.getJarScanner();
				standardJarScanner.setScanAllDirectories(false);
				standardJarScanner.setScanClassPath(false);

				try {
					//needed for compiled jsp (still don't know exactly why)
					ServletContainerInitializer initializer = (ServletContainerInitializer) ClassUtils
							.forName("org.apache.jasper.servlet.JasperInitializer", null)
							.newInstance();
					context.addServletContainerInitializer(initializer, null);
				} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | LinkageError e) {
				}
			}

			@Override
			protected void customizeConnector(Connector connector) {
				//binds the port on initialization, tricking heroku
				super.customizeConnector(connector);
				connector.setProperty("bindOnInit", "true");
				try {
					connector.init();
				} catch (LifecycleException e) {
					throw new RuntimeException (e);
				}
			}
		};
		
		//don't register JspServlet - uses compiled jsp
		factory.setRegisterJspServlet(false);
		return factory;
	}

}