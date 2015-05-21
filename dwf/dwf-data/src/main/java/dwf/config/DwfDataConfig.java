package dwf.config;

/**
 * @deprecated Configure dwf.data.tablePrefix and dwf.data.databaseSchema application.properties
 * @author Hirata
 *
 */
@Deprecated
public interface DwfDataConfig {
	
	String getDatabaseSchema();
	
	/**
	 * 
	 * @param className
	 * @return
	 */
	String tablePrefix(String className);

}
