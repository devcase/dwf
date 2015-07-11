package dwf.web.rest.autoconfigure;

import org.hibernate.SessionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate4.support.OpenSessionInViewInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import dwf.upload.UploadManager;
import dwf.upload.image.ImageResizer;
import dwf.upload.image.RabbitAsyncImageResizer;
import dwf.upload.image.SyncImageResizer;
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
		
		@Bean
		public ImageResizer imageResizer() {
			return new SyncImageResizer();
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
		
		
		
		@Configuration
		@ConditionalOnProperty(prefix="dwf.rabbitmq.listener", name="enabled")
		@ConditionalOnBean(S3UploadManagerAsyncConfiguration.class)
		static class WithListenerConfiguration {
			@Value("${dwf.web.uploadmanager.s3-async.rabbitmq.queuename:nonono}")
			private String queueName = "";

			@Bean
			public ImageResizer imageResizer() {
				return new RabbitAsyncImageResizer(queueName);
			}
			
			
			@Bean
			MessageListenerAdapter listenerAdapter(ImageResizer imageResizer) {
				MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(imageResizer, "onMessage");
				return messageListenerAdapter;
			}
			
			@Bean
			SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
				SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
				container.setConnectionFactory(connectionFactory);
				container.setQueueNames(this.queueName);
				container.setMessageListener(listenerAdapter);
				return container;
			}
		}
		@Configuration
		@ConditionalOnProperty(prefix="dwf.rabbitmq.listener", name="enabled", havingValue="false", matchIfMissing=true)
		@ConditionalOnBean(S3UploadManagerAsyncConfiguration.class)
		static class WithoutListenerConfiguration {
			@Value("${dwf.web.uploadmanager.s3-async.rabbitmq.queuename:nonono}")
			private String queueName = "";

			@Bean
			public ImageResizer imageResizer() {
				return new RabbitAsyncImageResizer(queueName);
			}
		}
		
	}
	

	@Configuration
	@ConditionalOnProperty(prefix = "dwf.web", name = "uploadmanager", havingValue = "filesystem")
	static class FileSystemUploadManagerConfiguration {

		@Value("${dwf.web.uploadmanager.directory:'/temp'}")
		private String directory = "testdb";

		@Bean
		public FileSystemUploadManager uploadManager() {
			FileSystemUploadManager s = new FileSystemUploadManager();
			s.setDirectory(directory);
			return s;
		}
		
		@Bean
		public ImageResizer imageResizer() {
			return new SyncImageResizer();
		}

	}
}
