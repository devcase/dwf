package dwf.persistence.dao;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import dwf.utils.ParsedMap;

public class DefaultQueryBuilder implements QueryBuilder {
	protected BaseDAOImpl<?> dao;
	
	public DefaultQueryBuilder(BaseDAOImpl<?> dao) {
		super();
		this.dao = dao;
	}

	/* (non-Javadoc)
	 * @see dwf.persistence.dao.QueryBuilder#createQuery(dwf.utils.ParsedMap, boolean, java.util.Map)
	 */
	@Override
	public String createQuery(ParsedMap filter, QueryReturnType<?> returnType, Map<String, Object> params) {

		StringBuilder query = new StringBuilder();
		
		if(returnType.isCount()) {
			query.append("select count(s.id) from ").append(dao.getEntityFullName()).append(" s ");
		} else {
			query.append("select ");
			returnType.appendSelectList(query, "s2");
			query.append(" from ").append(dao.getEntityFullName()).append(" s2 where s2.id in (select s from ").append(dao.getEntityFullName()).append(" s ");
		}

		appendJoins(filter, returnType, params, query, "s");

		query.append(" WHERE 1=1 ");

		appendConditions(filter, returnType, params, query, "s");

		
		if(!returnType.isCount()) {
			query.append(") ");
			appendOrderBy(filter, returnType, params, query, "s2");
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
	protected void appendOrderBy(ParsedMap filter, QueryReturnType<?> returnType, Map<String, Object> params, StringBuilder query, String domainAlias) {
		if(filter.containsKey("orderBy") && dao.hasPropertyWithName(filter.getString("orderBy"))) {
			query.append(" order by ").append(domainAlias).append(".").append( filter.getString("orderBy"));
			if(filter.containsKey("orderByDirection")) {
				//avoiding append to the hql the content directly from the filter
				query.append(" ").append(filter.getString("orderByDirection").toLowerCase().equals("desc") ? " DESC " : " ASC ");
			}
		} else {
			appendDefaultOrderBy(filter, returnType, params, query, "s2");
		}
		
	}
	
	protected void appendDefaultOrderBy(ParsedMap filter, QueryReturnType<?> returnType, Map<String, Object> params, StringBuilder query, String domainAlias) {
		query.append(" order by ").append(domainAlias).append(".autocompleteText");
	}
	
	/**
	 * Avoid appending contents directly from filter - it may cause an HQL injection security problem.
	 * @param filter
	 * @param count
	 * @param params
	 * @param query
	 */
	protected void appendJoins(ParsedMap filter, QueryReturnType<?> returnType, Map<String, Object> params, StringBuilder query, String domainAlias) {
	}

	/**
	 * Avoid appending contents directly from filter - it may cause an HQL injection security problem.
	 * @param filter
	 * @param count
	 * @param params
	 * @param query
	 */
	protected void appendConditions(ParsedMap filter, QueryReturnType<?> returnType, Map<String, Object> params, StringBuilder query, String domainAlias) {
		for ( PropertyDescriptor pDescriptor : dao.getPropertyList()) {
			String pName = pDescriptor.getName();
			if(filter.containsKey(pName)) {
				
				Object value = filter.get(pName, pDescriptor.getPropertyType());
				if(value != null && (value.getClass().isArray()) && Array.getLength(value) > 0) {
					//chegou array no filtro
					query.append(" and ").append(domainAlias).append(".").append(pName).append(" in (:").append(pName).append(") ");
				} else if(value != null && (value instanceof Collection<?>)) {
					//chegou collection no filtro
					query.append(" and ").append(domainAlias).append(".").append(pName).append(" in (:").append(pName).append(") ");
				} else {
					if(Collection.class.isAssignableFrom(pDescriptor.getPropertyType())) {
						//propriedade na entidade é collection
						query.append(" and :").append(pName).append(" member of ").append(domainAlias).append(".").append(pName);
					} else {
						query.append(" and ").append(domainAlias).append(".").append(pName).append(" = :").append(pName);
					}
				}
				params.put(pName, value);
			} else if(filter.containsKey(pName+ ".id")) {
				Long value = filter.getLong(pName+ ".id"); //TODO - Só funciona com Long!
				query.append(" and ").append(domainAlias).append(".").append(pName).append(".id = :").append(pName).append("Id ");
				params.put(pName + "Id", value);
			}
		}
		
		if(Boolean.TRUE.equals(filter.getBoolean("includeDisabled"))) {
		} else {
			query.append(" and ").append(domainAlias).append(".enabled = true ");
		}
		
		if(filter.containsKey("searchstring")) {
			query.append(" and lower(").append(domainAlias).append(".autocompleteText) like :searchstring ");
			params.put("searchstring", "%" + filter.getString("searchstring").trim() + "%");
		}
	}

}
