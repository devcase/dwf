package dwf.user.dao;

import javax.transaction.Transactional;
import javax.validation.ValidationException;

import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import dwf.persistence.dao.BaseDAOImpl;
import dwf.user.domain.BaseUser;
import dwf.user.domain.ChangePasswordBean;

@Transactional
public class BaseUserDAOImpl<D extends BaseUser> extends BaseDAOImpl<D> implements BaseUserDAO<D> {

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public BaseUserDAOImpl(Class<D> clazz) {
		super(clazz);
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
		final D currentUser = findByUsername(username);
		
		if (passwordEncoder.matches(changePasswordBean.getCurrentPassword(), currentUser.getHashedpass())) {
			currentUser.setHashedpass(encodePassword(changePasswordBean.getNewPassword()));
			updateByAnnotation(currentUser);
		} else {
			throw new ValidationException();
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public D findByUsername(String username) {
		final Query query = getSession().createQuery("from TravenupUser where username = :username");
		query.setParameter("username", username);
		return (D) query.uniqueResult();
	}
}
