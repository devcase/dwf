package dwf.user;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;


public class DwfUserUtils {
	public static String getCurrentUserId() {
		if(SecurityContextHolder.getContext() != null && SecurityContextHolder.getContext().getAuthentication() != null) {
			Object currentUser = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if(currentUser == null) return null;
			if(currentUser instanceof DwfUser)
				return ((DwfUser) currentUser).getUserId();
			else if (currentUser instanceof UserDetails)
				return ((UserDetails) currentUser).getUsername();
			else return currentUser.toString();
		}
		return null;
	}
}
