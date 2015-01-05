package dwf.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

public abstract class DwfConfigImpl implements DwfConfig {

	public String getEntityPackage() {
		return "";
	}

	public DataSource getDataSource() {
		if(getEnvConfig("JDBC_DRIVER_CLASS_NAME") == null || getEnvConfig("JDBC_CONNECTION_STRING") == null) {
			throw new RuntimeException("Necessário configurar as variáveis de sistema JDBC_DRIVER_CLASS_NAME e JDBC_CONNECTION_STRING");
		}
		
		//cria datasource a partir de variáveis de ambiente - configurar ambiente AWS e de desenvolvimento
		DriverManagerDataSource ds = new DriverManagerDataSource();
		ds.setDriverClassName(getEnvConfig("JDBC_DRIVER_CLASS_NAME"));
		ds.setUrl(getEnvConfig("JDBC_CONNECTION_STRING"));
		return ds;
	}

	protected String getEnvConfig(String configName) {
		String confValue = System.getProperty(configName);
		if(confValue == null) {
			confValue = System.getenv(configName);
		}
		return confValue;
	}

	public String getDatabaseSchema() {
		return "";
	}

	public String tablePrefix(String className) {
		return "";
	}
	
	/* (non-Javadoc)
	 * @see dwf.config.DwfConfig#getApplicationName()
	 */
	@Override
	public String getApplicationName() {
		return "dwfapplication";
	}

	/* (non-Javadoc)
	 * @see dwf.config.DwfConfig#getApplicationComponentPackages()
	 */
	@Override
	public String[] getApplicationComponentPackages() {
		return new String[] {""};
	}

	/* (non-Javadoc)
	 * @see dwf.config.DwfConfig#changeHibernateProperties(java.util.Properties)
	 */
	@Override
	public Properties changeHibernateProperties(Properties hibernateProperties) {
		return hibernateProperties;
	}

	
}
