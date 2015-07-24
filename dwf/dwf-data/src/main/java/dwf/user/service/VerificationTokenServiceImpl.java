package dwf.user.service;

import java.util.UUID;

import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;
import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import dwf.user.dao.BaseUserDAO;
import dwf.user.dao.VerificationTokenDAO;
import dwf.user.domain.BaseUser;
import dwf.user.domain.TokenType;
import dwf.user.domain.VerificationToken;

@Service("verificationTokenService")
@Transactional
public class VerificationTokenServiceImpl implements VerificationTokenService {

	private final VerificationTokenDAO verificationTokenDAO;
	private final BaseUserDAO baseUserDAO;
	
	@Autowired
	public VerificationTokenServiceImpl(VerificationTokenDAO verificationTokenDAO, BaseUserDAO baseUserDAO) {
		this.verificationTokenDAO = verificationTokenDAO;
		this.baseUserDAO = baseUserDAO;
	}

	@Override
	public VerificationToken findByToken(String token) {
		return verificationTokenDAO.findByToken(token);
	}
	
	@Override
	public VerificationToken generateToken(String email, TokenType type) {
		return newVerificationToken(email, type);
	}
	
	@Override
	public VerificationToken saveToken(String email, TokenType type, String preGeneratedToken) {
		final BaseUser user = baseUserDAO.findByEmail(email);
		final VerificationToken verToken = new VerificationToken(preGeneratedToken, user, type);
		verificationTokenDAO.saveNew(verToken);
		return verToken;
	}

	private VerificationToken newVerificationToken(String email, TokenType type) {
		final BaseUser user = baseUserDAO.findByEmail(email);
		final VerificationToken token = new VerificationToken(UUID.randomUUID().toString(), user, type);
		verificationTokenDAO.saveNew(token);
		return token;
	}

	@Override
	public void confirmToken(String token) {
		final VerificationToken verificationToken = verificationTokenDAO.findByToken(token);
		if (verificationToken == null || verificationToken.hasExpired() || verificationToken.isVerified()) {
			throw new ValidationException();
		}
		
		verifyToken(verificationToken);

		if (TokenType.EMAIL_CONFIRMATION.equals(verificationToken.getType())) {
			final BaseUser user = verificationToken.getUser();
			user.setVerified(true);
			baseUserDAO.updateByAnnotation(user);
		}
	}

	@Override
	public void verifyToken(VerificationToken token) {
		token.setVerified(true);
		verificationTokenDAO.updateByAnnotation(token);
	}
}
