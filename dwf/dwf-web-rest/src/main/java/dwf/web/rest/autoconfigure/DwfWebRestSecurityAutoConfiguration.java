package dwf.web.rest.autoconfigure;

import org.springframework.context.annotation.Configuration;

@Configuration
public class DwfWebRestSecurityAutoConfiguration {
	
//	/**
//	 * Configuração para aplicações REST, que não tenham dwf-web-view
//	 * @author hirata
//	 *
//	 */
//	@Configuration(value="dwfWebRestSecurityConfig")
//	@ConditionalOnWebApplication
//	@EnableWebSecurity
//	@ConditionalOnMissingClass(value="dwf.web.autoconfigure.DwfWebViewSecurityAutoConfiguration.DwfWebViewSecurityConfig")
//	@ConditionalOnProperty(prefix="dwf.security.web", value="enabled", matchIfMissing=true)
//	@ConditionalOnMissingBean(value=WebSecurityConfigurerAdapter.class)
//	static class DwfWebSecurityConfig extends WebSecurityConfigurerAdapter {
//		@Override
//		protected void configure(HttpSecurity http) throws Exception {
//			http.authorizeRequests().anyRequest().permitAll();
//			//desabilita segurança CSRF para o projeto rest
//			http.csrf().disable();
//		}
//	}

}
