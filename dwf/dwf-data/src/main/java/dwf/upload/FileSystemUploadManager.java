package dwf.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.SecureRandom;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class FileSystemUploadManager implements InitializingBean, UploadManager {
	
	@Autowired
	private ApplicationContext applicationContext;
	
	private SecureRandom random = new SecureRandom();

	private String directory;
	
	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		
		File rootDir = new File(getDirectory());
		if(!rootDir.exists()) {
			rootDir.mkdir();
		} else if(!rootDir.isDirectory()) {
			throw new IOException("Caminho de diretório configurado não é válido! " + getDirectory());
		}
	}


	@Override
	public String saveFile(InputStream is, String contentType, String fileName, String folderName) throws IOException {
		try {
			String randomString = new BigInteger(16, random).toString(32);
			fileName = randomString + fileName;
			File savedFile = getFileDestination(fileName, folderName);
			
			FileUtils.copyInputStreamToFile(is, savedFile);
			
			return (folderName.startsWith("/") ? "" : "/")   + folderName + (folderName.endsWith("/") ? "" : "/") + fileName;
		} finally {
			try { is.close(); } catch (Exception ignore) {}
		}
	}
	
	@Override
	public String saveFile(File file, String contentType, String fileName, String folderName) throws IOException {
		String randomString = new BigInteger(16, random).toString(32);
		fileName = randomString + fileName;
		File savedFile = getFileDestination(fileName, folderName);
		
		FileUtils.copyFile(file, savedFile);
		
		return (folderName.startsWith("/") ? "" : "/")   + folderName + (folderName.endsWith("/") ? "" : "/") + fileName;
	}

	protected File getFileDestination(String fileName, String folderName) throws IOException {
		File rootDir = new File(getDirectory().endsWith("/") ? getDirectory() : getDirectory() + "/");
		if(!rootDir.exists()) {
			rootDir.mkdir();
		} else if(!rootDir.isDirectory()) {
			throw new IOException("Caminho de diretório configurado não é válido! " + getDirectory());
		}
		
		File folder = new File(rootDir, folderName);
		
		if(!folder.exists()) {
			folder.mkdirs();
		} else if(!folder.isDirectory()) {
			throw new IOException("Caminho de diretório configurado não é válido! " + folder.getAbsolutePath());
		}
		
		
		File savedFile = new File(folder, fileName);
		if(savedFile.exists()) {
			savedFile.delete();
		}
		return savedFile;
	}

	/* (non-Javadoc)
	 * @see dwf.upload.UploadManager#remoteUrl(java.lang.String)
	 */
	@Override
	public String remoteUrl(String uploadKey) {
		return  "/dl" + (uploadKey.startsWith("/") ? "" : "/") + uploadKey;
	}
	
	
	/* (non-Javadoc)
	 * @see dwf.upload.UploadManager#deleteFile(java.lang.String)
	 */
	@Override
	public void deleteFile(String uploadKey) {
		File rootDir = new File(getDirectory().endsWith("/") ? getDirectory() : getDirectory() + "/");
		File deletedFile = new File(rootDir, uploadKey);
		if(deletedFile.exists()) {
			deletedFile.delete();
		}
	}

	@Override
	public InputStream openInputStream(String uploadKey) {
		try {
			return new FileInputStream(new File(directory + uploadKey));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
}
