package dwf.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SimpleParsedMap implements ParsedMap {
	private final Map<String, Object> values;

	public SimpleParsedMap(Map<String, ?> values) {
		this.values = new HashMap<String, Object>(values);
	}
	
	public SimpleParsedMap(Object... values) {
		this.values = new HashMap<String, Object>(values.length/2);
		for (int i = 0; i < values.length; i+=2) {
			this.values.put((String) values[i], values[i+1]);
		}
	}
	public SimpleParsedMap() {
		this.values = new HashMap<String, Object>();
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
		if(value == null) {
			return null;
		} else if(value instanceof Boolean) {
			return (Boolean) value;
		} else if(value instanceof String) {
			return Boolean.valueOf(((String) value).toLowerCase());
		} else {
			throw new IllegalArgumentException("Passed key can't be parsed into a boolean");
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
		} else if(value instanceof String) {
			return Long.valueOf((String) value);
		} else {
			throw new IllegalArgumentException("Passed key can't be parsed into a Long: " + value);
		}
	}
	
	

	@Override
	public Date getDate(String key) {
		Object value = values.get(key);
		if(value == null) return null;
		else if(value instanceof Date) {
			return (Date) value;
		} else if (value instanceof Calendar) {
			return ((Calendar) value).getTime();
		} else {
			throw new IllegalArgumentException("Passed key can't be parsed into a Date: " + value);
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
	

	/* (non-Javadoc)
	 * @see dwf.utils.ParsedMap#get(java.lang.String, java.lang.Class)
	 */
	@Override
	public <T> Object get(String key, Class<T> expectedClass) {
		Object v = get(key);
		if(v == null) return null;
		else if (v.getClass().isArray()) return v;
		if(Boolean.class.equals(expectedClass) || 
				boolean.class.equals(expectedClass)) {
			return getBoolean(key);
		} else if(Double.class.equals(expectedClass)||
				double.class.equals(expectedClass)) {
			return getDouble(key);
		} else if(Long.class.equals(expectedClass)) {
			return getLong(key);
		} else if(Date.class.equals(expectedClass)) {
			return getDate(key);
		} else if(String.class.equals(expectedClass)) {
			return getString(key);
		}
		return get(key);
	}
	
}
