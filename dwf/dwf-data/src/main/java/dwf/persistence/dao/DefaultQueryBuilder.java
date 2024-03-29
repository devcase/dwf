package dwf.persistence.dao;

import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dwf.persistence.annotations.DefaultOrderBy;
import dwf.utils.ParsedMap;
import dwf.utils.SearchstringUtils;

public class DefaultQueryBuilder implements QueryBuilder {
	private Logger logger= LoggerFactory.getLogger(DefaultQueryBuilder.class);
	protected BaseDAOImpl<?> dao;
	protected Class<?> clazz;
	
	public DefaultQueryBuilder(BaseDAOImpl<?> dao) {
		super();
		this.dao = dao;
		this.clazz = dao.getEntityClass();
	}

	/* (non-Javadoc)
	 * @see dwf.persistence.dao.QueryBuilder#createQuery(dwf.utils.ParsedMap, boolean, java.util.Map)
	 */
	@Override
	public String createQuery(ParsedMap filter, QueryReturnType<?> returnType, Map<String, Object> params) {

		StringBuilder query = new StringBuilder();
		String domainAlias = returnType.insertBeforeSelect(query, dao.getEntityFullName(), params, filter);
		appendSelectIdsCommand(filter, returnType, params, query, "s");

		appendJoins(filter, returnType, params, query, "s");

		query.append(" WHERE 1=1 ");

		appendConditions(filter, returnType, params, query, "s");

		returnType.appendAfterSelect(query, dao.getEntityFullName(), params, filter);
		
		if(!returnType.isCount()) {
			appendOrderBy(filter, returnType, params, query, domainAlias);
		}
		
		String ret = query.toString();
		if(logger.isDebugEnabled()) {
			logger.debug(ret);
		}
		return ret;		
	}
	
	protected void appendSelectIdsCommand(ParsedMap filter, QueryReturnType<?> returnType, Map<String, Object> params, StringBuilder query, String domainAlias) {
		query.append("select ").append(domainAlias).append(".id from ").append(dao.getEntityFullName()).append(" ").append(domainAlias).append(" ");
	}
	
	
	
	/**
	 * Avoid appending contents directly from filter - it may cause an HQL injection security problem.
	 * @param filter
	 * @param count
	 * @param params
	 * @param query
	 */
	protected void appendOrderBy(ParsedMap filter, QueryReturnType<?> returnType, Map<String, Object> params, StringBuilder query, String domainAlias) {
		if(filter.containsKey("orderBy")) {
			String orderBy = filter.getString("orderBy");
			String orderByDirection = null;
			if(orderBy.toLowerCase().endsWith(" desc") || orderBy.toLowerCase().endsWith(" asc")) {
				orderByDirection = orderBy.toLowerCase().endsWith(" desc") ? "DESC" : "ASC";
				orderBy = orderBy.substring(0, orderBy.indexOf(" "));
			}
			if(filter.containsKey("orderByDirection")) {
				orderByDirection = filter.getString("orderByDirection").toLowerCase().equals("desc") ? " DESC " : " ASC ";
			}
			
			if(dao.hasPropertyWithName(orderBy)) {
				query.append(" order by ").append(domainAlias).append(".").append( orderBy );
				if(orderByDirection != null) {
					query.append(" ").append(orderByDirection);
				}
			}
			
		} else {
			appendDefaultOrderBy(filter, returnType, params, query, domainAlias);
		}
		
	}
	
	protected void appendDefaultOrderBy(ParsedMap filter, QueryReturnType<?> returnType, Map<String, Object> params, StringBuilder query, String domainAlias) {
		String defaultOrderBy = getDefaultOrderByExpression(clazz);
		if(defaultOrderBy == null) {
			query.append(" order by ").append(domainAlias).append(".autocompleteText");
		} else {
			query.append(" order by ").append(domainAlias).append(".").append(defaultOrderBy);
		}
	}
	
	private String getDefaultOrderByExpression(Class<?> entityClass) {
		if(entityClass.isAnnotationPresent(DefaultOrderBy.class)) {
			return entityClass.getAnnotation(DefaultOrderBy.class).value();
		} else if(entityClass.getSuperclass() != null) {
			return getDefaultOrderByExpression(entityClass.getSuperclass());
		} else {
			return null;
		}
	}
	
