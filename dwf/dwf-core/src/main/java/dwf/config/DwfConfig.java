package dwf.config;

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
	String getDataSourceJNDIName();
	
	String getDatabaseSchema();
}
