package dwf.utils;

import java.lang.reflect.Array;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SimpleParsedMap implements ParsedMap, Map<String, Object> {
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
		} else if (value.getClass().isArray() && Long.class.isAssignableFrom(value.getClass().getComponentType())) {
			return (Long) Array.get(value, 0);
		} else if (value instanceof Number) {
			return ((Number) value).longValue();
		} else if(value instanceof String) {
			return Long.valueOf((String) value);
		} else {
			throw new IllegalArgumentException("Passed key can't be parsed into a Long: " + value);
		}
	}

	@Override
	public Long[] getLongArray(String key) {
		Object value = values.get(key);
		if(value == null) return null;
		else if(value instanceof Long[]) {
			return (Long[]) value;
		} else if (value instanceof Number) {
			return new Long[] {((Number) value).longValue()};
		} else if(value instanceof String) {
			return new Long[] {Long.valueOf((String) value)};
		} else if(value instanceof Collection) {
			return ((Collection<Long>) value).toArray(new Long[0]);
			//TODO - outras conversões
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
		if (v.getClass().isArray()) {
			if(Array.getLength(v) == 1) {
				v = Array.get(v, 0);
			} else if (Array.getLength(v) == 0) {
				return null;
			} else {
				return v;
			}
		}
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
	
	@Override
	public boolean isMultipleValued(String key) {
		if(values.containsKey(key)) {
			Object value = values.get(key);
			if(value == null) {
				return false;
			} else {
				if((value.getClass().isArray()) && Array.getLength(value) > 1) {
					//chegou array no filtro
					return true;
				} else if((value instanceof Collection<?>) && ((Collection) value).size() > 1) {
					//chegou collection no filtro
					return true;
				} else {
					return false;
				}
			}
		} else {
			return false;
		}
	}
	
	public boolean isEmpty() {
		return values.isEmpty();
	}

	@Override
	public int size() {
		return values.size();
	}

	@Override
	public boolean containsKey(Object key) {
		return values.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return values.containsValue(value);
	}

	@Override
	public Object get(Object key) {
		return values.get(key);
	}

	@Override
	public Object remove(Object key) {
		return values.remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		values.putAll(m);
	}

	@Override
	public void clear() {
		values.clear();
	}

	@Override
	public Set<String> keySet() {
		return values.keySet();
	}

	@Override
	public Collection<Object> values() {
		return values.values();
	}

	@Override
	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		return values.entrySet();
	}
	
	
}
