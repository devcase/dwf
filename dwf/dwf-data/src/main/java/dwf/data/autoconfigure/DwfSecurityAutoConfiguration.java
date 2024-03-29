package dwf.data.autoconfigure;

import java.io.Serializable;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.AbstractSecurityExpressionHandler;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import dwf.user.domain.BaseUserRole;
import dwf.user.utils.BasePermissionEvaluator;

/**
 *  
 * @author Hirata
 *
 */
@Configuration
@ConditionalOnBean(UserDetailsService.class)
public class DwfSecurityAutoConfiguration  {
	
	@Bean
	@ConditionalOnMissingBean(PasswordEncoder.class)
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Configuration
	static class DwfGlobalAuthenticationConfigurerAdapter extends GlobalAuthenticationConfigurerAdapter {
		@Autowired
		private UserDetailsService userDetailsService;
		
		@Autowired
		private PasswordEncoder passwordEncoder;

		@Override
		public void init(AuthenticationManagerBuilder auth) throws Exception {
			auth
				.userDetailsService(userDetailsService)
				.passwordEncoder(passwordEncoder);
		}

		@Override
		public void configure(AuthenticationManagerBuilder auth) throws Exception {

		}
		
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
		
		private static boolean hasRole(Authentication authentication, String role) {
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
	
	@Configuration
	static class MethodSecurityExpressionHandlerConfiguration {
		@Autowired
		private PermissionEvaluator permissionEvaluator;
		
		@Bean
		public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
			MethodSecurityExpressionHandler expHandler =  new DefaultMethodSecurityExpressionHandler();
			if(expHandler instanceof AbstractSecurityExpressionHandler) {
				((AbstractSecurityExpressionHandler<?>) expHandler).setPermissionEvaluator(permissionEvaluator);
			}
			return expHandler;
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
		private MethodSecurityExpressionHandler methodSecurityExpressionHandler;

		@Override
		protected MethodSecurityExpressionHandler createExpressionHandler() {
			return methodSecurityExpressionHandler;
		}
	}
	
	
}

