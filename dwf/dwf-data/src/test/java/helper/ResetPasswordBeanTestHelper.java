package helper;

import dwf.user.domain.ResetPasswordBean;

public class ResetPasswordBeanTestHelper {

	public static ResetPasswordBean invalidResetPasswordBean() {
		return newResetPasswordBean("confirmation", "wrong_confirmation");
	}

	public static ResetPasswordBean validResetPasswordBean() {
		return newResetPasswordBean("newconfirmation", "newconfirmation");
	}

	public static ResetPasswordBean newResetPasswordBean(String newPassword,
			String newPasswordConfirmation) {
		return new ResetPasswordBean(newPassword, newPasswordConfirmation);
	}
}
