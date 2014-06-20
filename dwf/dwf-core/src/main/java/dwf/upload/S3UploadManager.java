package dwf.upload;

import java.io.InputStream;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.GroupGrantee;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;

public abstract class S3UploadManager {
	private Random random = new Random();


	protected abstract String getKeyPrefix();
	
	protected abstract String getBucketName();
	
	private final AmazonS3Client awsS3Client;
	
	public S3UploadManager() {
		super();
		this.awsS3Client = new AmazonS3Client();
	}

	protected String generateKey() {
		return getKeyPrefix() + String.valueOf(random.nextLong());
	}

	public String saveFile(InputStream is, String contentType, String hash) {
		ObjectMetadata metadata = new ObjectMetadata();
		metadata.setContentType(contentType);
		if(!StringUtils.isBlank(hash))
			metadata.setContentMD5(hash);
		
		String key = generateKey();
		//sets ACL
		AccessControlList acl = new AccessControlList();
		acl.grantPermission(GroupGrantee.AllUsers, Permission.Read);
		
		awsS3Client.putObject(new PutObjectRequest(getBucketName(), key, is,
				metadata).withAccessControlList(acl));
		
		return key;
		
		
		
	}
	
	public String getDownloadUrl(String key) {
		return awsS3Client.getResourceUrl(getBucketName(), key);
		
	}
}
