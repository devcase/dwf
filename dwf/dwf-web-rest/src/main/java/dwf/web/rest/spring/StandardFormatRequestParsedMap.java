package dwf.web.rest.spring;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import dwf.utils.ParsedMap;

/**
 * Converte números e datas sem considerar o locale do usuário
 * @author hirata
 *
 */
public class StandardFormatRequestParsedMap implements ParsedMap {
	private final Map<String, String[]> requestMap;
	private final Map<String, Object> newObjectMap; //mapa para colocar itens extras
	private final String keyPrefix;
	private ISO8601DateFormat dateformat = new ISO8601DateFormat();
	private NumberFormat numberformat = new DecimalFormat("0.#", new DecimalFormatSymbols(Locale.US));

	
	
	public StandardFormatRequestParsedMap(Map<String, String[]> requestMap,
			String keyPrefix) {
		super();
		this.requestMap = Collections.unmodifiableMap(requestMap);
		this.newObjectMap = new HashMap<String, Object>();
		this.keyPrefix = keyPrefix;
	}
	
	@Override
	public String getString(String key) {
		if(newObjectMap.containsKey(key)) {
			return (String) newObjectMap.get(key);
		}
		if(keyPrefix != null) key = keyPrefix + key;
		if(requestMap.containsKey(key) && requestMap.get(key).length != 0) {
			return requestMap.get(key)[0];
		} else {
			return null;
		}
	}

	@Override
	public Double getDouble(String key) {
		if(newObjectMap.containsKey(key)) {
			return (Double) newObjectMap.get(key);
		}
		if(keyPrefix != null) key = keyPrefix + key;
		if(!requestMap.containsKey(key) || requestMap.get(key).length == 0 || StringUtils.isBlank(requestMap.get(key)[0])) {
			return null;
		} else {
			String paramValue = requestMap.get(key)[0];
			return convertToDouble(paramValue);
		}
	
	}
	
	@Override
	public Date getDate(String key) {
		if(newObjectMap.containsKey(key)) {
			return (Date) newObjectMap.get(key);
		}
		if(keyPrefix != null) key = keyPrefix + key;
		if(!requestMap.containsKey(key) || requestMap.get(key).length == 0 || StringUtils.isBlank(requestMap.get(key)[0])) {
			return null;
		} else {
			String paramValue = requestMap.get(key)[0];
			return convertToDate(paramValue);
		}
	
	}

	protected double convertToDouble(String paramValue) {
		try {
			
			return numberformat.parse(paramValue).doubleValue();
		} catch (ParseException e) {
			throw new IllegalArgumentException("Value at provided key can't be parsed into a Long", e);
		}
	}
	
	
	protected Date convertToDate(String paramValue) {
		try {
			return dateformat.parse(paramValue);
		} catch (ParseException e) {
			throw new IllegalArgumentException("Value at provided key can't be parsed into a Date", e);
		}
	}


	@Override
	public Long getLong(String key) {
		if(newObjectMap.containsKey(key)) {
			return (Long) newObjectMap.get(key);
		}
		
		if(keyPrefix != null) key = keyPrefix + key;
		if(!requestMap.containsKey(key) || requestMap.get(key).length == 0 || StringUtils.isBlank(requestMap.get(key)[0])) {
			return null;
		} else {
			String paramValue = requestMap.get(key)[0];
			return convertToLong(paramValue);
		}
	}
	
	

	@Override
	public Long[] getLongArray(String key) {
		if(newObjectMap.containsKey(key)) {
			return (Long[]) newObjectMap.get(key);
		}
		
		if(keyPrefix != null) key = keyPrefix + key;
		if(!requestMap.containsKey(key) || requestMap.get(key).length == 0 || StringUtils.isBlank(requestMap.get(key)[0])) {
			return null;
		} else {
			String[] paramValue = requestMap.get(key);
			Long[] ret = new Long[paramValue.length];
			for (int i = 0; i < ret.length; i++) {
				ret[i] = convertToLong(paramValue[i]);
			}
			return ret;
		}
	}

	protected long convertToLong(String paramValue) {
		try {
			return numberformat.parse(paramValue).longValue();
		} catch (ParseException e) {
			throw new IllegalArgumentException("Value at provided key can't be parsed into a Long", e);
		}
	}

	
	@Override
	public Boolean getBoolean(String key) {
		if(newObjectMap.containsKey(key)) {
			return (Boolean) newObjectMap.get(key);
		} 
		if(keyPrefix != null) key = keyPrefix + key;
		if(!requestMap.containsKey(key) || requestMap.get(key).length == 0 || StringUtils.isBlank(requestMap.get(key)[0])) {
			return null;
		} else {
			String paramValue = requestMap.get(key)[0];
			return convertToBoolean(paramValue);
		}
	}

