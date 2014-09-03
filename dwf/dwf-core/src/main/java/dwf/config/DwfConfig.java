package dwf.config;

import java.util.Properties;

import javax.sql.DataSource;

public interface DwfConfig {
	/**
	 * 
	 * @return
	 */
	String getApplicationName();

	/**
	 * 
	 * @return
	 */
	String[] getApplicationComponentPackages();
	
	String getEntityPackage();
	
	/**
	 * 
	 * @return
	 */
	DataSource getDataSource();
	
	String getDatabaseSchema();
	
	/**
	 * 
	 * @param className
	 * @return
	 */
	String tablePrefix(String className);
	
	Properties changeHibernateProperties(Properties hibernateProperties);
}
