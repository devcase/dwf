package dwf.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import dwf.user.dao.BaseUserDAO;
import dwf.user.domain.BaseUser;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final BaseUserDAO baseUserDAO;

	@Autowired
	public UserDetailsServiceImpl(BaseUserDAO baseUserDAO) {
		this.baseUserDAO = baseUserDAO;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		final BaseUser baseUser = baseUserDAO.findByUsername(username);
		if (baseUser != null) {
			return new User(baseUser.getUsername(), baseUser.getHashedpass(), AuthorityUtils
					.createAuthorityList(baseUser.getRole().toString()));
		} else {
			throw new UsernameNotFoundException(String.format("User with username=%s was not found", username));
		}
	}
}
