package dwf.data.autoconfigure;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dwf.asynchronous.ThumbnailUploaderAsyncListener;

/**
 * configura conexão a RabbitMQ (usado para o upload de imagens)
 * @author Hirata
 *
 */
@Configuration
@EnableConfigurationProperties()
@ConfigurationProperties(prefix = "dwf.web.uploadmanager.s3-async.rabbitmq")
@ConditionalOnProperty(prefix="dwf.web", name="uploadmanager", havingValue="s3-async")
public class ImageAsyncProcessingAutoConfiguration {
	
	private String host;
	private String username;
	private String password;
	private String virtualHost;
	private String queueName;
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	

	public String getVirtualHost() {
		return virtualHost;
	}

	public void setVirtualHost(String virtualHost) {
		this.virtualHost = virtualHost;
	}
	

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	@Bean
	public ConnectionFactory connectionFactory(){
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory(getHost());
		connectionFactory.setPassword(getPassword());
		connectionFactory.setUsername(getUsername());
		connectionFactory.setVirtualHost(getVirtualHost());
		return connectionFactory;
	}
	
	@Bean
	public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory){
		return new RabbitAdmin(connectionFactory);
	}
	
	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
		RabbitTemplate template = new RabbitTemplate(connectionFactory);
		return template;
	}
	
	@Bean
	public Queue queue() {
		return new Queue(queueName);
	}
	
	
	@Configuration
	@ConditionalOnProperty(prefix="dwf.web.uploadmanager.s3-async.listener", name="enabled", matchIfMissing=false)
	static class ListenerConfiguration {
		@Bean
		MessageListener thumbnailUploaderAsyncListener() {
			return new ThumbnailUploaderAsyncListener();
		}
		
		@Bean
		MessageListenerAdapter listenerAdapter(MessageListener thumbnailUploaderAsyncListener) {
			MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(thumbnailUploaderAsyncListener, "onMessage");
			return messageListenerAdapter;
		}
		
		@Bean
		SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, Queue queue, MessageListenerAdapter listenerAdapter) {
			SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
			container.setConnectionFactory(connectionFactory);
			container.setQueueNames(queue.getName());
			container.setMessageListener(listenerAdapter);
			container.start();
			return container;
		}
	}
}
