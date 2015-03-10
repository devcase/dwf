package dwf.user.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import dwf.user.domain.LoggedUserDetails;

@Component
public class LoggedUserImpl implements LoggedUser {

	@Override
	public Long getId() {
		final LoggedUserDetails userDetails = getLoggedUserDetails();
		return userDetails != null ? userDetails.getId() : null;
	}

	@Override
	public String getEmail() {
		final LoggedUserDetails userDetails = getLoggedUserDetails();
		return userDetails != null ? userDetails.getUsername() : null;
	}

	private LoggedUserDetails getLoggedUserDetails() {
		final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			final Object principal = authentication.getPrincipal();
			if (principal != null && principal instanceof LoggedUserDetails) {
				return (LoggedUserDetails) principal;
			}
		}
		return null;
	}
}
