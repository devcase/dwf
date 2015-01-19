package dwf.data.autoconfigure;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import dwf.config.DwfDataConfig;
import dwf.persistence.utils.DwfNamingStrategy;

@Configuration
@ComponentScan(basePackages = {"dwf.activitylog.service", "dwf.persistence.utils"})
public class DwfDataAutoConfiguration  {
	@Autowired
	private DataSource dataSource;
	@Autowired
	private DwfDataConfig dwfDataConfig;

	@Bean
	public javax.validation.Validator beanValidator() {
		return new LocalValidatorFactoryBean();
	}
	
	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public LocalSessionFactoryBean sessionFactory() {
		
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setPackagesToScan( new String [] {"dwf.activitylog.domain", dwfDataConfig.getClass().getPackage().getName()});
		sessionFactory.setNamingStrategy(new DwfNamingStrategy(dwfDataConfig));
		sessionFactory.setDataSource(dataSource);
		
		return sessionFactory;
	}
	
	
	
}
