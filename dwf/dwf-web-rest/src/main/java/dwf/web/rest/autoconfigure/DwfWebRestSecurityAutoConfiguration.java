package dwf.web.rest.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

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
			http.authorizeRequests().anyRequest().permitAll();
			http.csrf().disable();
		}
	}

}