	/**
	 * Avoid appending contents directly from filter - it may cause an HQL injection security problem.
	 * @param filter
	 * @param count
	 * @param params
	 * @param query
	 */
	protected void appendJoins(ParsedMap filter, QueryReturnType<?> returnType, Map<String, Object> params, StringBuilder query, String domainAlias) {
		for ( PropertyDescriptor pDescriptor : dao.getPropertyList()) {
			if(join(filter, pDescriptor)) {
				String pName = pDescriptor.getName();
				query.append(" join ").append(domainAlias).append(".").append(pName).append(" joined").append(pName);
			}
		}
	}
	
	private boolean join(ParsedMap filter, PropertyDescriptor pDescriptor) {
		//join automático de coleções, se veio no filtro
		if(Collection.class.isAssignableFrom(pDescriptor.getPropertyType())) {
			String pName = pDescriptor.getName();
			if(filter.containsKey(pName) || filter.containsKey(pName + "[0].id") || filter.containsKey(pName + "[].id") || filter.containsKey(pName+ ".id")) {
				return true;
			}
		}
		return false;
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
			String ref = pName;
			boolean join = join(filter, pDescriptor);
			if(join) {
				ref = "joined".concat(pName);
			} else {
				ref = domainAlias.concat(".").concat(pName);
			}

			
			if(filter.isMultipleValued(pName)) {
				query.append(" and ").append(ref).append(" in (:").append(pName).append(") ");
				params.put(pName, filter.get(pName, pDescriptor.getPropertyType()));
			} else if(filter.containsKey(pName)) {
				Object value = filter.get(pName, pDescriptor.getPropertyType());
				if(value == null) {
					query.append(" and ").append(ref).append(" is null ");
				} else {
					if(Collection.class.isAssignableFrom(pDescriptor.getPropertyType()) && !join) {
						//propriedade na entidade é collection
						query.append(" and :").append(pName).append(" member of ").append(ref);
					} else {
						query.append(" and ").append(ref).append(" = :").append(pName);
					}
					params.put(pName, value);
				}
			} else if(filter.containsKey(pName+ ".id")) {
				Long value = filter.getLong(pName+ ".id"); //TODO - Só funciona com Long!
				if(value == null) {
					query.append(" and ").append(ref).append(" is null ");
				} else {
					query.append(" and ").append(ref).append(".id = :").append(pName).append("Id ");
					params.put(pName + "Id", value);
				}

			} else if(filter.containsKey(pName + "[0].id")){
				Long value = filter.getLong(pName + "[0].id");
				
				query.append(" and (");
				for(Integer i = 1; value != null; i++){
					query.append(ref).append(".id = :").append(pName).append("Id" + i + " ");
					params.put(pName + "Id" + i, value);
					value = filter.getLong(pName + "[" + i + "].id");
					if(value != null)
						query.append(" or ");
				}
				query.append(")");
			} else if(filter.containsKey(pName + "[].id")){
				Long[] values = filter.getLongArray(pName + "[].id");
				query.append(" and (");
				for (int j = 0; j < values.length; j++) {
					Long value = values[j];
					query.append(ref).append(".id = :").append(pName).append("Id" + j + " ");
					params.put(pName + "Id" + j, value);
					if((j+1) < values.length) {
						query.append(" or ");
					}
				}
				query.append(")");
			}

			
			//queries do tipo "função"
			if(filter.containsKey(pName + ".isnull")) {
				Boolean v = filter.getBoolean(pName + ".isnull");
				if(Boolean.TRUE.equals(v)) {
					query.append(" and ").append(ref).append(" is null ");
				} else if(Boolean.FALSE.equals(v)) {
					query.append(" and ").append(ref).append(" is not null ");
				}
			}
			if(filter.containsKey(pName + ".ornull")) { //igual ou nulo
				Object v = filter.get(pName + ".ornull", pDescriptor.getPropertyType());
				query.append(" and (").append(":").append(pName).append("_ornull = ").append(ref).append(" or ").append(ref).append(" is null").append(") ");
				params.put(pName + "_ornull", v);
			}
			if(filter.containsKey(pName + ".orempty")) { //igual ou vazio
				Object v = filter.get(pName + ".orempty", pDescriptor.getPropertyType());
				query.append(" and (").append(":").append(pName).append("_orempty member of ").append(ref).append(" or ").append(ref).append(" is empty").append(") ");
				params.put(pName + "_orempty", v);
			}
			if(filter.containsKey(pName + ".gt")) {
				Object v = filter.get(pName + ".gt", pDescriptor.getPropertyType());
				if(v != null) {
					query.append(" and ").append(ref).append(" > :").append(pName).append("_gt");
					params.put(pName + "_gt", v);
				}
			}
			if(filter.containsKey(pName + ".lt")) {
				Object v = filter.get(pName + ".lt", pDescriptor.getPropertyType());
				if(v != null) {
					query.append(" and ").append(ref).append(" < :").append(pName).append("_lt");
					params.put(pName + "_lt", v);
				}
			}
			if(filter.containsKey(pName + ".gteq")) {
				Object v = filter.get(pName + ".gteq", pDescriptor.getPropertyType());
				if(v != null) {
					query.append(" and ").append(ref).append(" >= :").append(pName).append("_gteq");
					params.put(pName + "_gteq", v);
				}
			}
			if(filter.containsKey(pName + ".lteq")) {
				Object v = filter.get(pName + ".lteq", pDescriptor.getPropertyType());
				if(v != null) {
					query.append(" and ").append(ref).append(" <= :").append(pName).append("_lteq");
					params.put(pName + "_lteq", v);
				}
			}
			if(filter.containsKey(pName + ".isempty")) {
				Boolean v = filter.getBoolean(pName + ".isempty");
				if(Boolean.TRUE.equals(v)) {
					query.append(" and ").append(ref).append(" is empty ");
				} else if(Boolean.FALSE.equals(v)) {
					query.append(" and ").append(ref).append(" is not empty ");
				}
			}
			if(filter.containsKey(pName + ".ignorecase")) {
				String v = filter.getString(pName + ".ignorecase");
				if(v != null) {
					query.append(" and lower(").append(ref).append(") = :").append(pName).append("_ignorecase");
					params.put(pName + "_ignorecase", v.toLowerCase());
				}
			}
			if(filter.containsKey(pName + ".ne")) {
				Object v = filter.get(pName + ".ne", pDescriptor.getPropertyType());
				if(v != null) {
					query.append(" and ").append(ref).append(" != :").append(pName).append("_ne");
					params.put(pName + "_ne", v);
				}
			}

		}
		
