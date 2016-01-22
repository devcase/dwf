package dwf.upload.image;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class RabbitAsyncImageResizer extends SyncImageResizer implements MessageListener  {
	private Log log = LogFactory.getLog(getClass());
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	@Autowired
	private SessionFactory sessionFactory;
	
	private final String queueName;
	
	public RabbitAsyncImageResizer(String queueName) {
		super();
		this.queueName = queueName;
	}


	@Override
	public void resizeImage(Serializable id, String entityName, String property) throws IOException {
		Map<String, Serializable> imageProcParams = new HashMap<String, Serializable>();
		imageProcParams.put("id", id);
		imageProcParams.put("property", property);
		imageProcParams.put("entityName", entityName);
		rabbitTemplate.convertAndSend(this.queueName, imageProcParams);
		log.info("Image resize message sent to queue " + queueName);
	}

	@Override
	public void onMessage(Message msg) {
		log.info("Message arrived - starting image processing");
		
		Map<String, ?> convertedMessage = (Map<String, ?>) rabbitTemplate.getMessageConverter().fromMessage(msg);
		Serializable id = (Serializable) convertedMessage.get("id");
		String property = (String) convertedMessage.get("property");
		String entityName = (String)convertedMessage.get("entityName");
		
		Session session = sessionFactory.openSession();
		try {
			log.info("Starting image resizing for " + entityName + " with id " + id);
			super.resizeImage(id, entityName, property);
			log.info("Image processing done");
		} catch (Throwable e) {
			log.error("Error processing image: " , e);
		} finally {
			session.close();
		}
	}
}
