package dwf.user.utils;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class LoggedUserImpl implements LoggedUser {

	@Override
	public String getEmail() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}
}
