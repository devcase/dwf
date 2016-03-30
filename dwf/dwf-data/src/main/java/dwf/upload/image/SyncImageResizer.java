package dwf.upload.image;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.annotation.Transactional;

import dwf.persistence.annotations.Image;
import dwf.persistence.dao.DAO;
import dwf.upload.UploadManager;
import net.coobird.thumbnailator.util.exif.ExifFilterUtils;
import net.coobird.thumbnailator.util.exif.ExifUtils;
import net.coobird.thumbnailator.util.exif.Orientation;

@Transactional
public class SyncImageResizer implements ImageResizer {
	private Log log = LogFactory.getLog(getClass());
//	@Autowired
//	private SessionFactory sessionFactory;
	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private UploadManager uploadManager;
	

	@Override
	public void resizeImage(Serializable id, String entityName, String property) throws IOException {
//		Session session = sessionFactory.getCurrentSession();
		DAO<?> dao = (DAO<?>) applicationContext.getBean(entityName + "DAO");
		Object connectedEntity = dao.retrieveCopy(id);
		Class<?> entityClass = dao.getEntityClass();
		
		PropertyDescriptor pd = org.springframework.beans.BeanUtils.getPropertyDescriptor(entityClass, property);
		if(pd == null) {
			//não encontrei informação da propriedade
			throw new RuntimeException(property + " is not a valid property for " + entityName); 
		}
		
		InputStream srcImageInputStream = null;
		ImageInputStream iis = null;
		BufferedImage commonSourceImage = null;

		
		try {
			String originalImageKey = BeanUtils.getProperty(connectedEntity, property);
			if(StringUtils.isBlank(originalImageKey)) {
				throw new RuntimeException("Value not found in " + entityName + "." + property + " for id " + id);
			}
			srcImageInputStream = uploadManager.openInputStream(originalImageKey);
			iis = ImageIO.createImageInputStream(srcImageInputStream); 
			//reads original file into a bufferedimage, process exif orientation, processing exif orientation info
			Iterator<ImageReader> imageReaders = ImageIO.getImageReaders(iis);
			if(!imageReaders.hasNext()) {
				//não é possível ler a imagem
				srcImageInputStream.close();
				srcImageInputStream = null;
				throw new RuntimeException("ImageReader not found for file in " + entityName + "." + property + " for id " + id + " " + originalImageKey);
			}
			
//			srcImg = Thumbnails.of(srcImageInputStream).useExifOrientation(true).asBufferedImage();
			ImageReader imageReader = imageReaders.next();
			if(log.isDebugEnabled()) {
				log.debug("ImageReader found " + imageReader );
				log.debug("FormatName " + imageReader.getFormatName() );
			}
			imageReader.setInput(iis);
			
			Orientation orientation = imageReader.getFormatName().equals("JPEG") ?  ExifUtils.getExifOrientation(imageReader, 0) : null;
			commonSourceImage = imageReader.read(0);
			srcImageInputStream.close();
			srcImageInputStream = null;
			iis.close();
			iis = null;
			
			if (orientation != null && orientation != Orientation.TOP_LEFT)
			{
				BufferedImage rotatedImg = ExifFilterUtils.getFilterForOrientation(orientation).apply(commonSourceImage);
				commonSourceImage.flush();
				commonSourceImage = rotatedImg;
			}
			
			Image mainPropertyImageAnnotation = pd.getReadMethod().getAnnotation(Image.class);
			
			
			// array com as propriedades - propriedade principal e propriedade dos thumbnails
			String[] propertyNames = new String[1 + mainPropertyImageAnnotation.thumbnail().length];  
			propertyNames[0] = property;
			if(mainPropertyImageAnnotation.thumbnail().length > 0) {
				System.arraycopy(mainPropertyImageAnnotation.thumbnail(),0, propertyNames,1,mainPropertyImageAnnotation.thumbnail().length); 
			}

			for(String propertyName : propertyNames) {
				BufferedImage preScaleImage = null;
				BufferedImage resizedImg = null;
				BufferedImage finalImage = null;
				
				try {
					PropertyDescriptor currentPD = org.springframework.beans.BeanUtils.getPropertyDescriptor(entityClass, propertyName);
					Image imageAnnotation = currentPD.getReadMethod().getAnnotation(Image.class);
					
					
					// faz a conversão necessária entre imagem transparente e imagem não transparente
					if (commonSourceImage.getTransparency() != Transparency.OPAQUE &&
							imageAnnotation.noTransparency()) {
						preScaleImage = new BufferedImage(commonSourceImage.getWidth(), commonSourceImage.getHeight(), BufferedImage.TYPE_INT_RGB);
						String transpColor = imageAnnotation.transparencyColor();
						Color bgColor = Color.decode(transpColor);
						Graphics g = preScaleImage.createGraphics();
						g.drawImage(commonSourceImage, 0, 0, bgColor, null);
						g.dispose();
					} else if (commonSourceImage.getTransparency() != Transparency.OPAQUE && 
							commonSourceImage.getType() != BufferedImage.TYPE_INT_ARGB) {
						preScaleImage = new BufferedImage(commonSourceImage.getWidth(), commonSourceImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
						Graphics g = preScaleImage.createGraphics();
						g.drawImage(commonSourceImage, 0, 0, null);
						g.dispose();
					} else {
						preScaleImage = commonSourceImage;
					}
					
					String fileSuffix;
					String outputContentType;
					if(preScaleImage.getTransparency() == Transparency.OPAQUE) {
						outputContentType ="image/jpeg"; fileSuffix = ".jpg";
					} else if (preScaleImage.getType() == BufferedImage.TYPE_INT_ARGB) {
						outputContentType ="image/png"; fileSuffix = ".png";
					} else {
						throw new RuntimeException("Invalid image type " + preScaleImage.getType());
					}
					
					
					
					if (imageAnnotation.maxHeight() != 0 || imageAnnotation.maxWidth() != 0) {
						int maxWidth = imageAnnotation.maxWidth();
						int maxHeight = imageAnnotation.maxHeight();
						if (preScaleImage.getWidth() < (maxWidth == 0? Integer.MAX_VALUE : maxWidth) && preScaleImage.getHeight() < (maxHeight == 0? Integer.MAX_VALUE : maxHeight)) {
							finalImage = preScaleImage;
						} else {
							finalImage = Scalr.resize(preScaleImage, Scalr.Method.ULTRA_QUALITY, Mode.AUTOMATIC, (maxWidth == 0? Integer.MAX_VALUE : maxWidth), (maxHeight == 0? Integer.MAX_VALUE : maxHeight));
						}
						preScaleImage.flush();
						preScaleImage = null;
					} else if(imageAnnotation.fitMode() == Image.FIT_INSIDE) {
						//Faz resize da propriedade principal - e joga em croppedImg
						resizedImg = Scalr.resize(preScaleImage, org.imgscalr.Scalr.Method.ULTRA_QUALITY, Mode.AUTOMATIC, imageAnnotation.targetWidth(),
								imageAnnotation.targetHeight());
						preScaleImage.flush();
						preScaleImage = null;
						
						if(resizedImg.getWidth() == imageAnnotation.targetWidth() && resizedImg.getHeight() == imageAnnotation.targetHeight()) {
							finalImage = resizedImg;
							resizedImg = null;
						} else {
							int x = (imageAnnotation.targetWidth() - resizedImg.getWidth()) / 2;
							int y = (imageAnnotation.targetHeight() - resizedImg.getHeight()) / 2;
							Graphics g;
							
							if(resizedImg.getTransparency() == Transparency.OPAQUE) {
								finalImage = new BufferedImage(imageAnnotation.targetWidth(), imageAnnotation.targetHeight(), BufferedImage.TYPE_INT_RGB);
								Color bgColor = Color.WHITE;
								String transpColor = imageAnnotation.transparencyColor();
								if(transpColor != null) {
									bgColor = Color.decode(transpColor);
								}

								g = finalImage.getGraphics();
								g.setColor(bgColor);
								g.fillRect(0, 0, imageAnnotation.targetWidth(), imageAnnotation.targetHeight());
								g.drawImage(resizedImg, x, y, bgColor, null);
								g.dispose();
							} else {
								finalImage = new BufferedImage(imageAnnotation.targetWidth(), imageAnnotation.targetHeight(), BufferedImage.TYPE_INT_ARGB);
								g = finalImage.getGraphics();
								g.setColor(new Color(0, 0, 0, 0));
								g.fillRect(0, 0, imageAnnotation.targetWidth(), imageAnnotation.targetHeight());								
								g.drawImage(resizedImg, x, y, null);
								g.dispose();
							}
							resizedImg.flush();
							resizedImg = null;
						}
					} else {
						//Faz resize da propriedade principal - e joga em croppedImg
						Mode resizeMode = (((double) preScaleImage.getHeight() / (double) preScaleImage.getWidth()) < ((double) imageAnnotation.targetHeight() / (double) imageAnnotation
								.targetWidth())) ? Mode.FIT_TO_HEIGHT : Mode.FIT_TO_WIDTH;
						
						resizedImg = Scalr.resize(preScaleImage, org.imgscalr.Scalr.Method.ULTRA_QUALITY, resizeMode, imageAnnotation.targetWidth(),
								imageAnnotation.targetHeight());
						preScaleImage.flush();
						preScaleImage = null;
	
						int cropStartX = Math.max((resizedImg.getWidth() - imageAnnotation.targetWidth()) / 2, 0);
						int cropStartY = Math.max((resizedImg.getHeight() - imageAnnotation.targetHeight()) / 2, 0);
	
						finalImage = Scalr.crop(resizedImg, cropStartX, cropStartY, imageAnnotation.targetWidth(), imageAnnotation.targetHeight());
						resizedImg.flush();
						resizedImg = null;
					}
					
					//Grava a imagem no tamanho certo
					String uploadKey = uploadManager.saveFile(saveImageAsTempFile(finalImage, outputContentType), outputContentType, propertyName + fileSuffix, entityName + "/" + id);
					//Atualiza a entidade
					BeanUtils.setProperty(connectedEntity, propertyName, uploadKey);
					dao.setProperty(id, propertyName, uploadKey);
				} finally {
					if (resizedImg != null) {
						try {
							resizedImg.flush();
						} catch(Throwable ignore) {}
					}
					if (finalImage != null) {
						try {
							finalImage.flush();
						} catch(Throwable ignore) {}
					}
					if(preScaleImage != null) {
						try {
							preScaleImage.flush();
						} catch(Throwable ignore) {}
					}
				}
			}
			
			uploadManager.deleteFile(originalImageKey);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		} finally {
			if(iis != null) {
				try {
					iis.close();
				} catch(Throwable ignore) {}
			}
			if(srcImageInputStream != null) {
				try {
					srcImageInputStream.close();
				} catch(Throwable ignore) {}
			}
			if(commonSourceImage != null) { 
				try {
					commonSourceImage.flush();
				} catch(Throwable ignore) {}
			}
		}
	}
	
	
	public static File saveImageAsTempFile(RenderedImage image, String contentType) throws IOException {
		String formatName = "jpeg";
		boolean isJpeg = "image/jpeg".equals(contentType); 
		if(!isJpeg)
			formatName = "png";

		File tmpFile = File.createTempFile("tup-tmp", formatName.equals("jpeg") ? ".jpg" : "." + formatName);
		
		tmpFile.deleteOnExit();
		ImageOutputStream ios = ImageIO.createImageOutputStream(tmpFile);
		Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName(formatName);
		ImageWriter writer = iter.next();
		ImageWriteParam iwp = writer.getDefaultWriteParam();
		if(isJpeg) {
			iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			iwp.setCompressionQuality(0.95f);
		}
		writer.setOutput(ios);
		writer.write(null, new IIOImage(image, null, null), iwp);
		writer.dispose();
		return tmpFile;
	}


}
