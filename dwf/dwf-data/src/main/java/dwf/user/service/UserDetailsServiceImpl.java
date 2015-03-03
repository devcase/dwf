package dwf.user.service;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import dwf.user.domain.BaseUser;

@Service("userDetailsService")
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

	private final BaseUserService userService;

	@Autowired
	public UserDetailsServiceImpl(BaseUserService userService) {
		this.userService = userService;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		final BaseUser baseUser = userService.findByUsername(username);
		if (baseUser != null) {
			return new User(baseUser.getUsername(), baseUser.getHashedpass(), AuthorityUtils
					.createAuthorityList(baseUser.getRole().toString()));
		} else {
			throw new UsernameNotFoundException(String.format("User with username=%s was not found", username));
		}
	}
}
