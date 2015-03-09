package dwf.user.service;

import dwf.user.domain.TokenType;
import dwf.user.domain.VerificationToken;

public interface VerificationTokenService {
	VerificationToken findByToken(String token);
	VerificationToken generateToken(String username, TokenType type);
	void generateAndSendToken(String username, TokenType type);
	void confirmToken(String token);
	void verifyToken(VerificationToken token);
}
