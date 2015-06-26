package dwf.upload;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

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
	@Deprecated
	String saveImage(RenderedImage image, String contentType, String fileName, String folderName) throws IOException;
	String saveImage(InputStream is, int targetWidth, int targetHeight, int maxWidth, int maxHeight, boolean noTransparency, String transparencyReplaceColor, String propertyName, String folderName) throws IOException;
	String remoteUrl(String uploadKey);
	void deleteFile(String uploadKey);
	InputStream getOriginalImageInputStream(String uploadKey);
	void saveThumbnail(Serializable id, String propertyToFilePath, Class<?> daoClass, Class<?> entityClass, String entityName) throws Exception;
}
