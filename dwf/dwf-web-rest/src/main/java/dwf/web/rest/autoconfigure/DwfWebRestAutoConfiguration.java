package dwf.web.rest.autoconfigure;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.hibernate4.support.OpenSessionInViewInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import dwf.upload.UploadManager;
import dwf.web.upload.FileSystemDownloadEndpoint;
import dwf.web.upload.S3DownloadEndpoint;

@Configuration
@ComponentScan(basePackages = {"dwf.web"})
@PropertySource("classpath:/dwf-web-rest-default.properties")
public class DwfWebRestAutoConfiguration {

	@ConditionalOnWebApplication
	@ConditionalOnBean(type={"dwf.data.autoconfigure.DwfDataAutoConfiguration", "org.hibernate.SessionFactory"})
	public static class OpenSessionInViewConfiguration {
		@Autowired
		private SessionFactory sessionFactory;
	
		@Bean(name="openSessionInViewInterceptor")
		public OpenSessionInViewInterceptor openSessionInViewInterceptor() {
			OpenSessionInViewInterceptor o = new OpenSessionInViewInterceptor();
			o.setSessionFactory(sessionFactory);
			return o;
		}
	}
	
	@Configuration("dwfWebRestAutoConfiguration.OpenSessionInViewInterceptorConfiguration")
	@ConditionalOnBean(type={"dwf.data.autoconfigure.DwfDataAutoConfiguration", "org.hibernate.SessionFactory"})
	@ConditionalOnWebApplication
	static class OpenSessionInViewConfigurerAdapter extends WebMvcConfigurerAdapter {
		@Autowired
		private OpenSessionInViewInterceptor openSessionInViewInterceptor;
		
		@Override
		public void addInterceptors(InterceptorRegistry registry) {
			registry.addWebRequestInterceptor(openSessionInViewInterceptor);
		}
	}

	
	
	@Configuration
	@ConditionalOnProperty(prefix = "dwf.web", name = "uploadmanager", havingValue = "s3")
	@ConditionalOnWebApplication
	@ConditionalOnBean(value=UploadManager.class)
	static class S3UploadManagerConfiguration {
		@Bean
		public S3DownloadEndpoint downloadEndpoint() {
			S3DownloadEndpoint s = new S3DownloadEndpoint();
			return s;
		}
	}
	
	@Configuration
	@ConditionalOnProperty(prefix = "dwf.web", name = "uploadmanager", havingValue = "s3-async")
	@ConditionalOnWebApplication
	@ConditionalOnBean(value=UploadManager.class)
	static class S3DownloadEndpointConfiguration {
		@Bean
		public S3DownloadEndpoint downloadEndpoint() {
			S3DownloadEndpoint s = new S3DownloadEndpoint();
			return s;
		}
	}
	

	@Configuration
	@ConditionalOnProperty(prefix = "dwf.web", name = "uploadmanager", havingValue = "filesystem", matchIfMissing=true)
	@ConditionalOnWebApplication
	@ConditionalOnBean(value=UploadManager.class)
	static class FileSystemDownloadEndpointConfiguration {
		@Bean
		public FileSystemDownloadEndpoint downloadEndpoint() {
			FileSystemDownloadEndpoint s = new FileSystemDownloadEndpoint();
			return s;
		}
	}
}
