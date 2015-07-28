package dwf.web.rest.autoconfigure;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.ManagementServerProperties;
import org.springframework.boot.actuate.endpoint.mvc.EndpointHandlerMapping;
import org.springframework.boot.actuate.endpoint.mvc.MvcEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.autoconfigure.security.SpringBootWebSecurityConfiguration;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.util.StringUtils;

@Configuration
public class DwfWebRestSecurityAutoConfiguration {
	
	@Configuration(value="dwfWebRestSecurityConfig")
	@ConditionalOnWebApplication
	@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER + 1)
	@EnableWebSecurity
	@ConditionalOnMissingBean(name="dwfWebViewSecurityConfig")
	static class DwfWebSecurityConfig extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			//http.authorizeRequests().anyRequest().permitAll();
			http.csrf().disable();
		}
	}
	
	/**
	 * Acesso aos endpoints de gerenciamento do Actuator
	 * @author Hirata
	 *
	 */
	@Configuration
	@Order(ManagementServerProperties.ACCESS_OVERRIDE_ORDER)
	static class ManagementWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

		@Autowired
		private SecurityProperties security;

		@Autowired
		private ManagementServerProperties management;

		@Autowired
		private ServerProperties server;

		@Autowired
		private EndpointHandlerMapping endpointHandlerMapping;

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
			// secure endpoints
			String[] paths = getEndpointPaths(this.endpointHandlerMapping);
			
			AuthenticationEntryPoint entryPoint = entryPoint();
			http.exceptionHandling().authenticationEntryPoint(entryPoint);
			paths = this.server.getPathsArray(paths);
			http.requestMatchers().antMatchers(paths);
			
//			String[] insecureEndpointPaths = this.server.getPathsArray(getEndpointPaths(
//					this.endpointHandlerMapping, false));
			
			http.authorizeRequests()
				.anyRequest().hasRole(this.management.getSecurity().getRole());

			http.httpBasic().authenticationEntryPoint(entryPoint);
			
			// No cookies for management endpoints by default
			http.csrf().disable();
			http.sessionManagement().sessionCreationPolicy(
					this.management.getSecurity().getSessions());
			SpringBootWebSecurityConfiguration.configureHeaders(http.headers(),
					this.security.getHeaders());
		}


		private AuthenticationEntryPoint entryPoint() {
			BasicAuthenticationEntryPoint entryPoint = new BasicAuthenticationEntryPoint();
			entryPoint.setRealmName("DWF");
			return entryPoint;
		}



		private static String[] getEndpointPaths(EndpointHandlerMapping endpointHandlerMapping) {
			return StringUtils.mergeStringArrays(
					getEndpointPaths(endpointHandlerMapping, false),
					getEndpointPaths(endpointHandlerMapping, true));
		}

		private static String[] getEndpointPaths(
				EndpointHandlerMapping endpointHandlerMapping, boolean secure) {
			Set<? extends MvcEndpoint> endpoints = endpointHandlerMapping.getEndpoints();
			List<String> paths = new ArrayList<String>(endpoints.size());
			for (MvcEndpoint endpoint : endpoints) {
				if (endpoint.isSensitive() == secure) {
					String path = endpointHandlerMapping.getPath(endpoint.getPath());
					paths.add(path);
					// Ensure that nested paths are secured
					paths.add(path + "/**");
					// Add Spring MVC-generated additional paths
					paths.add(path + ".*");
				}
			}
			return paths.toArray(new String[paths.size()]);
		}
	}
}
