package dwf.persistence.dao;

import java.util.Map;

import dwf.utils.ParsedMap;

public interface QueryBuilder {
	String createQuery(ParsedMap filter, QueryReturnType<?> returnType, Map<String, Object> params);

}
