package dwf.user.service;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Default authentication manager
 * @author Hirata
 *
 */
@Component(value="authenticationManager")
public class DwfAuthenticationManager extends ProviderManager {
	
	@Autowired
	public DwfAuthenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
		super( initializeProviderList(userDetailsService, passwordEncoder));
	}
	
	private static List<AuthenticationProvider> initializeProviderList(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder);
		return Collections.singletonList(provider);
	}

}
