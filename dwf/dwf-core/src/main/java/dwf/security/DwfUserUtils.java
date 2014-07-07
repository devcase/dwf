package dwf.security;

import org.springframework.security.access.intercept.RunAsUserToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import dwf.user.domain.User;

public class DwfUserUtils {
	public static User getCurrentUser() {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		if(securityContext != null && securityContext.getAuthentication() != null && securityContext.getAuthentication().isAuthenticated() && securityContext.getAuthentication().getPrincipal() instanceof User) {
			return (User) securityContext.getAuthentication().getPrincipal();
		}
		return null;
	}
	
	public static void changeCurrentUser(User newUser) {
		SecurityContext securityContext = SecurityContextHolder.getContext();
		securityContext.setAuthentication(new RunAsUserToken(newUser.getHashedPassword(), newUser, securityContext.getAuthentication().getCredentials(), securityContext.getAuthentication().getAuthorities(), securityContext.getAuthentication().getClass()));
	}
}
