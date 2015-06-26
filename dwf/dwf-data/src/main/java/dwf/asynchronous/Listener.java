package dwf.asynchronous;

import java.awt.Color;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.cfg.Configuration;
import org.hibernate.sql.ordering.antlr.Factory;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Mode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.hibernate3.SpringSessionContext;

import dwf.activitylog.domain.UpdatedProperty;
import dwf.activitylog.service.ActivityLogService;
import dwf.persistence.annotations.Image;
import dwf.upload.UploadManager;
import dwf.persistence.dao.DAO;
import dwf.persistence.domain.BaseEntity;

public class Listener implements MessageListener{

	@Autowired
	protected ActivityLogService activityLogService;
	@Autowired
	private UploadManager uploadManager;
	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private RabbitTemplate rabbitTemplate;
	@Value("${dwf.web.uploadmanager.directory}")
	private String uploadDirectory;
	
	@Override
	public void onMessage(Message msg) {
		Map<String, ?> convertedMessage = (Map<String, ?>) rabbitTemplate.getMessageConverter().fromMessage(msg);
		System.out.println(convertedMessage);
		Serializable id = (Serializable) convertedMessage.get("id");
		String propertyToFilePath = (String) convertedMessage.get("propertyToFilePath");
		Class<?> daoClass = (Class<?>)convertedMessage.get("daoClass");
		Class<?> entityClass = (Class<?>)convertedMessage.get("entityClass");
		String entityName = (String)convertedMessage.get("entityName");
		Session session = (Session)convertedMessage.get("session");
		
		try {
			uploadFormattedImages(id, propertyToFilePath, daoClass, entityClass, entityName, session);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Params:
	 * 	id: id da entidade a receber o upload de foto
	 * 	propertyToFilePath: propriedade da entidade que contém o file path da foto
	 */
	private void uploadFormattedImages(Serializable id, String propertyToFilePath, Class<?> daoClass, Class<?> entityClass, String entityName, Session session) throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		try{
			
			Object dao = applicationContext.getBean(daoClass.getInterfaces()[0]);
			dao = daoClass.getInterfaces()[0].cast(dao);
			Object connectedEntity = ((DAO<?>)dao).findById(id);
			connectedEntity = entityClass.cast(connectedEntity);
//			Field f = entityClass.getDeclaredField(propertyToFilePath);
//			f.setAccessible(true);
//			String srcImagePath = (String)f.get(connectedEntity);
			String srcImagePath = BeanUtils.getProperty(connectedEntity, propertyToFilePath);
	//		PropertyDescriptor pd = connectedEntity.get(propertyToFilePath);
	//		Map<String, PropertyDescriptor> entityProperties = org.springframework.beans.BeanUtils.getPropertyDescriptors(entityClass);
			PropertyDescriptor pd = org.springframework.beans.BeanUtils.getPropertyDescriptor(entityClass, propertyToFilePath);
			
			InputStream srcImageInputStream = new FileInputStream(new File(uploadDirectory + srcImagePath));
			
			BufferedImage tmpImg = ImageIO.read(srcImageInputStream);
			BufferedImage srcImg = null;
			if(pd != null){
				Image imageAnnotation = pd.getReadMethod().getAnnotation(Image.class);
				String oldValue = (String) BeanUtils.getProperty(connectedEntity, propertyToFilePath);
				
				// se tiver transparencia com anotação de noTransparency, pinta o fundo
				if (imageAnnotation.noTransparency() && 
						(tmpImg.getType() == BufferedImage.TYPE_INT_ARGB || tmpImg.getType() == BufferedImage.TYPE_4BYTE_ABGR)) {
					srcImg = new BufferedImage(tmpImg.getWidth(), tmpImg.getHeight(), BufferedImage.TYPE_INT_RGB);
					String transpColor = imageAnnotation.transparencyColor();
					Color bgColor = Color.decode(transpColor);
					srcImg.createGraphics().drawImage(tmpImg, 0, 0, bgColor, null);
					tmpImg.flush();
				} else if (tmpImg.getTransparency() == Transparency.OPAQUE && tmpImg.getType() != BufferedImage.TYPE_INT_RGB) {
					srcImg = new BufferedImage(tmpImg.getWidth(), tmpImg.getHeight(), BufferedImage.TYPE_INT_RGB);
					srcImg.createGraphics().drawImage(tmpImg, 0, 0, null);
					tmpImg.flush();
				} else if (tmpImg.getTransparency() != Transparency.OPAQUE && tmpImg.getType() != BufferedImage.TYPE_INT_ARGB) {
					srcImg = new BufferedImage(tmpImg.getWidth(), tmpImg.getHeight(), BufferedImage.TYPE_INT_ARGB);
					srcImg.createGraphics().drawImage(tmpImg, 0, 0, null);
					tmpImg.flush();
				} else {
					srcImg = tmpImg;
				}
				
				BufferedImage resizedImg = null;
				BufferedImage croppedImg = null;
				try {
					String fileSuffix;
					String outputContentType;
					if(srcImg.getType() == BufferedImage.TYPE_INT_RGB) {
						outputContentType ="image/jpeg"; fileSuffix = ".jpg";
					} else if (srcImg.getType() == BufferedImage.TYPE_INT_ARGB) {
						outputContentType ="image/png"; fileSuffix = ".png";
					} else {
						throw new RuntimeException("Invalid image type " + srcImg.getType());
					}
					
					Mode resizeMode = (((double) srcImg.getHeight() / (double) srcImg.getWidth()) < ((double) imageAnnotation.targetHeight() / (double) imageAnnotation
							.targetWidth())) ? Mode.FIT_TO_HEIGHT : Mode.FIT_TO_WIDTH;
					
					if (imageAnnotation.maxHeight() != 0 || imageAnnotation.maxWidth() != 0) {
						int maxWidth = imageAnnotation.maxWidth();
						int maxHeight = imageAnnotation.maxHeight();
						if (srcImg.getWidth() < (maxWidth == 0? Integer.MAX_VALUE : maxWidth) && srcImg.getHeight() < (maxHeight == 0? Integer.MAX_VALUE : maxHeight)) {
							resizedImg = croppedImg = srcImg;
						} else {
							resizedImg = croppedImg = Scalr.resize(srcImg, Scalr.Method.ULTRA_QUALITY, Mode.AUTOMATIC, (maxWidth == 0? Integer.MAX_VALUE : maxWidth), (maxHeight == 0? Integer.MAX_VALUE : maxHeight));
						}
					} else {											
						resizedImg = Scalr.resize(srcImg, org.imgscalr.Scalr.Method.ULTRA_QUALITY, resizeMode, imageAnnotation.targetWidth(),
								imageAnnotation.targetHeight());
	
						int cropStartX = Math.max((resizedImg.getWidth() - imageAnnotation.targetWidth()) / 2, 0);
						int cropStartY = Math.max((resizedImg.getHeight() - imageAnnotation.targetHeight()) / 2, 0);
	
						croppedImg = Scalr.crop(resizedImg, cropStartX, cropStartY, imageAnnotation.targetWidth(), imageAnnotation.targetHeight());
					}
					String uploadKey = uploadManager.saveImage(croppedImg, outputContentType, propertyToFilePath + fileSuffix, entityName + "/" + id);
					if (oldValue != null && !oldValue.equals(uploadKey)) {
						uploadManager.deleteFile(oldValue);
					}
					BeanUtils.setProperty(connectedEntity, propertyToFilePath, uploadKey);
	
					for (String thumbProperty : imageAnnotation.thumbnail()) {
						// thumbnail
						PropertyDescriptor thumbPd = org.springframework.beans.BeanUtils.getPropertyDescriptor(entityClass, thumbProperty);
						if (thumbPd != null) {
							String oldThumbValue = (String) BeanUtils.getProperty(connectedEntity, thumbProperty);
	
							Image thumbAnnotation = thumbPd.getReadMethod().getAnnotation(Image.class);
							int thumbWidth = thumbAnnotation != null ? thumbAnnotation.targetWidth() : 100;
							int thumbHeight = thumbAnnotation != null ? thumbAnnotation.targetHeight() : 100;
	
							resizeMode = ((double) croppedImg.getHeight() / (double) croppedImg.getWidth()) < ((double) thumbHeight / (double) thumbWidth) ? Mode.FIT_TO_HEIGHT
									: Mode.FIT_TO_WIDTH;
	
							BufferedImage thumbImg = null;
							BufferedImage croppedThumbImg = null;
							try {
								thumbImg = Scalr.resize(croppedImg, org.imgscalr.Scalr.Method.ULTRA_QUALITY, resizeMode, thumbWidth, thumbHeight);
								int thumbCropStartX = (thumbImg.getWidth() - thumbWidth) / 2;
								int thumbCropStartY = (thumbImg.getHeight() - thumbHeight) / 2;
	
								croppedThumbImg = Scalr.crop(thumbImg, thumbCropStartX, thumbCropStartY, thumbWidth, thumbHeight);
								String thumbUpKey = uploadManager.saveImage(croppedThumbImg, "img/jpeg", thumbProperty + ".jpg", entityName + "/" + id);
								if (oldThumbValue != null && !oldThumbValue.equals(thumbUpKey)) {
									uploadManager.deleteFile(oldThumbValue);
								}
	
								BeanUtils.setProperty(connectedEntity, thumbProperty, thumbUpKey);
							} finally {
								if (thumbImg != null)
									resizedImg.flush();
								if (croppedThumbImg != null)
									croppedThumbImg.flush();
							}
						}
					}
					session.update(connectedEntity);
					session.flush();
//					activityLogService.logEntityPropertyUpdate((BaseEntity<?>) connectedEntity, new UpdatedProperty(propertyToFilePath, oldValue, uploadKey, true));
				} finally {
					// limpa (?) dados da memória - TODO estudar mais
					if (srcImg != null)
						srcImg.flush();
					if (resizedImg != null)
						resizedImg.flush();
					if (croppedImg != null)
						croppedImg.flush();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

}
