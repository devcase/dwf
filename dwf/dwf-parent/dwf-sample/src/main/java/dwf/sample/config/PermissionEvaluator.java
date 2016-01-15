package dwf.sample.config;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import dwf.sample.persistence.dao.CategoryDAO;
import dwf.sample.persistence.domain.Category;
import dwf.user.domain.BaseUserRole;
import dwf.user.utils.BasePermissionEvaluator;

@Component
public class PermissionEvaluator extends BasePermissionEvaluator {
	@Autowired
	private CategoryDAO categoryDAO;
	
	@Override
	public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
		if(hasRole(authentication, BaseUserRole.BACKOFFICE_ADMIN)) {
			return true;
		}
		if(targetDomainObject instanceof Category) {
			return hasPermission(authentication, ((Category) targetDomainObject).getId(), "category", permission); 
		}
		
		return false;
	}

	@Override
	public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
		if(hasRole(authentication, BaseUserRole.BACKOFFICE_ADMIN)) {
			return true;
		}
		if("category".equals(targetType)) {
			//só permite fazer alguma coisa se não for adminOnly
			return categoryDAO.countByFilter("id", targetId, "adminOnly", false) == 1;
		}
		return false;
	}

}
