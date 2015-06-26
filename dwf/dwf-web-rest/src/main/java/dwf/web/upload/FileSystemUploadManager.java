package dwf.web.upload;

import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import dwf.upload.UploadManagerThumbnail;


@RequestMapping // responds to requests via /dl/**
public class FileSystemUploadManager extends UploadManagerThumbnail implements InitializingBean {
	
	@Autowired
	private ApplicationContext applicationContext;
	
	private SecureRandom random = new SecureRandom();

	private ResourceHttpRequestHandler resourceHttpRequestHandler;
	
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
		resourceHttpRequestHandler = new ResourceHttpRequestHandler();
		resourceHttpRequestHandler.setApplicationContext(applicationContext);
		
		File rootDir = new File(getDirectory());
		if(!rootDir.exists()) {
			rootDir.mkdir();
		} else if(!rootDir.isDirectory()) {
			throw new IOException("Caminho de diretório configurado não é válido! " + getDirectory());
		}
		
		//FileSystemResource fsResource = new FileSystemResource(rootDir); <- não funciona
		FileSystemResource fsResource = new FileSystemResource(getDirectory());
		resourceHttpRequestHandler.setLocations(Collections.singletonList((Resource) fsResource));
		resourceHttpRequestHandler.afterPropertiesSet();
	}

	@Override
	public String saveImage(RenderedImage image, String contentType, String fileName, String folderName) throws IOException {
		String randomString = new BigInteger(16, random).toString(32);
		fileName = randomString + fileName;
		
		File savedFile = getFileDestination(fileName, folderName);
		
		ImageOutputStream ios = ImageIO.createImageOutputStream(savedFile);
		String formatName = "jpeg";
		boolean isJpeg = "image/jpeg".equals(contentType); 
		if(!isJpeg)
			formatName = "png";
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
		ios.close();
		return (folderName.startsWith("/") ? "" : "/")   + folderName + (folderName.endsWith("/") ? "" : "/") + fileName;
	}
	

	@Override
	public String saveImage(InputStream is, int targetWidth, int targetHeight, int maxWidth, int maxHeight, boolean noTransparency,
			String transparencyReplaceColor, String propertyName, String folderName) throws IOException {
		
		File tmpFile = AbstractUploadManager.resizeImageAndSaveAsTempFile(is, targetWidth, targetHeight, maxWidth, maxHeight, noTransparency, transparencyReplaceColor);
		
		String randomString = new BigInteger(16, random).toString(32);
		String fileName = propertyName + randomString + "." + FilenameUtils.getExtension(tmpFile.getName());
		
		File savedFile = getFileDestination(fileName, folderName);		
		FileUtils.copyFile(tmpFile, savedFile);
		return (folderName.startsWith("/") ? "" : "/")   + folderName + (folderName.endsWith("/") ? "" : "/") + fileName;
	}

	@Override
	public String saveFile(InputStream is, String contentType, String fileName, String folderName) throws IOException {
		String randomString = new BigInteger(16, random).toString(32);
		fileName = randomString + fileName;
		File savedFile = getFileDestination(fileName, folderName);
		
		FileUtils.copyInputStreamToFile(is, savedFile);
		
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


	@RequestMapping("/dl/**")
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		if(request.getServletPath().startsWith("/dl/")) {
			request.setAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, request.getServletPath().substring("/dl/".length()));
			resourceHttpRequestHandler.handleRequest(request, response);
		}
	}

	@Override
	public InputStream getOriginalImageInputStream(String uploadKey) {
		try {
			return new FileInputStream(new File(directory + uploadKey));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
}
