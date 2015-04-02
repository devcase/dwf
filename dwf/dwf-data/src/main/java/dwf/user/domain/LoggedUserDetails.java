package dwf.user.domain;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

public class LoggedUserDetails extends User {

	private static final long serialVersionUID = 8248614974611859840L;
	
	private final Long id;
	private final BaseUser baseUser;

	public LoggedUserDetails(BaseUser baseUser) {
		super(baseUser.getEmail(), baseUser.getHashedpass(), AuthorityUtils
				.createAuthorityList(baseUser.getRoles().toArray(new String[0])));
		this.id = baseUser.getId();
		this.baseUser = baseUser;
	}

	public Long getId() {
		return id;
	}

	public BaseUser getBaseUser() {
		return baseUser;
	}
}
