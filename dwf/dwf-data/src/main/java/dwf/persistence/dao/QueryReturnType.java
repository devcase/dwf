package dwf.persistence.dao;

import java.util.Map;

import dwf.utils.ParsedMap;

public interface QueryReturnType<T> {
	
	static class Factory {
		@SuppressWarnings("unchecked")
		public static <T> QueryReturnType<T> domainQueryReturnType() {
			return (QueryReturnType<T>) DOMAIN;
		}
	}

	static final QueryReturnType<Integer> COUNT = new QueryReturnType<Integer>() {
		@Override
		public boolean isCount() {
			return true;
		}

		@Override
		public String prependSelect(StringBuilder query, String entityName, Map<String, Object> params, ParsedMap filter) {
			query.append("select count(s2.id) from ").append(entityName).append(" s2 where s2.id in (");
			return "s2";
		}
	};

	static final QueryReturnType<?> DOMAIN = new QueryReturnType<Object>() {
		@Override
		public boolean isCount() {
			return false;
		}

		@Override
		public String prependSelect(StringBuilder query, String entityName, Map<String, Object> params, ParsedMap filter) {
			query.append("select s2 from ").append(entityName).append(" s2 where s2.id in (");
			return "s2";
		}
	};
	
	static final QueryReturnType<Long> ID = new QueryReturnType<Long>() {
		@Override
		public boolean isCount() {
			return false;
		}
		
		@Override
		public String prependSelect(StringBuilder query, String entityName, Map<String, Object> params, ParsedMap filter) {
			query.append("select s2.id from ").append(entityName).append(" s2 where s2.id in (");
			return "s2";
		}

	};

	
	boolean isCount();
	String prependSelect(StringBuilder query, String entityName, Map<String, Object> params, ParsedMap filter);
}
