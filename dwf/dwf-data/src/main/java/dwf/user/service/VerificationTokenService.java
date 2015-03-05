package dwf.user.service;

public interface VerificationTokenService {
	void sendEmailConfirmation(String username);
	void confirmToken(String token);
}
