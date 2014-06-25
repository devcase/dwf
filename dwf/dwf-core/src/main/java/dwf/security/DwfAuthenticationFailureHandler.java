package dwf.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.FlashMapManager;

@Component("dwfAuthenticationFailureHandler")
public class DwfAuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Autowired
	private FlashMapManager flashMapManager;
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.security.web.authentication.AuthenticationFailureHandler
	 * #onAuthenticationFailure(javax.servlet.http.HttpServletRequest,
	 * javax.servlet.http.HttpServletResponse,
	 * org.springframework.security.core.AuthenticationException)
	 */
	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException,
			ServletException {
		
		FlashMap flashMap = new FlashMap();
		flashMap.put("loginErrorMessage", exception.getMessage());
		flashMapManager.saveOutputFlashMap(flashMap, request, response);
		response.sendRedirect(request.getContextPath() + "/login");
	}

}
