package helper;

import java.util.Date;

import dwf.user.domain.BaseUser;
import dwf.user.domain.BaseUserRole;

public class BaseUserTestHelper {

	public static BaseUser newBaseUser(String email) {
		return newBaseUser(email, "abcd1234", null, BaseUserRole.BACKOFFICE_ADMIN);
	}

	public static BaseUser newBaseUser(String email, String hashedpass,
			Date expirationDate, String role) {
		return new BaseUser(email, hashedpass, expirationDate, role);
	}
}
