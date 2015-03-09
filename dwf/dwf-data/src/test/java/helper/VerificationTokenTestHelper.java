package helper;

import static helper.BaseUserTestHelper.newBaseUser;

import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

import dwf.user.domain.TokenType;
import dwf.user.domain.VerificationToken;

public class VerificationTokenTestHelper {

	public static VerificationToken expiredVerificationToken() {
		final VerificationToken token = newVerificationToken(TokenType.RESET_PASSWORD);
		token.setExpiryDate(DateUtils.addWeeks(new Date(), -1));
		return token;
	}
	
	public static VerificationToken verifiedVerificationToken() {
		final VerificationToken token = newVerificationToken(TokenType.EMAIL_CONFIRMATION);
		token.setVerified(true);
		return token;
	}
	
	public static VerificationToken validEmailConfirmationToken() {
		final VerificationToken token = newVerificationToken(TokenType.EMAIL_CONFIRMATION);
		return token;
	}
	
	public static VerificationToken validResetPasswordToken() {
		final VerificationToken token = newVerificationToken(TokenType.RESET_PASSWORD);
		return token;
	}
	
	private static VerificationToken newVerificationToken(TokenType type) {
		return new VerificationToken("abcdef1234", newBaseUser("travenup"), type);
	}
}
