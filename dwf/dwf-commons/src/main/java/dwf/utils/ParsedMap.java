package dwf.utils;

import java.util.Date;


public interface ParsedMap  {
	String getString(String key);
	Double getDouble(String key);
	Boolean getBoolean(String key);
	Long getLong(String key);
	Long[] getLongArray(String key);
	Date getDate(String key);

	boolean containsKey(String key);
	Object get(String key);
	Object put(String key, Object value);
	public <T> Object get(String key, Class<T> expectedClass);
}
