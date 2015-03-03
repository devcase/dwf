package dwf.user.dao;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import dwf.persistence.dao.BaseDAOImpl;
import dwf.user.domain.BaseUser;

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
			entity.setHashedpass(passwordEncoder.encode(entity.getUsername()));
		} else {
			final String hashedpass = findById(entity.getId()).getHashedpass();
			entity.setHashedpass(hashedpass);
		}
	}
}
