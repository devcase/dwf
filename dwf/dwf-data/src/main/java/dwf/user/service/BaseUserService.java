package dwf.user.service;

import dwf.user.domain.BaseUser;
import dwf.user.domain.ChangePasswordBean;

public interface BaseUserService {
	void changePassword(ChangePasswordBean changePasswordBean);
	BaseUser findByUsername(String username);
}