		if(Boolean.TRUE.equals(filter.getBoolean("includeDisabled")) || filter.containsKey("enabled")) { 
		} else {
			//by default - only enabled
			query.append(" and ").append(domainAlias).append(".enabled = true ");
		}
		
		if(filter.containsKey("searchstring") && StringUtils.isNotBlank(filter.getString("searchstring")) ) {
			boolean wildcardEnd = true;
			boolean wildcardStart = true;
			if(filter.containsKey("searchwildcards") && StringUtils.isNotBlank(filter.getString("searchwildcards")) ) {
				if(filter.getString("searchwildcards").toUpperCase().equals("NONE")) {
					wildcardStart = false;
					wildcardEnd = false;
				} else if(filter.getString("searchwildcards").toUpperCase().equals("BEFORE")) {
					wildcardStart = true;
					wildcardEnd = false;
				} else if(filter.getString("searchwildcards").toUpperCase().equals("AFTER")) {
					wildcardStart = false;
					wildcardEnd = true;
				} else if(filter.getString("searchwildcards").toUpperCase().equals("BOTH")) {
					wildcardStart = true;
					wildcardEnd = true;
				}
			}
			
			query.append(" and lower(").append(domainAlias).append(".autocompleteText) like :searchstring ");
			String searchString = SearchstringUtils.prepareForSearch(filter.getString("searchstring"));
			params.put("searchstring", (wildcardStart ? "%" : "").concat(searchString).concat(wildcardEnd ? "%": ""));
		}
	}

}
