package dwf.data.autoconfigure;

import java.util.Arrays;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.AggregateResourceBundleLocator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.LocaleContextMessageInterpolator;

import dwf.config.DwfDataConfig;
import dwf.persistence.utils.DwfNamingStrategy;
import dwf.persistence.validation.DwfConstraintValidatorFactory;

@Configuration
@ComponentScan(basePackages = {"dwf.activitylog.service", "dwf.persistence"})
public class DwfDataAutoConfiguration  {
	@Autowired
	private DataSource dataSource;
	@Autowired
	private DwfDataConfig dwfDataConfig;
	@Autowired
	private ApplicationContext applicationContext;
	
	
	@Value("${dwf.data.hibernate.hbm2ddl.auto:}")
	private String hbm2ddlAuto = null;

	

	@Bean
	public javax.validation.Validator beanValidator() {
		LocalValidatorFactoryBean a = new LocalValidatorFactoryBean();
		a.setConstraintValidatorFactory(new DwfConstraintValidatorFactory(applicationContext));
		a.setMessageInterpolator(
				new LocaleContextMessageInterpolator(
						new ResourceBundleMessageInterpolator(
								new AggregateResourceBundleLocator(Arrays.asList(new String[] {"labels", "dwf.labels", "org.hibernate.validator.ValidationMessages"})))));
		return a;
	}
	
	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public LocalSessionFactoryBean sessionFactory() {
		
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setPackagesToScan( new String [] {"dwf.activitylog.domain", dwfDataConfig.getClass().getPackage().getName()});
		sessionFactory.setNamingStrategy(new DwfNamingStrategy(dwfDataConfig));
		sessionFactory.setDataSource(dataSource);
		
		Properties hibernateProperties = new Properties();
		if(StringUtils.isNotBlank(hbm2ddlAuto)) {
			hibernateProperties.setProperty("hibernate.hbm2ddl.auto", hbm2ddlAuto);
		}
		sessionFactory.setHibernateProperties(hibernateProperties);
		
		
		return sessionFactory;
	}
	
	
	
}
