package helper;

import static helper.BaseUserTestHelper.newBaseUser;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

import dwf.user.domain.VerificationToken;

public class VerificationTokenTestHelper {

	public static VerificationToken expiredVerificationToken() {
		final VerificationToken token = newVerificationToken();
		token.setExpiryDate(DateUtils.addWeeks(new Date(), -1));
		return token;
	}
	
	public static VerificationToken verifiedVerificationToken() {
		final VerificationToken token = newVerificationToken();
		token.setVerified(true);
		return token;
	}
	
	public static VerificationToken validVerificationToken() {
		final VerificationToken token = newVerificationToken();
		return token;
	}
	
	private static VerificationToken newVerificationToken() {
		return new VerificationToken("abcdef1234", newBaseUser("travenup"));
	}
}
