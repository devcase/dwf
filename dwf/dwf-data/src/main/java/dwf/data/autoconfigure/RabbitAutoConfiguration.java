package dwf.data.autoconfigure;

import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import dwf.asynchronous.Listener;

@Configuration
public class RabbitAutoConfiguration {
	
	
	@Bean
	public ConnectionFactory connectionFactory(){
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
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
		return new Queue("testQueue");
	}
	
	@Bean
	MessageListener listener(){
		return new Listener();
	}
	
	@Bean
	MessageListenerAdapter listenerAdapter(MessageListener listener) {
		MessageListenerAdapter messageListenerAdapter = new MessageListenerAdapter(listener, "onMessage");
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
