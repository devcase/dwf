package helper;

import dwf.user.domain.ChangePasswordBean;

public class ChangePasswordBeanTestHelper {

	public static ChangePasswordBean invalidChangePasswordBean() {
		return newChangePasswordBean("current", "confirmation",
				"wrong_confirmation");
	}

	public static ChangePasswordBean validChangePasswordBean() {
		return newChangePasswordBean("current", "newconfirmation",
				"newconfirmation");
	}

	public static ChangePasswordBean newChangePasswordBean(
			String currentPassword, String newPassword,
			String newPasswordConfirmation) {
		return new ChangePasswordBean(currentPassword, newPassword,
				newPasswordConfirmation);
	}
}
