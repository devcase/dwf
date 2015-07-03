package dwf.upload;

import java.awt.Color;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.io.Serializable;

import javax.imageio.ImageIO;

import org.apache.commons.beanutils.BeanUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import dwf.persistence.annotations.Image;
import dwf.persistence.dao.DAO;

public abstract class UploadManagerThumbnail implements UploadManager{
	
//	@Autowired
//	private RabbitTemplate rabbitTemplate;
	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	private ApplicationContext applicationContext;
	
//	public void savedThumbnail(Serializable id, String propertyToFilePath, Class<?> daoClass, Class<?> entityClass, String entityName) throws IOException{
//		Map<String, Serializable> imageProcParams = new HashMap<String, Serializable>();
//		sessionFactory.getCurrentSession().flush();
//		imageProcParams.put("id", id);
//		imageProcParams.put("propertyToFilePath", propertyToFilePath);
//		imageProcParams.put("daoClass", daoClass);
//		imageProcParams.put("entityClass", entityClass);
//		imageProcParams.put("entityName", entityName);
//		
//		rabbitTemplate.convertAndSend("testQueue", imageProcParams);
//		
//	}
	
	
	
	public void saveThumbnail(Serializable id, String propertyToFilePath, Class<?> daoClass, Class<?> entityClass, String entityName) throws Exception{
		Session session;
		try {
			 session = sessionFactory.getCurrentSession();
		} catch (HibernateException ex) {
			//não há sessão aberta
			session = sessionFactory.openSession();
		}
		Object dao = applicationContext.getBean(daoClass.getInterfaces()[0]);
		dao = daoClass.getInterfaces()[0].cast(dao);
		Object connectedEntity = ((DAO<?>)dao).findById(id);
		connectedEntity = entityClass.cast(connectedEntity);
		String srcImagePath = BeanUtils.getProperty(connectedEntity, propertyToFilePath);
		PropertyDescriptor pd = org.springframework.beans.BeanUtils.getPropertyDescriptor(entityClass, propertyToFilePath);
		
		InputStream srcImageInputStream = null;
		BufferedImage tmpImg = null;
		BufferedImage srcImg = null;
		
		try {
			srcImageInputStream = getOriginalImageInputStream(srcImagePath);
			tmpImg = ImageIO.read(srcImageInputStream);
			srcImg = null;
			if(pd != null){
				Image imageAnnotation = pd.getReadMethod().getAnnotation(Image.class);
				String oldValue = (String) BeanUtils.getProperty(connectedEntity, propertyToFilePath);
				
				// se tiver transparencia com anotação de noTransparency, pinta o fundo
				if (imageAnnotation.noTransparency() && 
						tmpImg.getTransparency() != Transparency.OPAQUE) {
					srcImg = new BufferedImage(tmpImg.getWidth(), tmpImg.getHeight(), BufferedImage.TYPE_INT_RGB);
					String transpColor = imageAnnotation.transparencyColor();
					Color bgColor = Color.decode(transpColor);
					srcImg.createGraphics().drawImage(tmpImg, 0, 0, bgColor, null);
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
					if(srcImg.getTransparency() == Transparency.OPAQUE) {
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
					String uploadKey = saveImage(croppedImg, outputContentType, propertyToFilePath + fileSuffix, entityName + "/" + id);
					if (oldValue != null && !oldValue.equals(uploadKey)) {
						deleteFile(oldValue);
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
								String thumbUpKey = saveImage(croppedThumbImg, "img/jpeg", thumbProperty + ".jpg", entityName + "/" + id);
								if (oldThumbValue != null && !oldThumbValue.equals(thumbUpKey)) {
									deleteFile(oldThumbValue);
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
					if (resizedImg != null) {
						try {
							resizedImg.flush();
						} catch(Throwable ignore) {}
					}
					if (croppedImg != null) {
						try {
							croppedImg.flush();
						} catch(Throwable ignore) {}
					}
				}
			}
		} finally {
			if(srcImageInputStream != null) {
				try {
					srcImageInputStream.close();
				} catch(Throwable ignore) {}
			}
			if(tmpImg != null) { 
				try {
					tmpImg.flush();
				} catch(Throwable ignore) {}
			}
			if(srcImg != null) {
				try {
					srcImg.flush();
				} catch(Throwable ignore) {}
			}
		}
		
		
	}
	
}
