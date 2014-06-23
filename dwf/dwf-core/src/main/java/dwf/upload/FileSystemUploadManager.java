package dwf.upload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

@Controller
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public abstract class FileSystemUploadManager extends WebApplicationObjectSupport implements UploadManager, InitializingBean, ApplicationContextAware {
	

	private ResourceHttpRequestHandler resourceHttpRequestHandler;
	
	
	protected abstract String getDirectory();
	
	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		resourceHttpRequestHandler = new ResourceHttpRequestHandler();
		resourceHttpRequestHandler.setApplicationContext(this.getApplicationContext());
		FileSystemResourceLoader resourceLoader = new FileSystemResourceLoader();
		Resource fileDir = resourceLoader.getResource(getDirectory());
		resourceHttpRequestHandler.setLocations(Collections.singletonList(fileDir));
	}



	@Override
	public String saveFile(InputStream is, String contentType, String fileName, String folderName) throws IOException {
		File rootDir = new File(getDirectory());
		if(!rootDir.exists()) {
			rootDir.mkdir();
		} else if(!rootDir.isDirectory()) {
			throw new IOException("Caminho de diretório configurado não é válido! " + getDirectory());
		}
		
		File folder = new File(rootDir, folderName);
		if(!folder.exists()) {
			folder.mkdir();
		} else if(!folder.isDirectory()) {
			throw new IOException("Caminho de diretório configurado não é válido! " + folder.getAbsolutePath());
		}
		
		File savedFile = new File(folder, fileName);
		if(savedFile.exists()) {
			savedFile.delete();
		}
		
		FileUtils.copyInputStreamToFile(is, savedFile);
		
		return savedFile.getAbsolutePath().substring(rootDir.getAbsolutePath().length()).replaceAll("\\\\", "\\/");
	}

	/* (non-Javadoc)
	 * @see dwf.upload.UploadManager#remoteUrl(java.lang.String)
	 */
	@Override
	public String remoteUrl(String uploadKey) {
		return getWebApplicationContext().getServletContext().getContextPath() + "/upload" + uploadKey;
	}
	
	@RequestMapping("/upload/**")
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		request.setAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, request.getServletPath().substring("/upload".length()));
		resourceHttpRequestHandler.handleRequest(request, response);
	}

}
