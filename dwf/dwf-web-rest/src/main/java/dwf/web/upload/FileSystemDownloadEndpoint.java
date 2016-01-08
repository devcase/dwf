package dwf.web.upload;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import dwf.upload.FileSystemUploadManager;


@RequestMapping // responds to requests via /dl/**
public class FileSystemDownloadEndpoint implements InitializingBean, DownloadEndpoint {
	
	@Autowired
	private ApplicationContext applicationContext;

	private ResourceHttpRequestHandler resourceHttpRequestHandler;
	@Autowired
	private FileSystemUploadManager fileUploadManager;
	
	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		resourceHttpRequestHandler = new ResourceHttpRequestHandler();
		resourceHttpRequestHandler.setApplicationContext(applicationContext);
		
		//FileSystemResource fsResource = new FileSystemResource(rootDir); <- não funciona
		String fileDir = fileUploadManager.getDirectory().trim();
		if(!fileDir.endsWith("/") && !fileDir.endsWith("\\")) {
			fileDir = fileDir + "/";
		}
		FileSystemResource fsResource = new FileSystemResource(fileDir);
		resourceHttpRequestHandler.setLocations(Collections.singletonList((Resource) fsResource));
		resourceHttpRequestHandler.afterPropertiesSet();
	}



	@RequestMapping("/dl/**")
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		if(request.getServletPath().startsWith("/dl/")) {
			request.setAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, request.getServletPath().substring("/dl/".length()));
			resourceHttpRequestHandler.handleRequest(request, response);
			return;
		} else {
			response.sendError(404);
		}
	}

}
