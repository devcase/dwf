package dwf.sample.config;

import java.io.Serializable;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import dwf.user.domain.BaseUserRole;
import dwf.user.utils.BaseSecurityExpressionRoot;

@Configuration
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class SecurityConfig {
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	/**
	 * Configuração de segurança baseada em requisições HTTP
	 * @author Hirata
	 *
	 */
	@Configuration
	public static class WebSecurityConfig extends WebSecurityConfigurerAdapter {
		@Autowired
		private UserDetailsService userDetailsService;
		@Autowired
		private PasswordEncoder passwordEncoder;

		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth
				.userDetailsService(userDetailsService)
				.passwordEncoder(passwordEncoder);
		}

		@Override
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
					.antMatchers("/resources/**").permitAll()
					.antMatchers("/registration", "/resetPassword/**").permitAll()
					.anyRequest().authenticated()
					.and()
				.rememberMe();
		}
	}
	
	/**
	 * Configuração de segurança baseada em chamada de métodos
	 * @author Hirata
	 *
	 */
	@Configuration
	@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
	public static class GlobalMethodSecurityConfig extends GlobalMethodSecurityConfiguration {
		@Autowired
		private ApplicationContext applicationContext;

		/**
		 * Expressões customizadas para anotações @PreAuthorize e taglibs do spring-security
		 * @author Hirata
		 *
		 */
		static class SampleMethodSecurityExpressionRoot extends BaseSecurityExpressionRoot {
			
		    public SampleMethodSecurityExpressionRoot(Authentication a) {
				super(a);
			}
		    void setTarget(Object target) {
		        this.target = target;
		    }
		}
		
		@Override
		protected MethodSecurityExpressionHandler createExpressionHandler() {
			MethodSecurityExpressionHandler expressionHandler =  new DefaultMethodSecurityExpressionHandler() {
				@Autowired(required=false)
				AuthenticationTrustResolver trustResolver;
				
				@Override
				protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {
					SampleMethodSecurityExpressionRoot root = new SampleMethodSecurityExpressionRoot(authentication); 
					applicationContext.getAutowireCapableBeanFactory().autowireBean(root);
			        root.setTarget(invocation.getThis());
			        root.setPermissionEvaluator(getPermissionEvaluator());
			        if(trustResolver == null) {
			        	//ver {@link org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler}
			        	trustResolver = new AuthenticationTrustResolverImpl();
			        }
			        root.setTrustResolver(trustResolver);
			        root.setRoleHierarchy(getRoleHierarchy());
					return root;
				}
			};
			applicationContext.getAutowireCapableBeanFactory().autowireBean(expressionHandler);
			return expressionHandler;
		}

		@Bean
		public PermissionEvaluator samplePermissionEvaluator() {
			return new PermissionEvaluator() {
				@Override
				public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
					return false;
				}
				@Override
				public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
					return false;
				}
			};
		}
	}
	

}
