package dwf.upload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface UploadManager {
	
	/**
	 * 
	 * @param is
	 * @param contentType
	 * @param fileName
	 * @param folderName
	 * @return
	 * @throws IOException
	 */
	String saveFile(InputStream is, String contentType, String fileName, String folderName) throws IOException;
	String saveFile(File file, String contentType, String fileName, String folderName) throws IOException;
	String remoteUrl(String uploadKey);
	void deleteFile(String uploadKey);
	InputStream openInputStream(String uploadKey);
}
