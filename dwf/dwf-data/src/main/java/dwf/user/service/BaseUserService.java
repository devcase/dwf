package dwf.user.service;

import dwf.user.domain.BaseUser;
import dwf.user.domain.ChangePasswordBean;
import dwf.user.domain.ResetPasswordBean;

public interface BaseUserService {
	void changePassword(ChangePasswordBean changePasswordBean);
	String generateResetPasswordToken(String email);
	void resetPasswordChange(String token, ResetPasswordBean resetPasswordBean);
	BaseUser findByEmail(String email);
}
