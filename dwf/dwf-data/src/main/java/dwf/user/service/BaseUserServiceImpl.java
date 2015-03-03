package dwf.user.service;

import javax.transaction.Transactional;
import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import dwf.user.dao.BaseUserDAO;
import dwf.user.domain.BaseUser;
import dwf.user.domain.ChangePasswordBean;
import dwf.user.utils.LoggedUser;

@Service("baseUserService")
@Transactional
public class BaseUserServiceImpl implements BaseUserService {

	private final BaseUserDAO dao;
	private final PasswordEncoder passwordEncoder;
	private final LoggedUser loggedUser;
	
	@Autowired
	public BaseUserServiceImpl(BaseUserDAO dao, PasswordEncoder passwordEncoder, LoggedUser loggedUser) {
		this.dao = dao;
		this.passwordEncoder = passwordEncoder;
		this.loggedUser = loggedUser;
	}

	@Override
	public void changePassword(ChangePasswordBean changePasswordBean) {
		if (!changePasswordBean.isValidConfirmation()) {
			throw new ValidationException();
		}
		
		final String username = loggedUser.getUsername();
		if (username == null) {
			throw new ValidationException();
		}
		
		final BaseUser currentUser = findByUsername(username);
		
		if (passwordEncoder.matches(changePasswordBean.getCurrentPassword(), currentUser.getHashedpass())) {
			currentUser.setHashedpass(passwordEncoder.encode(changePasswordBean.getNewPassword()));
			dao.updateByAnnotation(currentUser);
		} else {
			throw new ValidationException();
		}
	}

	@Override
	public BaseUser findByUsername(String username) {
		return dao.findFirstByFilter("username", username);
	}
}
