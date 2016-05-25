package dwf.web.rest.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
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
	@AutoConfigureOrder(SecurityProperties.ACCESS_OVERRIDE_ORDER + 1)
	@EnableWebSecurity
	@ConditionalOnMissingClass(value="dwf.web.autoconfigure.DwfWebViewSecurityAutoConfiguration.DwfWebViewSecurityConfig")
	@ConditionalOnProperty(prefix="dwf.security.web", value="enabled", matchIfMissing=true)
	@ConditionalOnMissingBean(value=WebSecurityConfigurerAdapter.class)
	static class DwfWebSecurityConfig extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests().anyRequest().permitAll();
			//desabilita segurança CSRF para o projeto rest
			http.csrf().disable();
		}
	}

}
