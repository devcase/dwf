package dwf.web.autoconfigure;

import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import dwf.web.rest.autoconfigure.DwfWebRestAutoConfiguration;

@Configuration
@AutoConfigureBefore(DwfWebRestAutoConfiguration.class)
@ConditionalOnWebApplication
public class DwfWebViewSecurityAutoConfiguration {

	@ConditionalOnMissingBean(value=UserDetailsService.class)
	static class NoDwfDataUserDetailsServiceConfiguration {
		@Bean
		public UserDetailsService userDetailsService() {
			return new UserDetailsService() {
				@Override
				public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
					return new UserDetails() {
						@Override
						public boolean isEnabled() {
							return true;
						}
						
						@Override
						public boolean isCredentialsNonExpired() {
							return true;
						}
						
						@Override
						public boolean isAccountNonLocked() {
							return true;
						}
						
						@Override
						public boolean isAccountNonExpired() {
							return true;
						}
						
						@Override
						public String getUsername() {
							return "dwf";
						}
						
						@Override
						public String getPassword() {
							return null;
						}
						
						@Override
						public Collection<? extends GrantedAuthority> getAuthorities() {
							return Collections.emptyList();
						}
					};
				}
			};
		}
	}
	
	@Configuration
	@ConditionalOnProperty(prefix="dwf.security.web", value="enabled", matchIfMissing=true)
	static class WebDefaultConfig  {
		@Autowired
		private UserDetailsService userDetailsService;
		
		@Value("${dwf.security.web.tokenbasedrememberme.key}")
		private String rememberMeTokenKey;
		public String getRememberMeTokenKey() {
			return rememberMeTokenKey;
		}
		public void setRememberMeTokenKey(String rememberMeTokenKey) {
			this.rememberMeTokenKey = rememberMeTokenKey;
		}


		@Bean
		public RememberMeServices rememberMeServices() {
			TokenBasedRememberMeServices rememberMeServices = new TokenBasedRememberMeServices(rememberMeTokenKey, userDetailsService);
			return rememberMeServices;
		}
	}
	

	@Configuration(value="dwfWebViewSecurityConfig")
	@ConditionalOnWebApplication
	@AutoConfigureOrder(SecurityProperties.ACCESS_OVERRIDE_ORDER + 1)
	@EnableWebSecurity
	static class DwfWebViewSecurityConfig extends WebSecurityConfigurerAdapter {
		
		@Autowired
		private RememberMeServices rememberMeServices;
		@Autowired
		private UserDetailsService userDetailsService;

		@Value("${dwf.security.web.permitallpatterns:}")
		private String[] permitAllPatterns = new String[0];
		@Value("${dwf.security.web.ignorecsrfpatterns:}")
		private String[] ignoreCsrfPatterns = new String[0];
		@Value("${dwf.security.web.tokenbasedrememberme.key}")
		private String rememberMeTokenKey;
		@Value("${dwf.security.web.frameoption.disable:false}")
		private boolean frameoptionDisable;
		

		@Override
		// @formatter:off
		protected void configure(HttpSecurity http) throws Exception {
			http
				.formLogin()
					.loginPage("/signin")
					.loginProcessingUrl("/signin/authenticate")
					.failureUrl("/signin?error")
					.permitAll()
					.and()
				.logout()
					.logoutUrl("/logout")
					.logoutSuccessUrl("/signin?logout").permitAll()
					.logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
					.invalidateHttpSession(true)
					.and()
				.rememberMe().rememberMeServices(rememberMeServices).userDetailsService(userDetailsService).key(rememberMeTokenKey);
			
			
			ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry authorize = http.authorizeRequests();
			
			//dwf.security.web.permitallpatterns define some urls that may be accessed
			//anonymously
			if(permitAllPatterns != null && permitAllPatterns.length > 0) {
				authorize = authorize.antMatchers(permitAllPatterns).permitAll();
			}
			authorize = authorize
					.antMatchers("/signin","/signin/authenticate","/resources/**","/resetPassword/**").permitAll()
					.anyRequest().authenticated();
			authorize.and();
			
			if(ignoreCsrfPatterns != null && ignoreCsrfPatterns.length > 0) {
				http.csrf().ignoringAntMatchers(ignoreCsrfPatterns);
			}
			
			if(frameoptionDisable) {
				http.headers().frameOptions().disable();
			}
			
		}
		// @formatter:on
	}
}
