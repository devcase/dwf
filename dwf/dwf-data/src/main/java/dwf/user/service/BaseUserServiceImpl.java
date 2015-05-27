package dwf.user.service;

import javax.transaction.Transactional;
import javax.validation.ValidationException;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dwf.persistence.validation.ValidationGroups;
import dwf.user.dao.BaseUserDAO;
import dwf.user.domain.BaseUser;
import dwf.user.domain.ChangePasswordBean;
import dwf.user.domain.ResetPasswordBean;
import dwf.user.domain.TokenType;
import dwf.user.domain.VerificationToken;
import dwf.user.utils.LoggedUser;

@Service("baseUserService")
@Transactional
public class BaseUserServiceImpl implements BaseUserService {

	private final BaseUserDAO dao;
	private final PasswordEncoder passwordEncoder;
	private final LoggedUser loggedUser;
	private final VerificationTokenService verificationTokenService;
	
	@Autowired
	public BaseUserServiceImpl(BaseUserDAO dao, VerificationTokenService verificationTokenService, 
			PasswordEncoder passwordEncoder, LoggedUser loggedUser) {
		this.dao = dao;
		this.verificationTokenService = verificationTokenService;
		this.passwordEncoder = passwordEncoder;
		this.loggedUser = loggedUser;
	}

	@Override
	public void changePassword(ChangePasswordBean changePasswordBean) {
		if (!changePasswordBean.isValidConfirmation()) {
			throw new ValidationException();
		}
		
		final String email = loggedUser.getEmail();
		if (email == null) {
			throw new ValidationException();
		}
		
		final BaseUser currentUser = findByEmail(email);
		
		if (passwordEncoder.matches(changePasswordBean.getCurrentPassword(), currentUser.getHashedpass())) {
			currentUser.setHashedpass(passwordEncoder.encode(changePasswordBean.getNewPassword()));
			dao.updateByAnnotation(currentUser, ValidationGroups.ChangePassword.class);
		} else {
			throw new ValidationException();
		}
	}

	public String generateResetPasswordToken(String email) {
		final BaseUser user = findByEmail(email);
		if (user == null) {
			throw new ValidationException();
		}
		return verificationTokenService.generateToken(user.getEmail(), TokenType.RESET_PASSWORD).getToken();
	}
	
	@Override
	public void resetPasswordChange(String token, ResetPasswordBean resetPasswordBean) {
		if (!resetPasswordBean.isValidConfirmation()) {
			throw new ValidationException();
		}
		
		final VerificationToken verificationToken = verificationTokenService.findByToken(token);
		if (verificationToken == null) {
			throw new ValidationException();
		}

		final BaseUser user = verificationToken.getUser();
		user.setHashedpass(passwordEncoder.encode(resetPasswordBean.getNewPassword()));
		dao.updateByAnnotation(user, ValidationGroups.ChangePassword.class);
		
		verificationTokenService.verifyToken(verificationToken);
	}
	
	@Override
	public BaseUser findByEmail(String email) {
		BaseUser b = dao.findByEmail(email);
		if(b != null)
			Hibernate.initialize(b.getRoles());
		return b;
	}
}
