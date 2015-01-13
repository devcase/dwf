package dwf.config;

public interface DwfDataConfig {
	
	String getDatabaseSchema();
	
	/**
	 * 
	 * @param className
	 * @return
	 */
	String tablePrefix(String className);

}
