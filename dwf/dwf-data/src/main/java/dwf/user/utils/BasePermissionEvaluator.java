package dwf.user.utils;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import dwf.user.domain.LoggedUserDetails;

/**
 * 
 * @author Hirata
 *
 */
public abstract class BasePermissionEvaluator implements PermissionEvaluator {
    
	protected LoggedUserDetails getLoggedUserDetails(Authentication authentication) {
    	if (authentication == null) {
    		return null;
    	} else if(authentication instanceof LoggedUserDetails) {
    		return (LoggedUserDetails) authentication;
    	} else if(authentication.getPrincipal() instanceof LoggedUserDetails) {
        		return (LoggedUserDetails) authentication.getPrincipal();
    	} else {
    		return null;
    	}
    }
    
    protected Long getLoggedUserId(Authentication authentication) {
    	LoggedUserDetails l = getLoggedUserDetails(authentication);
    	if(l != null) return l.getId();
    	else return null;
    }

}
