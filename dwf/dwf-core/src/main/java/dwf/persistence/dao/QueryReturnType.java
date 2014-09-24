package dwf.persistence.dao;

public interface QueryReturnType<T> {
	static final QueryReturnType<Integer> COUNT = new QueryReturnType<Integer>() {
		@Override
		public boolean isCount() {
			return true;
		}
		
		@Override
		public void appendSelectList(StringBuilder queryBuilder, String domainAlias) {
		}
	};
	
	static class Factory {
		public static <T> QueryReturnType<T> domainQueryReturnType() {
			return (QueryReturnType<T>) DOMAIN;
		}
	}
	
	static final QueryReturnType<?> DOMAIN = new QueryReturnType<Object>() {
		@Override
		public boolean isCount() {
			return false;
		}
		
		@Override
		public void appendSelectList(StringBuilder queryBuilder, String domainAlias) {
			queryBuilder.append(domainAlias);
		}
	};
	
	static final QueryReturnType<Long> ID = new QueryReturnType<Long>() {
		@Override
		public boolean isCount() {
			return false;
		}
		
		@Override
		public void appendSelectList(StringBuilder queryBuilder, String domainAlias) {
			queryBuilder.append(domainAlias).append(".id");
		}
	};

	
	boolean isCount();
	void appendSelectList(StringBuilder queryBuilder, String domainAlias);
}
