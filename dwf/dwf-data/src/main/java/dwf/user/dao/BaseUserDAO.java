package dwf.user.dao;

import dwf.persistence.dao.DAO;
import dwf.user.domain.BaseUser;

public interface BaseUserDAO extends DAO<BaseUser> {
	BaseUser findByEmail(String email);
	BaseUser findByPrincipal(Object principal);
}
