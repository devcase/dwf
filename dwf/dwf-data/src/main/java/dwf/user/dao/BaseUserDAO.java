package dwf.user.dao;

import dwf.persistence.dao.DAO;
import dwf.user.domain.BaseUser;
import dwf.user.domain.ChangePasswordBean;

public interface BaseUserDAO extends DAO<BaseUser> {
	void changePassword(ChangePasswordBean changePasswordBean);
	BaseUser findByUsername(String username);
}
