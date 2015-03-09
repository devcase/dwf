package dwf.user.service;

import java.util.UUID;

import javax.transaction.Transactional;
import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
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
	private final MailSender mailSender;
	
	@Autowired
	public VerificationTokenServiceImpl(VerificationTokenDAO verificationTokenDAO, BaseUserDAO baseUserDAO, MailSender mailSender) {
		this.verificationTokenDAO = verificationTokenDAO;
		this.baseUserDAO = baseUserDAO;
		this.mailSender = mailSender;
	}

	@Override
	public VerificationToken findByToken(String token) {
		return verificationTokenDAO.findByToken(token);
	}
	
	@Override
	public VerificationToken generateToken(String username, TokenType type) {
		return newVerificationToken(username, type);
	}
	
	@Override
	public void generateAndSendToken(String username, TokenType type) {
		final VerificationToken token = newVerificationToken(username, type);
		final SimpleMailMessage mail = newMailMessage(token);
		
		try {
			mailSender.send(mail);
		} catch (MailException e) {
			e.printStackTrace();
		}
	}

	private SimpleMailMessage newMailMessage(VerificationToken token) {
		final SimpleMailMessage mail = new SimpleMailMessage();
		mail.setTo(token.getUser().getEmail());
		mail.setSubject(token.toString());
		mail.setText("http://localhost:8080/" + token.getType().getUrl() + "/" + token.getToken());
		return mail;
	}
	
	private VerificationToken newVerificationToken(String username, TokenType type) {
		final BaseUser user = baseUserDAO.findFirstByFilter("username", username);
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
