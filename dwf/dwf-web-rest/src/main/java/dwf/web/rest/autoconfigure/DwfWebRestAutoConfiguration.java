package dwf.web.rest.autoconfigure;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate4.support.OpenSessionInViewInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import dwf.web.rest.spring.ParsedMapArgumentResolver;
import dwf.web.upload.DownloadEndpoint;
import dwf.web.upload.FileSystemDownloadEndpoint;
import dwf.web.upload.S3DownloadEndpoint;

@Configuration
@ComponentScan(basePackages = {"dwf.web"})
public class DwfWebRestAutoConfiguration {
	
	@Autowired
	private ParsedMapArgumentResolver parsedMapArgumentResolver;
	
	@Configuration
	@ConditionalOnClass(name="dwf.data.autoconfigure.DwfDataAutoConfiguration")
	static class OpenSessionInViewInterceptorConfiguration extends WebMvcConfigurerAdapter {
		@Autowired
		private SessionFactory sessionFactory;

		@Bean
		public OpenSessionInViewInterceptor openSessionInViewInterceptor() {
			OpenSessionInViewInterceptor o = new OpenSessionInViewInterceptor();
			o.setSessionFactory(sessionFactory);
			return o;
		}
		
		@Override
		public void addInterceptors(InterceptorRegistry registry) {
			registry.addWebRequestInterceptor(openSessionInViewInterceptor());
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
	@ConditionalOnProperty(prefix = "dwf.web", name = "uploadmanager", havingValue = "filesystem")
	static class FileSystemDownloadEndpointConfiguration {
		@Bean
		public FileSystemDownloadEndpoint downloadEndpoint() {
			FileSystemDownloadEndpoint s = new FileSystemDownloadEndpoint();
			return s;
		}
	}
}
