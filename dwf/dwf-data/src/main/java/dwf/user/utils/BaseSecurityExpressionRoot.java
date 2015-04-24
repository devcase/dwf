package dwf.user.utils;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import dwf.user.domain.LoggedUserDetails;

/**
 * 
 * @author Hirata
 *
 */
public class BaseSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {
    protected Object filterObject;
    protected Object returnObject;
    protected Object target;

    public BaseSecurityExpressionRoot(Authentication a) {
        super(a);
    }

    public void setFilterObject(Object filterObject) {
        this.filterObject = filterObject;
    }

    public Object getFilterObject() {
        return filterObject;
    }

    public void setReturnObject(Object returnObject) {
        this.returnObject = returnObject;
    }

    public Object getReturnObject() {
        return true;
    }

    /**
     * Sets the "this" property for use in expressions. Typically this will be the "this" property of
     * the {@code JoinPoint} representing the method invocation which is being protected.
     *
     * @param target the target object on which the method in is being invoked.
     */
    void setThis(Object target) {
        this.target = target;
    }

    public Object getThis() {
        return target;
    }
    
    public LoggedUserDetails getLoggedUserDetails() {
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
    
    public Long getLoggedUserId() {
    	LoggedUserDetails l = getLoggedUserDetails();
    	if(l != null) return l.getId();
    	else return null;
    }

}
