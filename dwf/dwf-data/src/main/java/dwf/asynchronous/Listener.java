package dwf.asynchronous;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class Listener implements MessageListener{

	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	@Override
	public void onMessage(Message msg) {
		Object convertedMessage = rabbitTemplate.getMessageConverter().fromMessage(msg);
		System.out.println(convertedMessage);
	}

}
