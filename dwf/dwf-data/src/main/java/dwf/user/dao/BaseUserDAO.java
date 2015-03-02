package dwf.user.dao;

import dwf.persistence.dao.DAO;
import dwf.user.domain.BaseUser;
import dwf.user.domain.ChangePasswordBean;

public interface BaseUserDAO<D extends BaseUser> extends DAO<D> {
	void changePassword(ChangePasswordBean changePasswordBean);
	D findByUsername(String username);
}
