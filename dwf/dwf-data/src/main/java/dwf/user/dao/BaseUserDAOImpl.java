package dwf.user.dao;

import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import dwf.persistence.dao.BaseDAOImpl;
import dwf.user.domain.BaseUser;

@Repository("baseUserDAO")
@Transactional
public class BaseUserDAOImpl extends BaseDAOImpl<BaseUser> implements BaseUserDAO {

	public BaseUserDAOImpl() {
		super(BaseUser.class);
	}

	@Override
	public BaseUser findByEmail(String email) {
		return findFirstByFilter("email", email);
	}
}
