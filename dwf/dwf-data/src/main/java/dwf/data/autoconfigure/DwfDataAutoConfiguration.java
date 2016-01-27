package dwf.data.autoconfigure;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.AggregateResourceBundleLocator;
import org.jongo.Jongo;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.bind.RelaxedDataBinder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.LocaleContextMessageInterpolator;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import dwf.asynchronous.AsyncImporterListener;
import dwf.persistence.export.Importer;
import dwf.persistence.utils.DwfNamingStrategy;
import dwf.persistence.utils.MongoIdModule;
import dwf.upload.FileSystemUploadManager;
import dwf.upload.S3UploadManager;
import dwf.upload.UploadManager;
import dwf.upload.image.ImageResizer;
import dwf.upload.image.RabbitAsyncImageResizer;
import dwf.upload.image.SyncImageResizer;

@Configuration
@ComponentScan(basePackages = {"dwf.activitylog.service", "dwf.persistence", "dwf.multilang", "dwf.user", "dwf.slack"})
@PropertySource("classpath:/dwf-data-default.properties")
@ConfigurationProperties(prefix = "dwf.data")
@ConditionalOnProperty(prefix="dwf.data", name="entityPackage")
public class DwfDataAutoConfiguration  {
	@Autowired
	private DataSource dataSource;
	
	/**
	 * configuravel a partir de dwf.data.hibernateproperties
	 */
	private Map<String, String> hibernateProperties;
	/**
	 * configuravel a partir de dwf.data.entityPackage
	 */
	private String entityPackage;

	public Map<String, String> getHibernateProperties() {
		return hibernateProperties;
	}

	public void setHibernateProperties(Map<String, String> hibernateProperties) {
		this.hibernateProperties = hibernateProperties;
	}
	public String getEntityPackage() {
		return entityPackage;
	}
	public void setEntityPackage(String entityPackage) {
		this.entityPackage = entityPackage;
	}

	/**
	 * Bean Validation Provider. 
	 *  (http://docs.spring.io/spring-framework/docs/current/spring-framework-reference/html/validation.html#validation-beanvalidation)
	 * @return
	 */
	@Bean
	public LocalValidatorFactoryBean validator() {
		LocalValidatorFactoryBean a = new LocalValidatorFactoryBean();
		a.setMessageInterpolator(
				new LocaleContextMessageInterpolator(
						new ResourceBundleMessageInterpolator(
								new AggregateResourceBundleLocator(Arrays.asList(new String[] {"labels", "dwf.labels", "org.hibernate.validator.ValidationMessages", "ValidationMessages"})))));
		return a;
	}
	
	@Configuration
	@ConditionalOnWebApplication
	@ConditionalOnClass(org.springframework.web.servlet.config.annotation.WebMvcConfigurer.class)
	public static class WebMvcValidationConfiguration extends WebMvcConfigurerAdapter {
		@Autowired
		private LocalValidatorFactoryBean validator;
		@Override
		public Validator getValidator() {
			return validator;
		}
	}
	
	/**
	 * Enables Spring-driven Method Validation (http://docs.spring.io/spring-framework/docs/current/spring-framework-reference/html/validation.html#validation-beanvalidation)
	 * @return
	 */
	@Bean
	public MethodValidationPostProcessor methodValidationPostProcessor() {
		return new MethodValidationPostProcessor();
	}
	
	@Bean
	public DwfNamingStrategy dwfNamingStrategy(){
		return new DwfNamingStrategy();
	}
	
	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	@DependsOn("flyway") //sessionFactory é criado após o bean flyway
	public LocalSessionFactoryBean sessionFactory(DwfNamingStrategy dwfNamingStrategy) {
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setPackagesToScan( new String [] {"dwf.activitylog.domain", "dwf.user.domain", entityPackage});
		sessionFactory.setNamingStrategy(dwfNamingStrategy);
		sessionFactory.setDataSource(dataSource);
		if(hibernateProperties != null) {
			Properties p = new Properties();
			p.putAll(hibernateProperties);
			sessionFactory.setHibernateProperties(p);
		}
		
		return sessionFactory;
	}
	
