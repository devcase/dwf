package dwf.utils;

import java.util.HashMap;
import java.util.Map;

public class ModifiableParsedMap implements ParsedMap {
	private final Map<String, Object> values;

	public ModifiableParsedMap(Map<String, ?> values) {
		this.values = new HashMap<String, Object>(values);
	}
	
	public ModifiableParsedMap(Object... values) {
		this.values = new HashMap<String, Object>(values.length/2);
		for (int i = 0; i < values.length; i+=2) {
			this.values.put((String) values[i], values[i+1]);
		}
	}

	/* (non-Javadoc)
	 * @see dwf.utils.ParsedMap#getString(java.lang.String)
	 */
	@Override
	public String getString(String key) {
		Object value = values.get(key);
		if(value == null) return null;
		else if(value instanceof String) {
			return (String) value;
		} else {
			return value.toString();
		}
	}

	/* (non-Javadoc)
	 * @see dwf.utils.ParsedMap#getDouble(java.lang.String)
	 */
	@Override
	public Double getDouble(String key) {
		Object value = values.get(key);
		if(value == null) return null;
		else if(value instanceof Double) {
			return (Double) value;
		} else if (value instanceof Number) {
			return ((Number) value).doubleValue();
		} else {
			throw new IllegalArgumentException("Passed key can't be parsed into a Long");
		}
	}

	/* (non-Javadoc)
	 * @see dwf.utils.ParsedMap#getBoolean(java.lang.String)
	 */
	@Override
	public Boolean getBoolean(String key) {
		Object value = values.get(key);
		if(value == null) return null;
		else if(value instanceof Boolean) {
			return (Boolean) value;
		} else {
			throw new IllegalArgumentException("Passed key can't be parsed into a Long");
		}
	}

	/* (non-Javadoc)
	 * @see dwf.utils.ParsedMap#getLong(java.lang.String)
	 */
	@Override
	public Long getLong(String key) {
		Object value = values.get(key);
		if(value == null) return null;
		else if(value instanceof Long) {
			return (Long) value;
		} else if (value instanceof Number) {
			return ((Number) value).longValue();
		} else {
			throw new IllegalArgumentException("Passed key can't be parsed into a Long");
		}
	}

	/* (non-Javadoc)
	 * @see dwf.utils.ParsedMap#containsKey(java.lang.String)
	 */
	@Override
	public boolean containsKey(String key) {
		return values.containsKey(key);
	}

	/* (non-Javadoc)
	 * @see dwf.utils.ParsedMap#put(java.lang.String, java.lang.Object)
	 */
	@Override
	public Object put(String key, Object value) {
		return values.put(key, value);
	}

	/* (non-Javadoc)
	 * @see dwf.utils.ParsedMap#get(java.lang.String)
	 */
	@Override
	public Object get(String key) {
		return values.get(key);
	}
	
	
}
