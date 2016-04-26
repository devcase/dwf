package dwf.user.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;

import dwf.persistence.dao.BaseDAOImpl;
import dwf.user.domain.BaseUser;
import dwf.user.domain.LoggedUserDetails;
import dwf.utils.ParsedMap;

@Repository("baseUserDAO")
@Transactional
public class BaseUserDAOImpl extends BaseDAOImpl<BaseUser> implements BaseUserDAO {

	public BaseUserDAOImpl() {
		super(BaseUser.class);
	}

	@Override
	public BaseUser findByEmail(String email) {
		return findFirstByFilter("email", email);
	}


	@Override
	public BaseUser findByPrincipal(Object principal) {
		if(principal instanceof LoggedUserDetails) {
			return findById(((LoggedUserDetails) principal).getId());
		} 
		return findByEmail(principal.toString());
	}
	
	
}
