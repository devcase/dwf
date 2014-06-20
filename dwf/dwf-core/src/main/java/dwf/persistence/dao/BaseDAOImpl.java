package dwf.persistence.dao;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.groups.Default;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;

import dwf.activitylog.domain.UpdatedProperty;
import dwf.activitylog.service.ActivityLogService;
import dwf.persistence.annotations.HideActivityLogValues;
import dwf.persistence.annotations.NotEditableProperty;
import dwf.persistence.annotations.UpdatableProperty;
import dwf.persistence.domain.BaseEntity;
import dwf.utils.ParsedMap;
import dwf.validation.ValidationGroups;

/**
 * 
 * @author Hirata
 * 
 */
@Transactional()
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public abstract class BaseDAOImpl<D extends BaseEntity<?>>
		implements DAO<D> {
	
	private Log log = LogFactory.getLog(getClass());
	private final static Class<?>[] DEFAULT_VALIDATION_GROUP = {Default.class};

	@Autowired
	protected SessionFactory sessionFactory;
	@Autowired
	protected ActivityLogService activityLogService;
	@Autowired
	protected Validator beanValidator;

	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	protected final Class<D> clazz;
	protected final String entityName;

	/**
	 * 
	 */
	protected final List<Field> fieldsToTrim;
	/**
	 * Cache with UpdatableProperty annotations
	 */
	protected final Map<PropertyDescriptor, UpdatableProperty> updatableProperties;

	public BaseDAOImpl(Class<D> clazz) {
		super();
		this.clazz = clazz;
		this.entityName = clazz.getName();
		
		/**
		 * Find all string fields
		 */
		fieldsToTrim = new ArrayList<Field>();
		updatableProperties = new HashMap<PropertyDescriptor, UpdatableProperty>();
		processClazzFieldsRecursive(this.clazz);
	}
	
	/**
	 * Search for String fields to build the fieldsToTrim list.
	 * <p>Search for annotated fields with @{@link UpdatableProperty} annotation.
	 * @param cl
	 */
	private void processClazzFieldsRecursive(Class<?> cl) {
		if(cl.getSuperclass() != null) {
			processClazzFieldsRecursive(cl.getSuperclass());
		}
		
		for (final Field f : cl.getDeclaredFields()) {
			if(!Modifier.isStatic(f.getModifiers())) {
				if (f.getType().equals(String.class)) {
					fieldsToTrim.add(f);
				}
			}
		}
		for (final PropertyDescriptor p : PropertyUtils.getPropertyDescriptors(cl)) {
			
			if(p.getReadMethod().getAnnotation(UpdatableProperty.class) != null) {
				updatableProperties.put(p, p.getReadMethod().getAnnotation(UpdatableProperty.class));
			} else if(p.getReadMethod().getAnnotation(NotEditableProperty.class) != null || p.getWriteMethod() == null) {
				//propriedade readonly ou anotada com NotEditableProperty
			} else {
				updatableProperties.put(p, null);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public D findById(Serializable id) {
		return (D) getSession().get(clazz, id);
	}

	@Override
	public final List<?> findByFilter(ParsedMap filter, int offset,
			int pageSize) {
		Map<String, Object> params = new HashMap<String, Object>();
		String q = createQuery(filter, false, params);

		return findByPage(q, offset, pageSize, params);
	}
	
	

	/* (non-Javadoc)
	 * @see dwf.persistence.dao.DAO#findByFilter(dwf.utils.ParsedMap)
	 */
	@Override
	public List<?> findByFilter(ParsedMap filter) {
		return findByFilter(filter, 0, -1);
	}
	
	
	/* (non-Javadoc)
	 * @see dwf.persistence.dao.DAO#findAll()
	 */
	@Override
	public List<?> findAll() {
		return this.findByFilter(new ParsedMap() {

			/* (non-Javadoc)
			 * @see dwf.utils.ParsedMap#getString(java.lang.String)
			 */
			@Override
			public String getString(String key) {
				return null;
			}

			/* (non-Javadoc)
			 * @see dwf.utils.ParsedMap#getDouble(java.lang.String)
			 */
			@Override
			public Double getDouble(String key) {
				return null;
			}

			/* (non-Javadoc)
			 * @see dwf.utils.ParsedMap#getBoolean(java.lang.String)
			 */
			@Override
			public Boolean getBoolean(String key) {
				return null;
			}

			/* (non-Javadoc)
			 * @see dwf.utils.ParsedMap#getLong(java.lang.String)
			 */
			@Override
			public Long getLong(String key) {
				return null;
			}

			/* (non-Javadoc)
			 * @see dwf.utils.ParsedMap#containsKey(java.lang.String)
			 */
			@Override
			public boolean containsKey(String key) {
				return false;
			}

			/* (non-Javadoc)
			 * @see dwf.utils.ParsedMap#put(java.lang.String, java.lang.Object)
			 */
			@Override
			public Object put(String key, Object value) {
				return null;
			}
			
		});
	}

	@Override
	public int countByFilter(ParsedMap filter) {
		Map<String, Object> params = new HashMap<String, Object>();
		String q = createQuery(filter, true, params);
		return count(q, params);
	}

	public final List<?> findByPage(String hql, int offset, int fetchSize,
			Map<String, Object> params) {
		Query query = getSession().createQuery(hql);
		for (Entry<String, Object> entry : params.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		query.setFirstResult(offset);
		if(fetchSize >= 0)
			query.setMaxResults(fetchSize);
		return query.list();
	}

	public final int count(String hql, Map<String, Object> values) {
		Query query = getSession().createQuery(hql);
		for (Entry<String, Object> entry : values.entrySet()) {
			query.setParameter(entry.getKey(), entry.getValue());
		}
		Object count = query.uniqueResult();
		if (count != null && count instanceof Number) {
			return ((Number) count).intValue();
		}
		throw new RuntimeException("Invalid hql for counting: " + hql);
	}

	@Override
	@Transactional(rollbackFor=ValidationException.class)
	public D saveNew(D entity) throws ValidationException {
		trimAllStringFields(entity);
		validate(entity,ValidationGroups.MergePersist.class);
		validate(entity); //valida campos sem grupos definidos
		entity.setUpdateTime(new Date());
		getSession().persist(entity);
		activityLogService.log(entity, "saveNew");
		return entity;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(rollbackFor=ValidationException.class)
	public D merge(D entity) throws ValidationException {
		trimAllStringFields(entity);
		validate(entity,ValidationGroups.MergePersist.class);
		validate(entity); //valida campos sem grupos definidos
		entity.setUpdateTime(new Date());
		entity = (D) getSession().merge(entity);
		activityLogService.log(entity, "merge");
		return entity;
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(rollbackFor=ValidationException.class)
	public D updateByAnnotation(D entity, Class<?>... groups) throws ValidationException {
		trimAllStringFields(entity);
		validate(entity, groups);

		D x = findById(entity.getId());
		if(x == null)
			throw new IllegalArgumentException("Id must be not-null");
		//getSession().evict(retrievedEntity);
		D retrievedEntity;
		try {
			retrievedEntity = clazz.newInstance();
			BeanUtils.copyProperties(retrievedEntity, x);
		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e1) {
			throw new RuntimeException(e1);
		}
		
		List<UpdatedProperty> updatedProperties = new ArrayList<UpdatedProperty>();
		
		for (final PropertyDescriptor property : this.updatableProperties.keySet()) {
			try {
				UpdatableProperty annotation = this.updatableProperties.get(property);
				
				
				if(checkUpdateGroup(annotation, groups)) {
					Object value = PropertyUtils.getSimpleProperty(entity, property.getName());
					Object oldValue = PropertyUtils.getSimpleProperty(retrievedEntity, property.getName());
					
					if(value == null) {
						if(oldValue == null) continue; //dois nulos - não troca
					} else {
						if(value.equals(oldValue)) continue; //dois iguais - não troca
					}
					
					UpdatedProperty up = new UpdatedProperty();
					if(property.getReadMethod().getAnnotation(HideActivityLogValues.class) != null) {
						up.setHiddenValues(true);
					} else {
						up.setNewValue(value != null ? value.toString() : "-");
						up.setOldValue(oldValue != null ? oldValue.toString() : "-");
						up.setHiddenValues(false);
					}
					up.setPropertyName(property.getName());
					updatedProperties.add(up);
					BeanUtils.copyProperty(retrievedEntity, property.getName(), value);
				}
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				//Error copying the property
				throw new RuntimeException(e);
			}
		}
		retrievedEntity.setUpdateTime(new Date());
		
		activityLogService.logEntityUpdate(entity, entity, updatedProperties, groups);

		return (D) getSession().merge(retrievedEntity);
	}

	@Override
	public void delete(D entity, String comment) {
		D connectedEntity = findById(entity.getId());
		if (connectedEntity.isEnabled()) {
			activityLogService.log(entity, "delete", comment);
			connectedEntity.setEnabled(false);
		}
	}

	@Override
	public D restore(D entity, String comment) {
		D connectedEntity = findById(entity.getId());
		if (!connectedEntity.isEnabled()) {
			activityLogService.log(entity, "restore", comment);
			connectedEntity.setEnabled(true);
		}
		return connectedEntity;
	}

	/**
	 * @param entity
	 * @throws ValidationException
	 */
	public void validate(D entity, Class<?>... groups) throws ValidationException {
		Set<ConstraintViolation<D>> violations = beanValidator.validate(entity, groups);
		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(violations);
		}
	}

	protected String createQuery(ParsedMap filter, boolean count, Map<String, Object> params) {
		StringBuilder query = new StringBuilder();
		
		if(count) {
			query.append("select count(s.id) from " + this.entityName + " s ");
		} else {
			query.append("select s from " + this.entityName + " s ");
		}
		
		query.append(" WHERE 1=1 ");
		if(Boolean.TRUE.equals(filter.getBoolean("includeDisabled"))) {
		} else {
//			query.append(" and s.enabled = true ");
		}
		
		return query.toString();
	}

	/**
	 * 
	 * @param entity
	 */
	protected void trimAllStringFields(D entity) {
		for (final Field fieldToTrim : fieldsToTrim) {
			String propertyValue;
			try {
				propertyValue = (String) BeanUtils.getProperty(entity, fieldToTrim.getName());
				if(propertyValue == null) {
					continue;
				} else {
					propertyValue = propertyValue.trim();
					if(propertyValue.length() == 0)
						propertyValue = null;
				}
				BeanUtils.setProperty(entity, fieldToTrim.getName(), propertyValue);
			} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignore) {
				throw new RuntimeException(ignore);
			}

		}
	}
	
	/**
	 * 
	 * @param annotation
	 * @param selectedGroups
	 * @return
	 */
	private boolean checkUpdateGroup(UpdatableProperty annotation, Class<?>... selectedGroups) {
		if(annotation == null) {
			return selectedGroups == null || selectedGroups.length == 0;
		}
		if((annotation.groups() == null || annotation.groups().length == 0) && 
				(selectedGroups == null || selectedGroups.length == 0)) { 
			return true;
		} else {
			if(selectedGroups == null) {
				selectedGroups = DEFAULT_VALIDATION_GROUP;
			}
			for (Class<?> selectedGroup : selectedGroups) {
				for (Class<?> annotationGroup : annotation.groups()) {
					if(selectedGroup.equals(annotationGroup)) {
						return true;
					}
				}
			}
			return false;
		}
	}
}
