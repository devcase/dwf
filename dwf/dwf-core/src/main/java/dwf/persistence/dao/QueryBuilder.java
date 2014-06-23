package dwf.persistence.dao;

import java.util.Map;

import dwf.utils.ParsedMap;

public interface QueryBuilder {
	String createQuery(ParsedMap filter, boolean count, Map<String, Object> params);

}