	/**
	 * Se Flyway não é configurado automaticamente (exemplo: DdlScriptGenerator), garante a existência de bean com nome flyway,
	 * para a anotação @DependsOn("flyway") de sessionFactory() funcionar.
	 * @author cesar_000
	 *
	 */
	@Configuration
	@ConditionalOnMissingBean(name="flyway")
	static class MockFlywayConfiguration {
		@Bean(name="flyway")
		public Object flyway() {
			return new Object();
		}
	}
	
	
	@Configuration
	@ConditionalOnClass(Jongo.class)
	@ConditionalOnProperty(prefix="mongodb", name="uri")
	@ConfigurationProperties("mongodb")
	static class MongoConfig {
		
		private String uri;
		
		public String getUri() {
			return uri;
		}
		public void setUri(String uri) {
			this.uri = uri;
		}

		@Bean
		@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
		public Jongo jongo() throws UnknownHostException {
			MongoClientURI mongoUri = new MongoClientURI(uri);
			DB db = new MongoClient(mongoUri).getDB(mongoUri.getDatabase());
			return new Jongo(db,  MongoIdModule.getMapperBuilder().build());
		}
	}
	
	/**
	 * Substitui os beans que implementam dwf.persistence.export.Importer por 
	 * um Wrapper que enfileira as importações no RabbitMQ
	 * @author cesar_000
	 *
	 */
	@Configuration
	@ConditionalOnProperty(prefix="dwf.data.asyncimporter", name="enabled")
	static class AsyncImporterConfiguration {
		@Value("${dwf.data.asyncimporter.queuename:nonono}")
		private String queueName="";
		/**
		 * Substitui os beans que implementam dwf.persistence.export.Importer por 
		 * um Wrapper que enfileira as importações no RabbitMQ
		 * @author cesar_000
		 *
		 */
		@Bean
		public BeanPostProcessor asyncImporterPostProcessor() {
			return new BeanPostProcessor() {
				@Autowired
				private RabbitTemplate rabbitTemplate;

				
				
				@Override
				public Object postProcessBeforeInitialization(Object bean, String beanName)
						throws BeansException {
					return bean;
				}
				
				@Override
				public Object postProcessAfterInitialization(Object bean, String beanName)
						throws BeansException {
					if(bean instanceof Importer) {
						final Importer<?> importer = (Importer<?>) bean;
						return new AsyncImporterListener.ImporterWrapper(rabbitTemplate, importer, queueName);
					} else {
						return bean;
					}
				}
			};
		}

		/**
		 * Habilita o listener do rabbitMQ que processa as importações
		 * @author cesar_000
		 *
		 */
		@Configuration
		@ConditionalOnProperty(prefix="dwf.rabbitmq.listener", name="enabled")
		static class ListenerConfiguration {
			@Value("${dwf.data.asyncimporter.queuename:nonono}")
			private String queueName = "";
			
			@Bean
			AsyncImporterListener asyncImporterListener() {
				return new AsyncImporterListener();
			}
			
			@Bean
			MessageListenerAdapter asyncImporterListenerAdapter(AsyncImporterListener asyncImporterListener) {
				MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(asyncImporterListener, "onMessage");
				return messageListenerAdapter;
			}
			
			@Bean
			SimpleMessageListenerContainer asyncImporterListenerContainer(ConnectionFactory connectionFactory, MessageListenerAdapter asyncImporterListenerAdapter) {
				SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
				container.setConnectionFactory(connectionFactory);
				container.setQueueNames(this.queueName);
				container.setMessageListener(asyncImporterListenerAdapter);
				return container;
			}
		}
	}
	
	@Configuration
	static class UploadManagerConfiguration {
		@Configuration
		@ConditionalOnProperty(prefix = "dwf.web", name = "uploadmanager", havingValue = "filesystem", matchIfMissing=true)
		static class FileSystemUploadManagerConfiguration {

