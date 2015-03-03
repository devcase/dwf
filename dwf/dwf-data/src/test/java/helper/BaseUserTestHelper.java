package helper;

import java.util.Date;

import dwf.user.domain.BaseUser;
import dwf.user.domain.BaseUserRole;

public class BaseUserTestHelper {

	public static BaseUser newBaseUser(String username) {
		return newBaseUser(username, null, null, BaseUserRole.BACKOFFICE_ADMIN);
	}

	public static BaseUser newBaseUser(String username, String hashedpass,
			Date expirationDate, BaseUserRole role) {
		return new BaseUser(username, hashedpass, expirationDate, role);
	}
}
