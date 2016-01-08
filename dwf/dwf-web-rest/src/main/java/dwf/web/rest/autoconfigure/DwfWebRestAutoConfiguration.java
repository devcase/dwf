package dwf.web.rest.autoconfigure;

import javax.servlet.ServletContainerInitializer;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.scan.StandardJarScanner;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate4.support.OpenSessionInViewInterceptor;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import dwf.web.rest.spring.ParsedMapArgumentResolver;
import dwf.web.upload.FileSystemDownloadEndpoint;
import dwf.web.upload.S3DownloadEndpoint;

@Configuration
@ComponentScan(basePackages = {"dwf.web"})
public class DwfWebRestAutoConfiguration {
	
	@Autowired
	private ParsedMapArgumentResolver parsedMapArgumentResolver;
	
	@Autowired
	private SessionFactory sessionFactory;

	@Bean(name="openSessionInViewInterceptor")
	public OpenSessionInViewInterceptor openSessionInViewInterceptor() {
		OpenSessionInViewInterceptor o = new OpenSessionInViewInterceptor();
		o.setSessionFactory(sessionFactory);
		return o;
	}
	
	@Configuration("dwfWebRestAutoConfiguration.OpenSessionInViewInterceptorConfiguration")
	@ConditionalOnClass(name="dwf.data.autoconfigure.DwfDataAutoConfiguration")
	@ConditionalOnWebApplication
	static class OpenSessionInViewInterceptorConfiguration extends WebMvcConfigurerAdapter {
		@Autowired
		private OpenSessionInViewInterceptor openSessionInViewInterceptor;
		
		@Override
		public void addInterceptors(InterceptorRegistry registry) {
			registry.addWebRequestInterceptor(openSessionInViewInterceptor);
		}
	}
	
	
	@Configuration
	@ConditionalOnProperty(prefix = "dwf.web", name = "uploadmanager", havingValue = "s3")
	static class S3UploadManagerConfiguration {
		@Bean
		public S3DownloadEndpoint downloadEndpoint() {
			S3DownloadEndpoint s = new S3DownloadEndpoint();
			return s;
		}
	}
	
	@Configuration
	@ConditionalOnProperty(prefix = "dwf.web", name = "uploadmanager", havingValue = "s3-async")
	static class S3DownloadEndpointConfiguration {
		@Bean
		public S3DownloadEndpoint downloadEndpoint() {
			S3DownloadEndpoint s = new S3DownloadEndpoint();
			return s;
		}
	}
	

	@Configuration
	@ConditionalOnProperty(prefix = "dwf.web", name = "uploadmanager", havingValue = "filesystem", matchIfMissing=true)
	static class FileSystemDownloadEndpointConfiguration {
		@Bean
		public FileSystemDownloadEndpoint downloadEndpoint() {
			FileSystemDownloadEndpoint s = new FileSystemDownloadEndpoint();
			return s;
		}
	}
	
	/**
	 * Customizations that will improve the startup time
	 * @author Hirata
	 *
	 */
	@Configuration
	@ConditionalOnWebApplication
	@AutoConfigureBefore(EmbeddedServletContainerAutoConfiguration.class)
	@ConditionalOnProperty(prefix = "dwf.generated.jsp.configuration", name = "enabled", matchIfMissing = false)
	static class DwfTomcatEmbeddedServletContainerFactoryConfiguration {
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
}
