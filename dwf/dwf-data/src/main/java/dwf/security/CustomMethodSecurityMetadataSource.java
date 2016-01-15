package dwf.security;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.method.AbstractMethodSecurityMetadataSource;
import org.springframework.security.access.method.DelegatingMethodSecurityMetadataSource;

/**
 * A component that extends this will add itself as a source to the DelegatingMethodSecurityMetadataSource, defined
 * by {@link org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration.methodSecurityMetadataSource()} 
 * @author hirata
 */
public abstract class CustomMethodSecurityMetadataSource extends AbstractMethodSecurityMetadataSource implements InitializingBean {
	@Autowired
	protected ApplicationContext applicationContext;

	@Override
	public final void afterPropertiesSet() throws Exception {
		DelegatingMethodSecurityMetadataSource delegatingMethodSecurityMetadataSource = applicationContext.getBean(DelegatingMethodSecurityMetadataSource.class);
		delegatingMethodSecurityMetadataSource.getMethodSecurityMetadataSources().add(this);
	}
}
