package dwf.web.upload;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import dwf.upload.S3UploadManager;

@RequestMapping // responds to requests via /dl/**
public class S3DownloadEndpoint implements DownloadEndpoint {
	
	@Autowired
	private S3UploadManager s3UploadManager;
	
	public S3DownloadEndpoint() {
		super();
	}
	
	@RequestMapping("/dl/**")
	public void redirectToFile(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		if(request.getServletPath().startsWith("/dl/")) {
			String key = request.getServletPath().substring("/dl/".length());
			String remoteURL = s3UploadManager.remoteUrl(key);
			if(remoteURL != null) {
				response.sendRedirect(remoteURL);
				return;
			}
		} else {
			response.sendError(404);
		}
	}
}

