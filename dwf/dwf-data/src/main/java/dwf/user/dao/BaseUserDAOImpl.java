package dwf.user.dao;

import javax.transaction.Transactional;
import javax.validation.ValidationException;

import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import dwf.persistence.dao.BaseDAOImpl;
import dwf.user.domain.BaseUser;
import dwf.user.domain.ChangePasswordBean;

@Repository("baseUserDAO")
@Transactional
public class BaseUserDAOImpl extends BaseDAOImpl<BaseUser> implements BaseUserDAO {

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public BaseUserDAOImpl() {
		super(BaseUser.class);
	}

	@Override
	protected void prepareEntity(BaseUser entity) {
		if (entity.getId() == null) {
			entity.setHashedpass(encodePassword(entity.getUsername()));
		} else {
			final String hashedpass = findById(entity.getId()).getHashedpass();
			entity.setHashedpass(hashedpass);
		}
	}
	
	private String encodePassword(String password) {
		return passwordEncoder.encode(password);
	}

	@Override
	public void changePassword(ChangePasswordBean changePasswordBean) {
		if (!changePasswordBean.isValidConfirmation()) {
			throw new ValidationException();
		}
		
		final String username = SecurityContextHolder.getContext().getAuthentication().getName();
		final BaseUser currentUser = findByUsername(username);
		
		if (passwordEncoder.matches(changePasswordBean.getCurrentPassword(), currentUser.getHashedpass())) {
			currentUser.setHashedpass(encodePassword(changePasswordBean.getNewPassword()));
			updateByAnnotation(currentUser);
		} else {
			throw new ValidationException();
		}
	}

	@Override
	public BaseUser findByUsername(String username) {
		final Query query = getSession().createQuery("from BaseUser where username = :username");
		query.setParameter("username", username);
		return (BaseUser) query.uniqueResult();
	}
}
