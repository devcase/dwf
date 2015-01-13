package dwf.web.autoconfigure;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import dwf.upload.UploadManager;
import dwf.web.sitemesh.SitemeshView;
import dwf.web.spring.DwfRequestMappingHandlerAdapter;
import dwf.web.spring.ParsedMapArgumentResolver;
import dwf.web.upload.FileSystemUploadManager;
import dwf.web.upload.S3UploadManager;

@Configuration
@ComponentScan(basePackages = {"dwf.web"})
@EnableWebMvc
public class DwfWebViewAutoConfiguration extends WebMvcConfigurerAdapter {
	
	@Autowired
	private ParsedMapArgumentResolver parsedMapArgumentResolver;
	
	@Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new  ResourceBundleMessageSource();
		messageSource.setBasenames("labels", "dwf.labels", "org.hibernate.validator.ValidationMessages");
		return messageSource;
	}
	
	/**
	 * Overrides {@link org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration} 
	 * 
	 * @return
	 */
	@Bean
	public InternalResourceViewResolver defaultViewResolver() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setViewClass(SitemeshView.class);
		viewResolver.setPrefix("/WEB-INF/jsp/");
		viewResolver.setSuffix(".jsp");
		return viewResolver;
	}
	
	@Bean()
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	@Lazy(false)
	public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
		DwfRequestMappingHandlerAdapter requestMappingHandlerAdapter = new DwfRequestMappingHandlerAdapter();
		requestMappingHandlerAdapter.setIgnoreDefaultModelOnRedirect(true);//TODO comentar o motivo
		requestMappingHandlerAdapter.setCustomArgumentResolvers(new ArrayList<HandlerMethodArgumentResolver>());
		requestMappingHandlerAdapter.getCustomArgumentResolvers().add(parsedMapArgumentResolver);
		return requestMappingHandlerAdapter;
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//		<!-- Ao acessar urls do tipo /resources/, ele procura na pasta da aplicação e, depois, nos arquivos do dwf -->
//		<mvc:resources mapping="/resources/**" location="/resources/, classpath:/dwf/web-resources/" />
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/", "classpath:/dwf/web-resources/");
	}
	
	
	@Configuration
	@ConditionalOnProperty(prefix = "dwf.web", name = "uploadmanager", havingValue = "s3")
	public class S3UploadManagerConfiguration {
		
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
	public static class FileSystemUploadManagerConfiguration {
		
		public FileSystemUploadManagerConfiguration() {
			super();
		}

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
