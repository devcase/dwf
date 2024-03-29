package dwf.web.rest.spring;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import dwf.utils.ParsedMap;
import dwf.utils.SimpleParsedMap;
import dwf.web.conversion.DwfCustomDateEditor;

/**
 * Resolves {@link ParsedMap} method arguments annotated with an @{@link RequestParam} where the annotation does
 * specify a param name.
 * 
 * @author Hirata
 *
 */
@Component
public class ParsedMapArgumentResolver implements HandlerMethodArgumentResolver {
	
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return (!StandardFormatRequestParsedMap.class.isAssignableFrom(parameter.getParameterType())) && (RequestParsedMap.class.isAssignableFrom(parameter.getParameterType()) || ParsedMap.class.isAssignableFrom(parameter.getParameterType()));
	}

	@Override
	public Object resolveArgument(MethodParameter parameter,
			ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) throws Exception {
		//String parameterPrefix = parameter.getParameterName().concat(".");
		Map<String, String[]> parameterMap = webRequest.getParameterMap();
		return new RequestParsedMap(parameterMap, "", webRequest.getLocale());
	}

	/**
	 * Wraps a {@code Map<String, String[]>}. Delegates the {@link Map} methods to the
	 * wrapped map. The {@link ParsedMap} methods parses the correspondent value, according
	 * to the user locale.
	 * @author Hirata
	 *
	 */
	public static class RequestParsedMap implements ParsedMap, Map<String, Object> {
		private final Map<String, String[]> requestMap;
		private final SimpleParsedMap simpleParsedMap;
		
		private final String keyPrefix;
		private final Locale locale;
		public RequestParsedMap(Map<String, String[]> requestMap,
				String keyPrefix, Locale locale) {
			super();
			this.requestMap = Collections.unmodifiableMap(requestMap);
			this.simpleParsedMap = new SimpleParsedMap();
			this.keyPrefix = keyPrefix;
			this.locale = locale;
		}
		
		@Override
		public String getString(String key) {
			if(simpleParsedMap.containsKey(key)) {
				return simpleParsedMap.getString(key);
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
			if(simpleParsedMap.containsKey(key)) {
				return simpleParsedMap.getDouble(key);
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
			if(simpleParsedMap.containsKey(key)) {
				return simpleParsedMap.getDate(key);
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
				return NumberFormat.getInstance(this.locale).parse(paramValue).doubleValue();
			} catch (ParseException e) {
				throw new IllegalArgumentException("Value at provided key can't be parsed into a Long", e);
			}
		}
		protected Date convertToDate(String paramValue) {
			try {
				return DwfCustomDateEditor.createDateFormat(locale).parse(paramValue);
			} catch (ParseException e) {
				throw new IllegalArgumentException("Value at provided key can't be parsed into a Date", e);
			}
		}


		@Override
		public Long getLong(String key) {
			if(simpleParsedMap.containsKey(key)) {
				return simpleParsedMap.getLong(key);
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
			if(simpleParsedMap.containsKey(key)) {
				return simpleParsedMap.getLongArray(key);
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
				return NumberFormat.getInstance(this.locale).parse(paramValue).longValue();
			} catch (ParseException e) {
				throw new IllegalArgumentException("Value at provided key can't be parsed into a Long", e);
			}
		}

		protected int convertToInteger(String paramValue) {
			try {
				return NumberFormat.getInstance(this.locale).parse(paramValue).intValue();
			} catch (ParseException e) {
				throw new IllegalArgumentException("Value at provided key can't be parsed into a Integer", e);
			}
		}

		
		@Override
		public Boolean getBoolean(String key) {
			if(simpleParsedMap.containsKey(key)) {
				return simpleParsedMap.getBoolean(key);
			}
			if(keyPrefix != null) key = keyPrefix + key;
			if(!requestMap.containsKey(key) || requestMap.get(key).length == 0 || StringUtils.isBlank(requestMap.get(key)[0])) {
				return null;
			} else {
				String paramValue = requestMap.get(key)[0];
				return convertToBoolean(paramValue);
			}
		}

		protected Boolean convertToBoolean(String paramValue) {
			paramValue = paramValue.toLowerCase().trim();
			if ("true".equals(paramValue) || "yes".equals(paramValue)) {
				return true;
			} else if ("false".equals(paramValue) || "no".equals(paramValue)) { 
				return false;
			} else if ("".equals(paramValue)) { 
				return null;
			} else {
				throw new IllegalArgumentException("Value at provided key can't be parsed into a Boolean");
			}
		}

		public int size() {
			return requestMap.size();
		}

		public boolean isEmpty() {
			return simpleParsedMap.isEmpty() && requestMap.isEmpty();
		}

		public boolean containsKey(String key) {
			return simpleParsedMap.containsKey(key) || requestMap.containsKey(keyPrefix + key);
			
		}
		public boolean containsKey(Object key) {
			return simpleParsedMap.containsKey(key.toString()) || requestMap.containsKey(keyPrefix + key);
		}

		public boolean containsValue(Object value) {
			return simpleParsedMap.containsValue(value) || requestMap.containsValue(value);
		}

		public Object get(Object key) {
			if(simpleParsedMap.containsKey(key)) return simpleParsedMap.get(key);

			if(keyPrefix != null) key = keyPrefix + key;
			String[] value = requestMap.get(key);
			if (value != null && value.length == 1) {
				return value[0];
			} else {
				return value;
			}
		}

		public Object put(String key, Object value) {
			return simpleParsedMap.put(key, value);
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
//			return requestMap.keySet();
			throw new UnsupportedOperationException();
		}

		public Collection<Object> values() {
			return new ArrayList<Object>(requestMap.values());
		}

		public Set<java.util.Map.Entry<String, Object>> entrySet() {
			//TODO! Inconsistente com o prefixo de chave
//			return requestMap.entrySet();
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
			if(simpleParsedMap.containsKey(key)) {
				return simpleParsedMap.get(key);
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
			if(simpleParsedMap.containsKey(key)) {
				return simpleParsedMap.get(key, expectedClass);
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
					} else if(Integer.class.isAssignableFrom(testClass)) {
						convertedValues.add(convertToInteger(submittedValue));
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

		@Override
		public boolean isMultipleValued(String key) {
			if(simpleParsedMap.containsKey(key)) {
				return simpleParsedMap.isMultipleValued(key);
			} else {
				String[] reqParam = requestMap.get(key);
				return reqParam != null && reqParam.length > 1;
			}
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
		
		
		
	}
}
 