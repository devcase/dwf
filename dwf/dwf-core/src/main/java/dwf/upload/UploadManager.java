package dwf.upload;

import java.awt.image.RenderedImage;
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
	String saveImage(RenderedImage image, String contentType, String fileName, String folderName) throws IOException;
	String remoteUrl(String uploadKey);
}
