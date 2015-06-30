package dwf.data.autoconfigure;

import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * configura conexão a RabbitMQ (usado para o upload de imagens)
 * @author Hirata
 *
 */
@Configuration
@EnableConfigurationProperties()
@ConfigurationProperties(prefix = "dwf.rabbitmq")
@ConditionalOnProperty(prefix="dwf.rabbitmq", name="host")
public class RabbitAutoConfiguration {
	
	private String host;
	private String username;
	private String password;
	private String virtualHost;
	
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
	
	@Bean
	public ConnectionFactory connectionFactory(){
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory(getHost());
		if(StringUtils.isNotBlank(password)) connectionFactory.setPassword(password);
		if(StringUtils.isNotBlank(username)) connectionFactory.setUsername(username);
		if(StringUtils.isNotBlank(virtualHost)) connectionFactory.setVirtualHost(virtualHost);
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
}
