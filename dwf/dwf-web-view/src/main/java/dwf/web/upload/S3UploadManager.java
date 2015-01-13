package dwf.web.upload;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;

import dwf.upload.UploadManager;

public class S3UploadManager implements UploadManager {

	private final AmazonS3Client awsS3Client;
	private SecureRandom random = new SecureRandom();
	private String bucketName;
	
	

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public S3UploadManager() {
		super();
		this.awsS3Client = new AmazonS3Client();
		
	}
	
	protected String generateKey(String fileName, String folderName) {
		if(folderName == null) folderName ="default";
		
		String randomString = new BigInteger(16, random).toString(32);
		
		if(fileName == null) return String.valueOf(System.currentTimeMillis());
		
		return (folderName.startsWith("/") ? folderName.substring(1) : folderName )  + (folderName.endsWith("/") ? "" : "/") + randomString + fileName;
	}

	@Override
	public String saveFile(InputStream is, String contentType, String fileName, String folderName) throws IOException {
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(contentType);
		String key = generateKey(fileName, folderName);
		
		// sets ACL
		AccessControlList acl = new AccessControlList();
		acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);

		awsS3Client.putObject(new PutObjectRequest(getBucketName(), key, is, metadata).withAccessControlList(acl));

		return key;

	}

	@Override
	public String remoteUrl(String key) {
		if(key != null && key.startsWith("/")) key= key.substring(1);
		return awsS3Client.getResourceUrl(getBucketName(), key);
	}

	/* (non-Javadoc)
	 * @see dwf.upload.UploadManager#saveImage(java.awt.image.RenderedImage, java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String saveImage(RenderedImage image, String contentType, String fileName, String folderName) throws IOException {
		String key = generateKey(fileName, folderName);
		// sets ACL
		AccessControlList acl = new AccessControlList();
		acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
		File tmpFile = File.createTempFile("tup-tmp", ".jpg");
		tmpFile.deleteOnExit();
		try {
			ImageOutputStream ios = ImageIO.createImageOutputStream(tmpFile);
			Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
			ImageWriter writer = iter.next();
			ImageWriteParam iwp = writer.getDefaultWriteParam();
			iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			iwp.setCompressionQuality(0.9f);
			writer.setOutput(ios);
			writer.write(null, new IIOImage(image, null, null), iwp);
			writer.dispose();
			awsS3Client.putObject(new PutObjectRequest(getBucketName(), key, tmpFile).withAccessControlList(acl));
			return key;
		} finally {
			tmpFile.delete();
		}
	}

	/* (non-Javadoc)
	 * @see dwf.upload.UploadManager#deleteFile(java.lang.String)
	 */
	@Override
	public void deleteFile(String uploadKey) {
		awsS3Client.deleteObject(getBucketName(), uploadKey);
	}
	
	
	
}
