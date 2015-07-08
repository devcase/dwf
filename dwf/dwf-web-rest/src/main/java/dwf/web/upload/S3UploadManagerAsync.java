package dwf.web.upload;

import java.awt.Color;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.hibernate.SessionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;

@RequestMapping // responds to requests via /dl/**
public class S3UploadManagerAsync extends S3UploadManager {
	
	@Autowired
	private SessionFactory sessionFactory;
	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	private SecureRandom random = new SecureRandom();
	
	private String queueName;
	
	public S3UploadManagerAsync(String accessKey, String secretKey, String queueName) {
		super(accessKey, secretKey);
		this.queueName = queueName;
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
		
		rabbitTemplate.convertAndSend(this.queueName, imageProcParams);

	}
	
	@Override
	public String saveImage(InputStream is, int targetWidth, int targetHeight, int maxWidth, int maxHeight, boolean noTransparency,
			String transparencyReplaceColor, String propertyName, String folderName) throws IOException {
		
		BufferedImage tmpImg = ImageIO.read(is);
		BufferedImage srcImg = null;
		// se tiver transparencia com anotação de noTransparency, pinta o fundo
		if (noTransparency &&  
				(tmpImg.getType() == BufferedImage.TYPE_INT_ARGB || tmpImg.getType() == BufferedImage.TYPE_4BYTE_ABGR)) {
			srcImg = new BufferedImage(tmpImg.getWidth(), tmpImg.getHeight(), BufferedImage.TYPE_INT_RGB);
			String transpColor = transparencyReplaceColor;
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
		
		String fileSuffix;
		String outputContentType;
		
		if(srcImg.getType() == BufferedImage.TYPE_INT_RGB) {
			outputContentType ="image/jpeg"; fileSuffix = ".jpg";
		} else if (srcImg.getType() == BufferedImage.TYPE_INT_ARGB) {
			outputContentType ="image/png"; fileSuffix = ".png";
		} else {
			srcImg.flush();
			throw new RuntimeException("Invalid image type " + srcImg.getType());
		}
		
		File tmpFile = AbstractUploadManager.saveImageAsTempFile(srcImg, outputContentType);
		
		String randomString = new BigInteger(16, random).toString(32);
		String fileName = propertyName + randomString + "." + FilenameUtils.getExtension(tmpFile.getName());
		
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(outputContentType);
		String key = generateKey(fileName, folderName);
		
		// sets ACL
		AccessControlList acl = new AccessControlList();
		acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);

		super.awsS3Client.putObject(new PutObjectRequest(getBucketName(), key, tmpFile).withAccessControlList(acl));
		
		return key;
	}
	
}
