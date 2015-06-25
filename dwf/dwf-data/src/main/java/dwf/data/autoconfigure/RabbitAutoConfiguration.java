package dwf.data.autoconfigure;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dwf.asynchronous.Listener;

@Configuration
public class RabbitAutoConfiguration {
	
	@Bean
	public ConnectionFactory connectionFactory(){
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
		return connectionFactory;
	}
	
	@Bean
	public AmqpAdmin amqpAdmin(){
		return new RabbitAdmin(connectionFactory());
	}
	
	@Bean
	public RabbitTemplate rabbitTemplate(){
		return new RabbitTemplate(connectionFactory());
	}
	
	@Bean
	public Queue queue() {
		return new Queue("testQueue");
	}
	
	@Bean
	SimpleMessageListenerContainer container(ConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		container.setQueueNames("testQueue");
		container.setMessageListener(listenerAdapter);
		return container;
	}
	
	
	@Bean
	Listener listener(){
		return new Listener();
	}
	
	@Bean
	MessageListenerAdapter listenerAdapter(Listener listener) {
		return new MessageListenerAdapter(listener, "onMessage");
	}
}
