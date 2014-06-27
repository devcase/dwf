package dwf.security;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import dwf.user.domain.User;

public class DwfUserUtils {
	public static User getCurrentUser() {
		SecurityContext securityContext = SecurityContextHolder .getContext();
		if(securityContext != null && securityContext.getAuthentication() != null && securityContext.getAuthentication().isAuthenticated() && securityContext.getAuthentication().getPrincipal() instanceof User) {
			return (User) securityContext.getAuthentication().getPrincipal();
		}
		return null;
	}
}
