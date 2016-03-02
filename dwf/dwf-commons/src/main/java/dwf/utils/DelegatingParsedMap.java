package dwf.utils;

import java.util.Date;


public class DelegatingParsedMap implements ParsedMap {
	private final ParsedMap a;
	private final ParsedMap b;
	
	public DelegatingParsedMap(ParsedMap a, ParsedMap b) {
		super();
		this.a = a;
		this.b = b;
	}

	@Override
	public String getString(String key) {
		if(a.containsKey(key)) return a.getString(key);
		else return b.getString(key);
	}

	@Override
	public Double getDouble(String key) {
		if(a.containsKey(key)) return a.getDouble(key);
		else return b.getDouble(key);
	}

	@Override
	public Boolean getBoolean(String key) {
		if(a.containsKey(key)) return a.getBoolean(key);
		else return b.getBoolean(key);
	}

	@Override
	public Long getLong(String key) {
		if(a.containsKey(key)) return a.getLong(key);
		else return b.getLong(key);
	}

	@Override
	public Date getDate(String key) {
		if(a.containsKey(key)) return a.getDate(key);
		else return b.getDate(key);
	}

	@Override
	public boolean containsKey(String key) {
		if(a.containsKey(key)) return true;
		else return b.containsKey(key);
	}

	@Override
	public Object get(String key) {
		if(a.containsKey(key)) return a.get(key);
		else return b.get(key);
	}

	@Override
	public Object put(String key, Object value) {
		return b.put(key, value);
	}

	@Override
	public <T> Object get(String key, Class<T> expectedClass) {
		if(a.containsKey(key)) return a.get(key, expectedClass);
		else return b.get(key, expectedClass);
	}
}
