package dwf.web.autoconfigure;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.catalina.Context;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.WebResourceRoot.ResourceSetType;
import org.apache.catalina.core.StandardContext;
import org.apache.tomcat.util.scan.StandardJarScanner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.boot.context.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import dwf.upload.UploadManager;
import dwf.web.AjaxHashKeyManager;
import dwf.web.sitemesh.SitemeshView;
import dwf.web.spring.DwfReCaptchaInterceptor;
import dwf.web.upload.FileSystemUploadManager;
import dwf.web.upload.S3UploadManager;

@Configuration
@ComponentScan(basePackages = {"dwf.web"})
@EnableWebMvc
public class DwfWebViewAutoConfiguration extends WebMvcConfigurerAdapter {
	
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
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		//<!-- Ao acessar urls do tipo /resources/, ele procura na pasta da aplicação e, depois, nos arquivos do dwf -->
		//<mvc:resources mapping="/resources/**" location="/resources/, classpath:/dwf/web-resources/" />
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/", "classpath:/dwf/web-resources/");
	}
	
	
	@Value("${dwf.web.recaptcha.privatekey:testdb}")
	private String recaptchaPrivateKey = "testdb";
	@Value("${dwf.web.recaptcha.publickey:testdb}")
	private String recaptchaPublicKey = "testdb";

	
	public String getRecaptchaPublicKey() {
		return recaptchaPublicKey;
	}

	public void setRecaptchaPublicKey(String recaptchaPublicKey) {
		this.recaptchaPublicKey = recaptchaPublicKey;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new DwfReCaptchaInterceptor(recaptchaPrivateKey)).addPathPatterns("/**");
	}

	@Bean
	public LocaleResolver localeResolver() {
		return new SessionLocaleResolver();
	}
	
	@Bean
	public AjaxHashKeyManager ajaxHashKeyManager() {
		return new AjaxHashKeyManager();
	}

	@Configuration
	static class LocaleChangeInterceptorConfiguration extends WebMvcConfigurerAdapter {

		@Override
		public void addInterceptors(InterceptorRegistry registry) {
			registry.addInterceptor(new LocaleChangeInterceptor());
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
	
	/**
	 * <p>Workaround maldito para usar a taglib do dwf-web-view como item do classpath como diretório
	 * ao invés de jar (resolução do workspace do m2e), com TomcatEmbedded.</p>
	 * <p>Com esta configuração:</p>
	 * <ol>
	 * 	<li>O {@link org.apache.tomcat.util.scan.StandardJarScanner} vai incluir diretórios durante a busca
	 * por taglibs que é feita pelo jasper (ver {@link org.apache.jasper.servlet.TldScanner})</li>
	 * 	<li>Quando uma tag for referenciada, ele precisa encontrar o arquivo tag na pasta do dwf - o tomcat
	 * procura o arquivo dentro dos WebResources</li>
	 * </ol>
	 * <p>Só vai ser habilitado se detectar que o dwf-web-view está no classpath como diretório, ao invés
	 * de via jar</p>
	 * @return
	 */
	@Configuration
	@ConditionalOnClass(org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory.class)
	@Conditional(ConfiguracaoMalditaConfigurationCondition.class)
	@AutoConfigureAfter(EmbeddedServletContainerAutoConfiguration.class)
	static class ConfiguracaoMalditaConfiguration extends WebMvcConfigurerAdapter {
		@Bean
		public EmbeddedServletContainerCustomizer tomcatContextCustomizer() {
			return new EmbeddedServletContainerCustomizer() {
				
				private StandardContext standardContext;

				@Override
				public void customize(ConfigurableEmbeddedServletContainer container) {
					//Habilita a busca de taglibs
					if (container instanceof TomcatEmbeddedServletContainerFactory) {
						
						TomcatEmbeddedServletContainerFactory factory = (TomcatEmbeddedServletContainerFactory) container;
						
						factory.addInitializers(new ServletContextInitializer() {

							@Override
							public void onStartup(ServletContext servletContext) throws ServletException {
								//Adiciona diretório do classpath como participante dos WebResources - assim o jasper encontrará
								//os arquivos .tag durante a compilação do JSP
								WebResourceRoot resources = standardContext.getResources();
//								if(resources == null) {
//									resources = new StandardRoot(standardContext);
//									standardContext.setResources(resources);
//								}
								ClassLoader loader = getClass().getClassLoader();
								if (loader instanceof URLClassLoader) {
									for (URL url : ((URLClassLoader) loader).getURLs()) {
										String urlString = url.toString();
										if (urlString.startsWith("file:")) {
											String dir = urlString.substring("file:".length());
											if (new File(dir).isDirectory()) {
												resources.createWebResourceSet(
														ResourceSetType.RESOURCE_JAR, "/", url, "/");
											}
										}
									}
								}
								
							}
							
						});
						
						factory.addContextCustomizers(new TomcatContextCustomizer() {
							@Override
							public void customize(Context context) {
								
								standardContext = (StandardContext) context;
								StandardJarScanner standardJarScanner = (StandardJarScanner) standardContext.getJarScanner();
								
								//{@link org.apache.jasper.servlet.TldScanner} vai incluir na busca diretórios do classpath
								standardJarScanner.setScanAllDirectories(true);
							}
						});
					}
				}
			};
		}
	}
	/**
	 * 
	 * @author Hirata
	 *
	 */
	static class ConfiguracaoMalditaConfigurationCondition implements Condition {
		public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
			ClassLoader loader = getClass().getClassLoader();
			if (loader instanceof URLClassLoader) {
				for (URL url : ((URLClassLoader) loader).getURLs()) {
					String urlString = url.toString();
					if (urlString.startsWith("file:") && urlString.contains("dwf-web-view")) {
						String dir = urlString.substring("file:".length());
						if (new File(dir).isDirectory()) {
							return true;
						}
					}
				}
			}
			return false;
		}
	}
}
