package dwf.user.dao;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import dwf.persistence.dao.BaseDAOImpl;
import dwf.user.domain.BaseUser;

@Repository("baseUserDAO")
@Transactional
public class BaseUserDAOImpl extends BaseDAOImpl<BaseUser> implements BaseUserDAO {

	private final PasswordEncoder passwordEncoder;
	
	@Autowired
	public BaseUserDAOImpl(PasswordEncoder passwordEncoder) {
		super(BaseUser.class);
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	protected void prepareEntity(BaseUser entity) {
		if (entity.getId() == null) {
			entity.setHashedpass(passwordEncoder.encode(entity.getEmail()));
		} else if (StringUtils.isBlank(entity.getHashedpass())) {
			final String hashedpass = findById(entity.getId()).getHashedpass();
			entity.setHashedpass(hashedpass);
		}
	}

	@Override
	public BaseUser findByEmail(String email) {
		return findFirstByFilter("email", email);
	}
}
