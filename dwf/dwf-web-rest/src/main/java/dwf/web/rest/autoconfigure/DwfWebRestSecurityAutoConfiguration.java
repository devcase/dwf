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
	
	/**
	 * Configuração para aplicações REST, que não tenham dwf-web-view
	 * @author hirata
	 *
	 */
	@Configuration(value="dwfWebRestSecurityConfig")
	@ConditionalOnWebApplication
	@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER + 1)
	@EnableWebSecurity
	@ConditionalOnMissingBean(name="dwfWebViewSecurityConfig")
	static class DwfWebSecurityConfig extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests().anyRequest().permitAll();
			//desabilita segurança CSRF para o projeto rest
			http.csrf().disable();
		}
	}

}
