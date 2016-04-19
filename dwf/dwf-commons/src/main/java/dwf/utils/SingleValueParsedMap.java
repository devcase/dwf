package dwf.utils;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Date;

import org.springframework.util.Assert;

public class SingleValueParsedMap implements ParsedMap {
	private final String key;
	private final Object value;

	public SingleValueParsedMap(String key, Object value) {
		super();
		Assert.hasText(key);
		this.key = key;
		this.value = value;
	}
	
	@Override
	public String getString(String key) {
		if(this.key.equals(key)) return (String) value;
		return null;
	}
	@Override
	public Double getDouble(String key) {
		if(this.key.equals(key)) return (Double) value;
		return null;
	}
	@Override
	public Boolean getBoolean(String key) {
		if(this.key.equals(key)) return (Boolean) value;
		return null;
	}
	@Override
	public Long getLong(String key) {
		if(this.key.equals(key)) return (Long) value;
		return null;
	}
	@Override
	public Long[] getLongArray(String key) {
		return new Long[] { getLong(key) };
	}

	@Override
	public Date getDate(String key) {
		if(this.key.equals(key)) return (Date) value;
		return null;
	}
	@Override
	public boolean containsKey(String key) {
		return this.key.equals(key);
	}
	@Override
	public Object get(String key) {
		if(this.key.equals(key)) return value;
		return null;
	}
	@Override
	public Object put(String key, Object value) {
		throw new IllegalArgumentException("dwf.utils.SingleValueParsedMap.put(String, Object) não deveria ter sido chamado");
	}
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
	
	@Override
	public boolean isMultipleValued(String key) {
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
	}
}
