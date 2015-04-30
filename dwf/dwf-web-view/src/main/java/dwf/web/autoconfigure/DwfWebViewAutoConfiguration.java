package dwf.web.autoconfigure;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.catalina.Context;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.WebResourceRoot.ResourceSetType;
import org.apache.catalina.core.StandardContext;
import org.apache.tomcat.util.scan.StandardJarScanner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.EmbeddedServletContainerAutoConfiguration;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
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
import org.springframework.context.annotation.Scope;
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

import de.javakaffee.web.msm.MemcachedBackupSessionManager;
import dwf.web.AjaxHashKeyManager;
import dwf.web.sitemesh.SitemeshView;
import dwf.web.spring.DwfReCaptchaInterceptor;

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
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/", "classpath:/dwf/web-resources/", "classpath:/resources/");
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
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public LocaleResolver localeResolver() {
		SessionLocaleResolver localeResolver = new SessionLocaleResolver();
		localeResolver.setDefaultLocale(Locale.getDefault());
		return localeResolver;
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
	@ConditionalOnClass(MemcachedBackupSessionManager.class)
	@ConditionalOnProperty("dwf.webview.memcachedsessionmanager.servers")
	static class MemcachedSessionManagerConfiguration {
		@Value("${dwf.webview.memcachedsessionmanager.servers:void}")
		private String memcacheservers;
		@Value("${dwf.webview.memcachedsessionmanager.username:void}")
		private String username;
		@Value("${dwf.webview.memcachedsessionmanager.password:void}")
		private String password;
		@Bean
		public TomcatContextCustomizer tomcatContextCustomizer() {
			return new TomcatContextCustomizer() {

				@Override
				public void customize(Context context) {
					 MemcachedBackupSessionManager manager = new MemcachedBackupSessionManager();
	                   
		            String[] servers = memcacheservers.split(",");
		            if (servers.length > 1) {
		              for (int i = 0; i < servers.length; ++i) {
		                servers[i] = "mc" + i + ":" + servers[i];
		              }
		            }
		            String serversStr = servers[0];
		            for (int i = 1; i < servers.length; ++i) {
		              serversStr += "," + servers[i];
		            }
		            manager.setMemcachedNodes(serversStr);
		            manager.setUsername(username);
		            manager.setPassword(password);
			        
			        manager.setMemcachedProtocol("binary");
			        manager.setSticky(false);
			        manager.setSessionBackupAsync(false);
			        manager.setEnabled(true);
			        manager.setEnableStatistics(true);
//				        manager.setOperationTimeout(commandLineParams.sessionStoreOperationTimout);
//				        manager.setLockingMode(commandLineParams.sessionStoreLockingMode);
//				        manager.setRequestUriIgnorePattern(commandLineParams.sessionStoreIgnorePattern);
			        context.setManager(manager);
				}
				

			};
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
	 * 
	 * TODO: Analisar outras estratégias - sobrescrever JasperInitializer? TldScanner?
	 * @return
	 */
	@Configuration
	@ConditionalOnClass(org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory.class)
	@Conditional(AdicionaDwfTagLibCondition.class)
	@AutoConfigureAfter(EmbeddedServletContainerAutoConfiguration.class)
	static class AdicionaDwfTaglibConfiguration extends WebMvcConfigurerAdapter {
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
								ClassLoader loader = getClass().getClassLoader();
								if (loader instanceof URLClassLoader) {
									for (URL url : ((URLClassLoader) loader).getURLs()) {
										String urlString = url.toString();
										if (urlString.startsWith("file:") && urlString.contains("dwf-web-view")) {
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
	static class AdicionaDwfTagLibCondition implements Condition {
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

