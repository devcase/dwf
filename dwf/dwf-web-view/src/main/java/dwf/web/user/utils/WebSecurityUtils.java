package dwf.web.user.utils;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import dwf.web.user.NewUserAuthentication;

@Component
public class WebSecurityUtils {
	
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private UserDetailsService userDetailsService; 
	
	public void authenticate(String username, String password, HttpServletRequest request) {
        // Must be called from request filtered by Spring Security, otherwise SecurityContextHolder is not updated
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
        token.setDetails(new WebAuthenticationDetails(request));
        
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
	
	public void changeUser(String username) {
        // Must be called from request filtered by Spring Security, otherwise SecurityContextHolder is not updated
		UserDetails userdetails = userDetailsService.loadUserByUsername(username); 
		if(userdetails != null) {
	        Authentication authentication = new NewUserAuthentication(userdetails);
	        SecurityContextHolder.getContext().setAuthentication(authentication);
		}
    }
}
