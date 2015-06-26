package dwf.web.upload;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping // responds to requests via /dl/**
public class S3UploadManagerAsync extends S3UploadManager {
	
	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	public S3UploadManagerAsync(String accessKey, String secretKey) {
		super(accessKey, secretKey);
	}

	@Override
	public void saveThumbnail(Serializable id, String propertyToFilePath,
			Class<?> daoClass, Class<?> entityClass, String entityName) throws Exception{
		Map<String, Serializable> imageProcParams = new HashMap<String, Serializable>();
		imageProcParams.put("id", id);
		imageProcParams.put("propertyToFilePath", propertyToFilePath);
		imageProcParams.put("daoClass", daoClass);
		imageProcParams.put("entityClass", entityClass);
		imageProcParams.put("entityName", entityName);
		
		rabbitTemplate.convertAndSend("testQueue", imageProcParams);

	}
	
}
