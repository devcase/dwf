package dwf.user.dao;

import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import dwf.persistence.dao.BaseDAOImpl;
import dwf.user.domain.VerificationToken;

@Repository("verificationTokenDAO")
@Transactional
public class VerificationTokenDAOImpl extends BaseDAOImpl<VerificationToken> implements VerificationTokenDAO {

	public VerificationTokenDAOImpl() {
		super(VerificationToken.class);
	}
}
