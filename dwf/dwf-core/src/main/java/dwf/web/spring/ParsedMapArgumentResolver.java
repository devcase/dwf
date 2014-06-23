package dwf.web.spring;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
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
		return ParsedMap.class.isAssignableFrom(parameter.getParameterType());
	}

	@Override
	public Object resolveArgument(MethodParameter parameter,
			ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) throws Exception {
		String parameterPrefix = parameter.getParameterName().concat(".");
		Map<String, String[]> parameterMap = webRequest.getParameterMap();
		return new RequestMapWrapper(parameterMap, parameterPrefix, webRequest.getLocale());
	}

	/**
	 * Wraps a {@code Map<String, String[]>}. Delegates the {@link Map} methods to the
	 * wrapped map. The {@link ParsedMap} methods parses the correspondent value, according
	 * to the user locale.
	 * @author Hirata
	 *
	 */
	protected static class RequestMapWrapper implements ParsedMap, Map<String, Object> {
		private final Map<String, String[]> requestMap;
		private final Map<String, Object> newObjectMap; //mapa para colocar itens extras
		private final String keyPrefix;
		private final Locale locale;
		public RequestMapWrapper(Map<String, String[]> requestMap,
				String keyPrefix, Locale locale) {
			super();
			this.requestMap = Collections.unmodifiableMap(requestMap);
			this.newObjectMap = new HashMap<String, Object>();
			this.keyPrefix = keyPrefix;
			this.locale = locale;
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
			if(requestMap.containsKey(key) && requestMap.get(key).length != 0 && !StringUtils.isBlank(requestMap.get(key)[0])) {
				try {
					return NumberFormat.getInstance(this.locale).parse(requestMap.get(key)[0]).doubleValue();
				} catch (ParseException e) {
					throw new IllegalArgumentException("Passed key can't be parsed into a Double", e);
				}
			} else {
				return null;
			}			
		}

		@Override
		public Long getLong(String key) {
			if(newObjectMap.containsKey(key)) {
				return (Long) newObjectMap.get(key);
			}
			
			if(keyPrefix != null) key = keyPrefix + key;
			if(requestMap.containsKey(key) && requestMap.get(key).length != 0 && !StringUtils.isBlank(requestMap.get(key)[0])) {
				try {
					return NumberFormat.getInstance(this.locale).parse(requestMap.get(key)[0]).longValue();
				} catch (ParseException e) {
					throw new IllegalArgumentException("Passed key can't be parsed into a Long", e);
				}
			} else {
				return null;
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
				String paramValue = requestMap.get(key)[0].toLowerCase().trim();
				if ("true".equals(paramValue) || "yes".equals(paramValue)) {
					return Boolean.TRUE;
				} else if ("false".equals(paramValue) || "no".equals(paramValue)) { 
					return Boolean.FALSE;
				} else {
					throw new IllegalArgumentException("Passed key can't be parsed into a Boolean");
				}
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
			return get(key);
		}

		
		
		
	}
}
