package dwf.persistence.dao;

import java.beans.PropertyDescriptor;
import java.util.Map;

import org.apache.commons.lang3.StringEscapeUtils;

import dwf.utils.ParsedMap;

public class BaseQueryBuilder implements QueryBuilder {
	protected BaseDAOImpl<?> dao;
	
	public BaseQueryBuilder(BaseDAOImpl<?> dao) {
		super();
		this.dao = dao;
	}

	/* (non-Javadoc)
	 * @see dwf.persistence.dao.QueryBuilder#createQuery(dwf.utils.ParsedMap, boolean, java.util.Map)
	 */
	@Override
	public String createQuery(ParsedMap filter, boolean count, Map<String, Object> params) {

		StringBuilder query = new StringBuilder();
		
		if(count) {
			query.append("select count(s.id) from ").append(dao.getEntityName()).append(" s ");
		} else {
			query.append("select s2 from ").append(dao.getEntityName()).append(" s2 where s2.id in (select s from ").append(dao.getEntityName()).append(" s ");
		}

		appendJoins(filter, count, params, query);

		query.append(" WHERE 1=1 ");

		appendConditions(filter, count, params, query);

		
		if(!count) {
			query.append(") ");
			appendOrderBy(filter, count, params, query);
		}
		
		return query.toString();		
	}
	
	/**
	 * Avoid appending contents directly from filter - it may cause an HQL injection security problem.
	 * @param filter
	 * @param count
	 * @param params
	 * @param query
	 */
	protected void appendOrderBy(ParsedMap filter, boolean count, Map<String, Object> params, StringBuilder query) {
		if(filter.containsKey("orderBy") && dao.hasPropertyWithName(filter.getString("orderBy"))) {
			query.append(" order by s2.").append( filter.getString("orderBy"));
			if(filter.containsKey("orderByDirection")) {
				//avoiding append to the hql the content directly from the filter
				query.append(" ").append(filter.getString("orderByDirection").toLowerCase().equals("desc") ? " DESC " : " ASC ");
			}
		}
		
	}
	
	/**
	 * Avoid appending contents directly from filter - it may cause an HQL injection security problem.
	 * @param filter
	 * @param count
	 * @param params
	 * @param query
	 */
	protected void appendJoins(ParsedMap filter, boolean count, Map<String, Object> params, StringBuilder query) {
	}

	/**
	 * Avoid appending contents directly from filter - it may cause an HQL injection security problem.
	 * @param filter
	 * @param count
	 * @param params
	 * @param query
	 */
	protected void appendConditions(ParsedMap filter, boolean count, Map<String, Object> params, StringBuilder query) {
		for (String pName : dao.getPropertyNames()) {
			if(filter.containsKey(pName)) {
				query.append(" and s.").append(pName).append(" = :").append(pName);
				params.put(pName, filter.get(pName));
			}
		}
		
		if(Boolean.TRUE.equals(filter.getBoolean("includeDisabled"))) {
		} else {
			query.append(" and s.enabled = true ");
		}
	}

}
