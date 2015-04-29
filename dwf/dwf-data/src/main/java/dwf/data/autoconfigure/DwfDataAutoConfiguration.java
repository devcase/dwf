package dwf.data.autoconfigure;

import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.hibernate.validator.resourceloading.AggregateResourceBundleLocator;
import org.jongo.Jongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.LocaleContextMessageInterpolator;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import dwf.config.DwfDataConfig;
import dwf.persistence.utils.DwfNamingStrategy;
import dwf.persistence.utils.MongoIdModule;

@Configuration
@ComponentScan(basePackages = {"dwf.activitylog.service", "dwf.persistence", "dwf.multilang", "dwf.user"})
@ConfigurationProperties(prefix = "dwf.data")
public class DwfDataAutoConfiguration  {
	@Autowired
	private DataSource dataSource;
	@Autowired
	private DwfDataConfig dwfDataConfig;
	@Autowired
	private ApplicationContext applicationContext;
	
	/**
	 * configuravel a partir de dwf.data.hibernateproperties
	 */
	private Map<String, String> hibernateProperties;
	/**
	 * configuravel a partir de dwf.data.entityPackage
	 */
	private String entityPackage;

	public Map<String, String> getHibernateProperties() {
		return hibernateProperties;
	}

	public void setHibernateProperties(Map<String, String> hibernateProperties) {
		this.hibernateProperties = hibernateProperties;
	}
	public String getEntityPackage() {
		return entityPackage;
	}
	public void setEntityPackage(String entityPackage) {
		this.entityPackage = entityPackage;
	}

	@Bean
	public javax.validation.Validator beanValidator() {
		LocalValidatorFactoryBean a = new LocalValidatorFactoryBean();
		a.setMessageInterpolator(
				new LocaleContextMessageInterpolator(
						new ResourceBundleMessageInterpolator(
								new AggregateResourceBundleLocator(Arrays.asList(new String[] {"labels", "dwf.labels", "org.hibernate.validator.ValidationMessages"})))));
		return a;
	}
	
	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public LocalSessionFactoryBean sessionFactory() {
		if(entityPackage == null) {
			entityPackage = dwfDataConfig.getClass().getPackage().getName();
		}
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setPackagesToScan( new String [] {"dwf.activitylog.domain", "dwf.user.domain", entityPackage});
		sessionFactory.setNamingStrategy(new DwfNamingStrategy(dwfDataConfig));
		sessionFactory.setDataSource(dataSource);
		if(hibernateProperties != null) {
			Properties p = new Properties();
			p.putAll(hibernateProperties);
			sessionFactory.setHibernateProperties(p);
		}
		
		
		return sessionFactory;
	}
	
	
	@Configuration
	@ConditionalOnClass(Jongo.class)
	@ConfigurationProperties("mongodb")
	static class MongoConfig {
		
		private String uri;
		
		public String getUri() {
			return uri;
		}
		public void setUri(String uri) {
			this.uri = uri;
		}

		@Bean
		@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
		public Jongo jongo() throws UnknownHostException {
			MongoClientURI mongoUri = new MongoClientURI(uri);
			DB db = new MongoClient(mongoUri).getDB(mongoUri.getDatabase());
			return new Jongo(db,  MongoIdModule.getMapperBuilder().build());
		}
	}
	
	@Configuration
	@ConditionalOnMissingBean(DwfDataConfig.class)
	@ConfigurationProperties(prefix = "dwf.data")
	static class DefaultDwfDataConfig {
		
		private String tablePrefix;
		private String databaseSchema;
		
		
		public String getTablePrefix() {
			return tablePrefix;
		}
		public void setTablePrefix(String tablePrefix) {
			this.tablePrefix = tablePrefix;
		}
		public String getDatabaseSchema() {
			return databaseSchema;
		}
		public void setDatabaseSchema(String databaseSchema) {
			this.databaseSchema = databaseSchema;
		}


		@Bean
		public DwfDataConfig dwfDataConfig() {
			return new DwfDataConfig() {
				@Override
				public String tablePrefix(String className) {
					return tablePrefix;
				}
				
				@Override
				public String getDatabaseSchema() {
					return databaseSchema;
				}
			};
		}
	}
}
