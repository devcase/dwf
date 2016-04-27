package dwf.user.dao;

import javax.transaction.Transactional;

import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.stereotype.Repository;

import dwf.persistence.dao.BaseDAOImpl;
import dwf.user.domain.BaseUser;
import dwf.user.domain.LoggedUserDetails;

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
		} else if(principal instanceof User) {
			return findByEmail(((User) principal).getName());
		}
		return findByEmail(principal.toString());
	}
	
	
}
