package dwf.web.user.utils;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

@Component
public class WebSecurityUtils {
	
	@Autowired(required=false)
	private AuthenticationManager authenticationManager;
	
	public void authenticate(String username, String password, HttpServletRequest request) {
        // Must be called from request filtered by Spring Security, otherwise SecurityContextHolder is not updated
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        token.setDetails(new WebAuthenticationDetails(request));
        
        Authentication authentication = authenticationManager.authenticate(token);
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!" + authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
