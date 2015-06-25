package dwf.asynchronous;

import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.util.IOUtils;

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;

import dwf.persistence.annotations.Image;
import dwf.persistence.domain.BaseEntity;
import dwf.persistence.utils.NotSyncPropertyDescriptor;

public class AsyncParamsPOJO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2751419951317185858L;
	private byte[] inputStreamByteArray;
	private Image imageAnnotation;
	private String contentType;
	private String originalFilename;
	private String propertyName;
	private String entityName;
	private Serializable id;
	private BaseEntity<?> connectedEntity;
	private Map<String, NotSyncPropertyDescriptor> entityProperties;
	
	public AsyncParamsPOJO(InputStream inputStream, Image imageAnnotation, String contentType, String originalFilename, String propertyName, 
			String entityName, Serializable id, BaseEntity<?> connectedEntity, Map<String, PropertyDescriptor> entityProperties){
		try {
			this.setInputStreamByteArray(IOUtils.toByteArray(inputStream));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.setImageAnnotation(imageAnnotation);
		this.setContentType(contentType);
		this.originalFilename = originalFilename;
		this.setPropertyName(propertyName);
		this.setEntityName(entityName);
		this.setId(id);
		this.setConnectedEntity(connectedEntity);
		
		Map<String, NotSyncPropertyDescriptor> newEntityProperties = new HashMap<String, NotSyncPropertyDescriptor>();
		for (Map.Entry<String, PropertyDescriptor> entry : entityProperties.entrySet()) {
			newEntityProperties.put(entry.getKey(), (NotSyncPropertyDescriptor)entry.getValue());
		}
		this.entityProperties = newEntityProperties;
	}

	public byte[] getInputStreamByteArray() {
		return inputStreamByteArray;
	}

	public void setInputStreamByteArray(byte[] inputStream) {
		this.inputStreamByteArray = inputStream;
	}
	
	public InputStream getInputStream(){
		ByteArrayInputStream bis = new ByteArrayInputStream(inputStreamByteArray);
		return bis;
	}

	public Image getImageAnnotation() {
		return imageAnnotation;
	}

	public void setImageAnnotation(Image imageAnnotation) {
		this.imageAnnotation = imageAnnotation;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getOriginalFilename() {
		return originalFilename;
	}

	public void setOriginalFilename(String originalFilename) {
		this.originalFilename = originalFilename;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public Serializable getId() {
		return id;
	}

	public void setId(Serializable id) {
		this.id = id;
	}

	public BaseEntity<?> getConnectedEntity() {
		return connectedEntity;
	}

	public void setConnectedEntity(BaseEntity<?> connectedEntity) {
		this.connectedEntity = connectedEntity;
	}

	public Map<String, NotSyncPropertyDescriptor> getEntityProperties() {
		return entityProperties;
	}

	public void setEntityProperties(Map<String, NotSyncPropertyDescriptor> entityProperties) {
		this.entityProperties = entityProperties;
	}

}
