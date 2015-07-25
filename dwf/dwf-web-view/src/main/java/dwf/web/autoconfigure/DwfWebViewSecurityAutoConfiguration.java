package dwf.web.autoconfigure;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.LogFactoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import dwf.web.rest.autoconfigure.DwfWebRestAutoConfiguration;

@Configuration
@AutoConfigureBefore(DwfWebRestAutoConfiguration.class)
@ConditionalOnWebApplication
public class DwfWebViewSecurityAutoConfiguration {

	
	@Configuration
	@ConditionalOnProperty(prefix="dwf.security.web", value="enabled", matchIfMissing=true)
	static class WebDefaultConfig  {
		
		/**
		 * Repositório JDBC para armazenar tokens para a função Remember-me
		 * @param dataSource
		 * @return
		 */
		@Bean
		public PersistentTokenRepository tokenRepository(DataSource dataSource) {
			JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl() {
				Log log = LogFactoryImpl.getLog(getClass());

				@Override
				protected void initDao() {
					try {
						getJdbcTemplate().execute(CREATE_TABLE_SQL);
					} catch (Exception ignore) {
						log.info("Could not create persistent_logins table - it probably already exists: " + ignore.getMessage());
					}
				}
				
			};
			tokenRepository.setDataSource(dataSource);
			return tokenRepository;
		}
		
		@Configuration(value="dwfWebViewSecurityConfig")
		@ConditionalOnWebApplication
		@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER + 1)
		@EnableWebSecurity
		static class DwfWebSecurityConfig extends WebSecurityConfigurerAdapter {

			@Autowired
			private UserDetailsService userDetailsService;
			
			@Autowired
			private PasswordEncoder passwordEncoder;
			
			@Autowired
			private PersistentTokenRepository tokenRepository;
			
			@Value("${dwf.security.web.permitallpatterns:}")
			private String[] permitAllPatterns = new String[0];
			@Value("${dwf.security.web.ignorecsrfpatterns:}")
			private String[] ignoreCsrfPatterns = new String[0];
			
			@Override
			protected void configure(AuthenticationManagerBuilder auth) throws Exception {
				auth
					.userDetailsService(userDetailsService)
					.passwordEncoder(passwordEncoder);
			}

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
					.authorizeRequests()
						.antMatchers("/signin","/signin/authenticate","/resources/**","/resetPassword/**").permitAll()
						.anyRequest().authenticated()
						.and()
					.rememberMe().tokenRepository(tokenRepository);
				
				if(permitAllPatterns != null && permitAllPatterns.length > 0) {
					http.authorizeRequests().antMatchers(permitAllPatterns).permitAll()
						.anyRequest().denyAll();
				}
				if(ignoreCsrfPatterns != null && ignoreCsrfPatterns.length > 0) {
					http.csrf().ignoringAntMatchers(ignoreCsrfPatterns);
				}
				
			}
			// @formatter:on
		}

	}
}
