package dwf.web.user;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class NewUserAuthentication implements Authentication {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6375272804582728074L;
	private final UserDetails userDetails;
	
	
	public NewUserAuthentication(UserDetails userDetails) {
		super();
		this.userDetails = userDetails;
	}

	@Override
	public String getName() {
		return userDetails.getUsername();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return userDetails.getAuthorities();
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getDetails() {
		return null;
	}

	@Override
	public Object getPrincipal() {
		return userDetails;
	}

	@Override
	public boolean isAuthenticated() {
		return true;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
		throw new IllegalArgumentException();
	}

}
