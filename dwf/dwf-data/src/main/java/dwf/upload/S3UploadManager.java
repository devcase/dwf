package dwf.upload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.SecureRandom;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;

public class S3UploadManager implements UploadManager {

	protected final AmazonS3Client awsS3Client;
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
	
	public S3UploadManager(String accessKey, String secretKey) {
		super();
		this.awsS3Client = new AmazonS3Client(new BasicAWSCredentials(accessKey, secretKey));
	}
	
	protected String generateKey(String fileName, String folderName) {
		if(folderName == null) folderName ="default";
		
		String randomString = new BigInteger(16, random).toString(32);
		
		if(fileName == null) return String.valueOf(System.currentTimeMillis());
		
		return (folderName.startsWith("/") ? folderName.substring(1) : folderName )  + (folderName.endsWith("/") ? "" : "/") + randomString + fileName;
	}

	@Override
	public String saveFile(InputStream is, String contentType, String fileName, String folderName) throws IOException {
		try {
			ObjectMetadata metadata = new ObjectMetadata();
			metadata.setContentType(contentType);
			String key = generateKey(fileName, folderName);
			
			// sets ACL
			AccessControlList acl = new AccessControlList();
			acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
	
			awsS3Client.putObject(new PutObjectRequest(getBucketName(), key, is, metadata).withAccessControlList(acl));
			return key;
		} finally {
			try { is.close(); } catch (Exception ignore) {}
		}
	}
	
	@Override
	public String saveFile(File file, String contentType, String fileName, String folderName) throws IOException {
		String key = generateKey(fileName, folderName);
		
		// sets ACL
		AccessControlList acl = new AccessControlList();
		acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);

		awsS3Client.putObject(new PutObjectRequest(getBucketName(), key, file).withAccessControlList(acl));
		return key;
	}

	@Override
	public String remoteUrl(String key) {
		if(key != null && key.startsWith("/")) key= key.substring(1);
		return awsS3Client.getResourceUrl(getBucketName(), key);
	}

	/* (non-Javadoc)
	 * @see dwf.upload.UploadManager#deleteFile(java.lang.String)
	 */
	@Override
	public void deleteFile(String uploadKey) {
		awsS3Client.deleteObject(getBucketName(), uploadKey);
	}
	
	public InputStream openInputStream(String uploadKey) {
		return awsS3Client.getObject(getBucketName(), uploadKey).getObjectContent();
	}
	
}

