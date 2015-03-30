package dwf.web.rest.autoconfigure;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate4.support.OpenSessionInViewInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import dwf.upload.UploadManager;
import dwf.web.rest.spring.ParsedMapArgumentResolver;
import dwf.web.upload.FileSystemUploadManager;
import dwf.web.upload.S3UploadManager;

@Configuration
@ComponentScan(basePackages = {"dwf.web.rest"})
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

		@Value("${dwf.web.uploadmanager.bucketname:testdb}")
		private String bucketName = "testdb";
		
		@Bean
		public UploadManager uploadManager() {
			S3UploadManager s = new S3UploadManager();
			s.setBucketName(bucketName);
			return s;
			
		}
	}
	

	@Configuration
	@ConditionalOnProperty(prefix = "dwf.web", name = "uploadmanager", havingValue = "filesystem")
	static class FileSystemUploadManagerConfiguration {

		@Value("${dwf.web.uploadmanager.directory:testdb}")
		private String directory = "testdb";

		@Bean
		public FileSystemUploadManager uploadManager() {
			FileSystemUploadManager s = new FileSystemUploadManager();
			s.setDirectory(directory);
			return s;
		}
	}
}
