package dwf.data.autoconfigure;

import java.io.Serializable;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.AbstractSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import dwf.user.domain.BaseUserRole;
import dwf.user.utils.BasePermissionEvaluator;

/**
 *  
 * @author Hirata
 *
 */
@Configuration
public class DwfSecurityAutoConfiguration  {
	
	@Bean
	@ConditionalOnMissingBean(PasswordEncoder.class)
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	/**
	 * PermissionEvaluator padrão - permite tudo se tiver a role BaseUserRole.BACKOFFICE_ADMIN.
	 * Aplicações podem registrar seu próprio PermissionEvaluator.
	 * @author Hirata
	 *
	 */
	@Configuration
	@ConditionalOnMissingBean(value=PermissionEvaluator.class)
	static class DwfDefaultPermissionEvaluatorConfiguration {

		@Bean
		public PermissionEvaluator dwfDefaultPermissionEvaluator() {
			return new BasePermissionEvaluator() {
				
				@Override
				public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
					return hasRole(authentication, BaseUserRole.BACKOFFICE_ADMIN);
				}
				
				@Override
				public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
					return hasRole(authentication, BaseUserRole.BACKOFFICE_ADMIN);
				}
			};
		}
		
		public static boolean hasRole(Authentication authentication, String role) {
	        for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
	            if (role.equals(grantedAuthority.getAuthority())) {
	                return true;
	            }
	        }
	        return false;
		}
	}
	
	/**
	 * Associa o DefaultWebSecurityExpressionHandler ao permissionEvaluator disponível 
	 * @author Hirata
	 *
	 */
	@Configuration
	static class PermissionEvaluatorConfiguration implements BeanPostProcessor {
		@Autowired
		private PermissionEvaluator permissionEvaluator;

		@Override
		public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
			return bean;
		}

		@Override
		public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
			if(bean instanceof AbstractSecurityExpressionHandler) {
				((AbstractSecurityExpressionHandler<?>) bean).setPermissionEvaluator(permissionEvaluator);
			}
			return bean;
		}
	}
	
	/**
	 * Associa o DefaultMethodSecurityExpressionHandler ao permissionEvaluator definido aqui ou na aplicação.
	 * @author Hirata
	 *
	 */
	@Configuration
	@EnableGlobalMethodSecurity(prePostEnabled=true, securedEnabled=true)
	static class DwfGlobalMethodSecurityConfiguration extends GlobalMethodSecurityConfiguration {
		@Autowired
		private PermissionEvaluator permissionEvaluator;

		@Override
		protected MethodSecurityExpressionHandler createExpressionHandler() {
			MethodSecurityExpressionHandler expHandler =  super.createExpressionHandler();
			if(expHandler instanceof AbstractSecurityExpressionHandler) {
				((AbstractSecurityExpressionHandler<?>) expHandler).setPermissionEvaluator(permissionEvaluator);
			}
			return expHandler;
		}
	}
	
	@Configuration
	@ConditionalOnMissingBean(WebSecurityConfigurerAdapter.class)
	static class DwfWebSecurityConfig  {
		
		@Bean
		public PersistentTokenRepository tokenRepository(DataSource dataSource) {
			JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
			tokenRepository.setDataSource(dataSource);
			tokenRepository.setCreateTableOnStartup(true);
			return tokenRepository;
		}
		
		@Bean
		@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
		public WebSecurityConfigurerAdapter webSecurityConfigurerAdapter() {
			return new WebSecurityConfigurerAdapter() {

				@Autowired
				private UserDetailsService userDetailsService;
				
				@Autowired
				private PasswordEncoder passwordEncoder;
				
				@Autowired
				private PersistentTokenRepository tokenRepository;
				
				@Value("${dwf.security.web.permitallpatterns:}")
				private String[] permitAllPatterns = new String[0];

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
							.antMatchers(permitAllPatterns).permitAll()
							.anyRequest().authenticated()
							.and()
						.rememberMe().tokenRepository(tokenRepository);
				}
				// @formatter:on
			};
		}

	}
	
	
}

