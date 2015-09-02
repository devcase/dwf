package dwf.web.autoconfigure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import dwf.web.rest.autoconfigure.DwfWebRestAutoConfiguration;

@Configuration
@AutoConfigureBefore(DwfWebRestAutoConfiguration.class)
@ConditionalOnWebApplication
public class DwfWebViewSecurityAutoConfiguration {

	
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
	@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER + 1)
	@EnableWebSecurity
	static class DwfWebSecurityConfig extends WebSecurityConfigurerAdapter {
		
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
			
		}
		// @formatter:on
	}
}