	protected boolean convertToBoolean(String paramValue) {
		paramValue = paramValue.toLowerCase().trim();
		if ("true".equals(paramValue) || "yes".equals(paramValue)) {
			return true;
		} else if ("false".equals(paramValue) || "no".equals(paramValue)) { 
			return false;
		} else {
			throw new IllegalArgumentException("Value at provided key can't be parsed into a Boolean");
		}
	}

	public int size() {
		return requestMap.size();
	}

	public boolean isEmpty() {
		return newObjectMap.isEmpty() && requestMap.isEmpty();
	}

	public boolean containsKey(String key) {
		return newObjectMap.containsKey(key) || requestMap.containsKey(keyPrefix + key);
		
	}
	public boolean containsKey(Object key) {
		return newObjectMap.containsKey(key) || requestMap.containsKey(keyPrefix + key);
	}

	public boolean containsValue(Object value) {
		return newObjectMap.containsValue(value) || requestMap.containsValue(value);
	}

	public Object get(Object key) {
		if(newObjectMap.containsKey(key)) return newObjectMap.get(key);

		if(keyPrefix != null) key = keyPrefix + key;
		String[] value = requestMap.get(key);
		if (value != null && value.length == 1) {
			return value[0];
		} else {
			return value;
		}
	}

	public Object put(String key, Object value) {
		return newObjectMap.put(key, value);
	}

	public Object remove(Object key) {
		throw new UnsupportedOperationException();
	}

	public void putAll(Map<? extends String, ? extends Object> m) {
		throw new UnsupportedOperationException();
	}

	public void clear() {
		throw new UnsupportedOperationException();
	}

	public Set<String> keySet() {
		//TODO!
//		return requestMap.keySet();
		throw new UnsupportedOperationException();
	}

	public Collection<Object> values() {
		return new ArrayList<Object>(requestMap.values());
	}

	public Set<java.util.Map.Entry<String, Object>> entrySet() {
		//TODO! Inconsistente com o prefixo de chave
//		return requestMap.entrySet();
		throw new UnsupportedOperationException();
	}

	public boolean equals(Object o) {
		return requestMap.equals(o);
	}

	public int hashCode() {
		return requestMap.hashCode();
	}

	/* (non-Javadoc)
	 * @see dwf.utils.ParsedMap#get(java.lang.String)
	 */
	@Override
	public Object get(String key) {
		if(newObjectMap.containsKey(key)) {
			return newObjectMap.get(key);
		}
		if(keyPrefix != null) key = keyPrefix + key;
		if(requestMap.containsKey(key) && requestMap.get(key).length != 0) {
			return requestMap.get(key)[0];
		} else {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see dwf.utils.ParsedMap#get(java.lang.String, java.lang.Class)
	 */
	@Override
	public <T> Object get(String key, Class<T> expectedClass) {
		if(newObjectMap.containsKey(key)) {
			return newObjectMap.get(key);
		}
		if(keyPrefix != null) key = keyPrefix + key;
		Class testClass = expectedClass;
		if(expectedClass.isArray()) {
			testClass = expectedClass.getComponentType();
		}
		if(requestMap.containsKey(key)) {
			//check if it is a array or a single value
			List<Object> convertedValues = new ArrayList<Object>();
			for (String submittedValue : requestMap.get(key)) {
				if(testClass == boolean.class) {
					convertedValues.add(convertToBoolean(submittedValue));
				} else if(Boolean.class.isAssignableFrom(testClass)) {
					convertedValues.add(convertToBoolean(submittedValue));
				} else if(Long.class.isAssignableFrom(testClass)) {
					convertedValues.add(convertToLong(submittedValue));
				} else if(Double.class.isAssignableFrom(testClass)) {
					convertedValues.add(convertToDouble(submittedValue));
				} else if(testClass.isEnum()){
					convertedValues.add(Enum.valueOf((Class<? extends Enum>) testClass, submittedValue));
				} else {
					convertedValues.add(submittedValue);
				}
			}
			
			if(convertedValues.size() == 1) {
				return convertedValues.get(0);
			} else {
				return convertedValues;
			}
		}
		return null;
	}

	
	public String getQueryString() {
		StringBuilder queryStringBuilder = new StringBuilder();
		for (Map.Entry<String, String[]> requestParam : requestMap.entrySet()) {
			if(requestParam.getValue() != null && !requestParam.getKey().equals("pageNumber") && !requestParam.getKey().equals("fetchSize") && !requestParam.getKey().equals("decorator")) {
				for (String str : requestParam.getValue()) {
					queryStringBuilder.append("&");
					queryStringBuilder.append(requestParam.getKey()).append("=").append(str);
				}
			}
		}
		return queryStringBuilder.toString();
	}
	
	@Override
	public boolean isMultipleValued(String key) {
		if(newObjectMap.containsKey(key)) {
			Object value = newObjectMap.get(key);
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
			String[] reqParam = requestMap.get(key);
			return reqParam != null && reqParam.length > 1;
		}
	}

}