			@Value("${dwf.web.uploadmanager.directory:uploadedFiles}")
			private String directory = "uploadedFiles";

			@Bean
			public UploadManager uploadManager() {
				FileSystemUploadManager s = new FileSystemUploadManager();
				s.setDirectory(directory);
				return s;
			}
		}

		@Configuration
		@ConditionalOnProperty(prefix = "dwf.web", name = "uploadmanager", havingValue = "s3")
		static class S3UploadManagerConfiguration {

			@Value("${dwf.web.uploadmanager.bucketname:testdb}")
			private String bucketName = "testdb";
			@Value("${aws.accessKeyId:testdb}")
			private String accessKeyId = "testdb";
			@Value("${aws.secretKey:testdb}")
			private String secretKey = "testdb";

			@Bean
			public UploadManager uploadManager() {
				S3UploadManager s = new S3UploadManager(accessKeyId, secretKey);
				s.setBucketName(bucketName);
				return s;
			}
		}
		
		@Configuration
		@ConditionalOnProperty(prefix = "dwf.web", name = "uploadmanager", havingValue = "s3-async")
		static class S3UploadManagerAsyncConfiguration {

			@Value("${dwf.web.uploadmanager.bucketname:testdb}")
			private String bucketName = "testdb";
			@Value("${aws.accessKeyId:testdb}")
			private String accessKeyId = "testdb";
			@Value("${aws.secretKey:testdb}")
			private String secretKey = "testdb";

			@Bean
			public UploadManager uploadManager() {
				S3UploadManager s = new S3UploadManager(accessKeyId, secretKey);
				s.setBucketName(bucketName);
				return s;
			}
		}

		
	}
	
	@Configuration
	static class ImageResizerConfiguration {
		@Configuration
		@ConditionalOnProperty(prefix = "dwf.web", name = "uploadmanager", havingValue = "s3-async")
		static class AsyncConfig {
			@Value("${dwf.web.uploadmanager.s3-async.rabbitmq.queuename:imageprocessingqueue}")
			private String queueName = "imageprocessingqueue";

			@Bean
			@ConditionalOnProperty(prefix="dwf.rabbitmq.listener", name="enabled")
			MessageListenerAdapter asyncImageResizerListenerAdapter(ImageResizer imageResizer) {
				MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(imageResizer, "onMessage");
				return messageListenerAdapter;
			}
			
			@Bean
			@ConditionalOnProperty(prefix="dwf.rabbitmq.listener", name="enabled")
			SimpleMessageListenerContainer asyncImageListenerContainer(ConnectionFactory connectionFactory, MessageListenerAdapter asyncImageResizerListenerAdapter) {
				SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
				container.setConnectionFactory(connectionFactory);
				container.setQueueNames(queueName);
				container.setMessageListener(asyncImageResizerListenerAdapter);
				return container;
			}

			@Bean
			public ImageResizer imageResizer(UploadManager uploadManager) {
				return new RabbitAsyncImageResizer(queueName);
			}
		}
		
		@Bean
		@ConditionalOnMissingBean(ImageResizer.class)
		public ImageResizer imageResizer(UploadManager uploadManager) {
			return new SyncImageResizer();
		}
	}
	
	@Configuration
	@ConfigurationProperties(prefix = "spring.datasource")
	@EnableConfigurationProperties
	protected static class DataSourcePoolConfiguration {
		
		private Map<String, String> properties;
		public Map<String, String> getProperties() {
			return properties;
		}
		public void setProperties(Map<String, String> properties) {
			this.properties = properties;
		}


		@Bean
		public BeanPostProcessor dataSourcePostProcessor() {
			return new BeanPostProcessor() {
				
				@Override
				public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
					if( properties!=null && bean instanceof DataSource) {
						MutablePropertyValues pv = new MutablePropertyValues(properties);
						new RelaxedDataBinder(bean).bind(pv);
					}
					return bean;
				}
				
				@Override
				public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
					return bean;
				}
			};
		}
	}
}
