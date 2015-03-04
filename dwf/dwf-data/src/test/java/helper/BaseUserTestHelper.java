package helper;

import java.util.Date;

import dwf.user.domain.BaseUser;
import dwf.user.domain.BaseUserRole;

public class BaseUserTestHelper {

	public static BaseUser newBaseUser(String username) {
		return newBaseUser(username, "user@example.com", null, null,
				BaseUserRole.BACKOFFICE_ADMIN);
	}

	public static BaseUser newBaseUser(String username, String email,
			String hashedpass, Date expirationDate, BaseUserRole role) {
		return new BaseUser(username, email, hashedpass, expirationDate, role);
	}
}
