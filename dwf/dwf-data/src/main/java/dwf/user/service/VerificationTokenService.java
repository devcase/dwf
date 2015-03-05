package dwf.user.service;

public interface VerificationTokenService {
	void sendEmailConfirmation(final String username);
}
