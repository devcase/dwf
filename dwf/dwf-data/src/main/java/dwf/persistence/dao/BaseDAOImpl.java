package dwf.persistence.dao;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.Transient;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.groups.Default;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.NaturalIdLoadAccess;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.ListType;
import org.hibernate.type.MapType;
import org.hibernate.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;

import dwf.activitylog.domain.UpdatedProperty;
import dwf.activitylog.service.ActivityLogService;
import dwf.persistence.annotations.CascadeDelete;
import dwf.persistence.annotations.ConditionalGroup;
import dwf.persistence.annotations.EntityStateValidator;
import dwf.persistence.annotations.FillWithCurrentUser;
import dwf.persistence.annotations.HideActivityLogValues;
import dwf.persistence.annotations.IgnoreActivityLog;
import dwf.persistence.annotations.Image;
import dwf.persistence.annotations.NotEditableProperty;
import dwf.persistence.annotations.UpdatableProperty;
import dwf.persistence.domain.BaseEntity;
import dwf.persistence.utils.NotSyncPropertyDescriptor;
import dwf.persistence.validation.ValidationGroups;
import dwf.upload.UploadManager;
import dwf.upload.image.ImageResizer;
import dwf.user.DwfUserUtils;
import dwf.utils.ParsedMap;
import dwf.utils.SimpleParsedMap;
import javassist.bytecode.SignatureAttribute.ArrayType;

/**
 * 
 * @author Hirata
 * 
 */
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@Transactional
public abstract class BaseDAOImpl<D extends BaseEntity<? extends Serializable>> implements DAO<D> {
	private Log log = LogFactory.getLog(getClass());

	private final static Class<?>[] DEFAULT_VALIDATION_GROUP = { Default.class };

	@Autowired
	protected SessionFactory sessionFactory;
	@Autowired
	protected ActivityLogService activityLogService;
	@Autowired
	protected Validator beanValidator;
	@Autowired(required = false)
	private UploadManager uploadManager;
	@Autowired(required = false)
	private ImageResizer imageResizer;

	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	protected final Class<D> clazz;
	protected final String entityFullName;
	protected final String entityName;

	/**
	 * 
	 */
	protected final List<Field> fieldsToTrim;
	/**
	 * Cache with UpdatableProperty annotations
	 */
	protected final Map<NotSyncPropertyDescriptor, UpdatableProperty> updatableProperties;
	protected final Map<String, PropertyDescriptor> entityProperties;

	/**
	 * Cache com os setters anotados com @FillWithCurrentUser
	 */
	protected final Map<NotSyncPropertyDescriptor, FillWithCurrentUser> filledWithUser;
	private List<NotSyncPropertyDescriptor> propertyList;
	private Set<String> propertyNames;
	private final Set<String> readAndWritePropertyNames;
	private Set<NotSyncPropertyDescriptor> cascadeDeleteProperties;

	public BaseDAOImpl(Class<D> clazz) {
		super();
		this.clazz = clazz;
		this.entityFullName = clazz.getName();
		this.entityName = StringUtils.uncapitalize(clazz.getSimpleName());

		/**
		 * Find all string fields
		 */
		fieldsToTrim = new ArrayList<Field>();
		updatableProperties = new HashMap<NotSyncPropertyDescriptor, UpdatableProperty>();
		this.propertyList = new ArrayList<NotSyncPropertyDescriptor>();
		this.propertyNames = new HashSet<String>();
		this.readAndWritePropertyNames = new HashSet<String>();
		this.filledWithUser = new HashMap<NotSyncPropertyDescriptor, FillWithCurrentUser>();
		this.entityProperties = new HashMap<String, PropertyDescriptor>();
		this.cascadeDeleteProperties = new HashSet<NotSyncPropertyDescriptor>();
		try {
			processClazzPropertiesRecursive(this.clazz);
		} catch (IllegalAccessException | InvocationTargetException | IntrospectionException e) {
			throw new RuntimeException("Couldn't create the DAO. Check the Entity configuration.", e);
		}

	}

	/**
	 * Search for String fields to build the fieldsToTrim list.
	 * <p>
	 * Search for annotated fields with @{@link UpdatableProperty} annotation.
	 * 
	 * @param cl
	 * @throws IntrospectionException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private void processClazzPropertiesRecursive(Class<?> cl) throws IllegalAccessException, InvocationTargetException, IntrospectionException {
		if (cl.getSuperclass() != null) {
			processClazzPropertiesRecursive(cl.getSuperclass());
		}

		for (final Field f : cl.getDeclaredFields()) {
			if (!Modifier.isStatic(f.getModifiers())) {
				if (f.getType().equals(String.class)) {
					fieldsToTrim.add(f);
				}
			}
		}

		for (PropertyDescriptor p1 : PropertyUtils.getPropertyDescriptors(cl)) {
			NotSyncPropertyDescriptor p = new NotSyncPropertyDescriptor(p1);
			Method writeMethod = p.getWriteMethod();
			Method readMethod = p.getReadMethod();
			

			if (readMethod.isAnnotationPresent(Transient.class) && !readMethod.isAnnotationPresent(UpdatableProperty.class)) {
				// ignorar propriedades transientes
				// usado para dar override (ex: override do UpdatableProperty do getName de BaseMultilangEntity)				
				continue;
			}
			
			if (readMethod.isAnnotationPresent(CascadeDelete.class)) {
				cascadeDeleteProperties.add(p);
			}

			propertyList.add(p);
			propertyNames.add(p.getName());
			entityProperties.put(p.getName(), p);

			if (writeMethod != null && readMethod != null) {
				readAndWritePropertyNames.add(p.getName());
			}

			if (writeMethod != null && readMethod.getAnnotation(FillWithCurrentUser.class) != null) {
				filledWithUser.put(p, readMethod.getAnnotation(FillWithCurrentUser.class));
			}

			if (readMethod.getAnnotation(UpdatableProperty.class) != null) {
				updatableProperties.put(p, readMethod.getAnnotation(UpdatableProperty.class));
			} else if (readMethod.getAnnotation(NotEditableProperty.class) != null || writeMethod == null) {
				// propriedade readonly ou anotada com NotEditableProperty
			} else {
				updatableProperties.put(p, null);
			}
		}
	}

	@Override
	public D find(D copyWithId) {
		if (copyWithId == null || copyWithId.getId() == null) {
			// TODO - naturalids?
			return findByNaturalId(copyWithId);
		} else {
			return findById(copyWithId.getId());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public D findById(Serializable id) {
		D e = (D) getSession().byId(clazz).load(id);
		if(e != null) {
			getSession().setReadOnly(e, true);
		}
		return e;
	}

	@SuppressWarnings("unchecked")
	public D retrieveCopy(Serializable id) {
		StatelessSession ss = sessionFactory.openStatelessSession(((SessionImplementor) getSession()).connection());
		return (D) ss.get(clazz, id);
	}

	@Override
	public final List<D> findByFilter(ParsedMap filter, int offset, int pageSize) {
		QueryReturnType<D> qrt = QueryReturnType.Factory.domainQueryReturnType();
		return findByFilter(filter, qrt, offset, pageSize);
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> findByFilter(ParsedMap filter, QueryReturnType<T> returnType, int offset, int pageSize) {
		Map<String, Object> params = new HashMap<String, Object>();
		String q = createQuery(filter, returnType, params);

		return (List<T>) findByPage(q, offset, pageSize, params);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dwf.persistence.dao.DAO#findByFilter(dwf.utils.ParsedMap)
	 */
	@Override
	public List<D> findByFilter(ParsedMap filter) {
		return findByFilter(filter, 0, -1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dwf.persistence.dao.DAO#findByFilter(java.lang.Object[])
	 */
	@Override
	public List<D> findByFilter(Object... params) {
		return findByFilter(new SimpleParsedMap(params));
	}

	@Override
	public int countByFilter(Object... params) {
		return countByFilter(new SimpleParsedMap(params));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dwf.persistence.dao.DAO#findFirstByFilter(java.lang.Object[])
	 */
	@Override
	public D findFirstByFilter(Object... params) {
		return findFirstByFilter(new SimpleParsedMap(params));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dwf.persistence.dao.DAO#findFirstByFilter(java.lang.Object[])
	 */
	@SuppressWarnings("unchecked")
	@Override
	public D findFirstByFilter(ParsedMap filter) {
		List<?> list = findByFilter(filter, 0, 1);
		if (list.isEmpty())
			return null;
		else
			return (D) list.get(0);
	}

	@Override
	public <T> T findFirstByFilter(ParsedMap filter, QueryReturnType<T> returnType) {
		List<T> list = findByFilter(filter, returnType, 0, 1);
		if (list.isEmpty())
			return null;
		else
			return list.get(0);
	}

	@Override
	public <T> T findFirstByFilter(QueryReturnType<T> returnType, Object... params) {
		return findFirstByFilter(new SimpleParsedMap(params), returnType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dwf.persistence.dao.DAO#findAll()
	 */
	@Override
	public List<D> findAll() {
		return this.findByFilter(new ParsedMap() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see dwf.utils.ParsedMap#getString(java.lang.String)
			 */
			@Override
			public String getString(String key) {
				return null;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see dwf.utils.ParsedMap#getDouble(java.lang.String)
			 */
			@Override
			public Double getDouble(String key) {
				return null;
			}

			@Override
			public Date getDate(String key) {
				return null;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see dwf.utils.ParsedMap#getBoolean(java.lang.String)
			 */
			@Override
			public Boolean getBoolean(String key) {
				return null;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see dwf.utils.ParsedMap#getLong(java.lang.String)
			 */
			@Override
			public Long getLong(String key) {
				return null;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see dwf.utils.ParsedMap#containsKey(java.lang.String)
			 */
			@Override
			public boolean containsKey(String key) {
				return false;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see dwf.utils.ParsedMap#put(java.lang.String, java.lang.Object)
			 */
			@Override
			public Object put(String key, Object value) {
				return null;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see dwf.utils.ParsedMap#get(java.lang.String)
			 */
			@Override
			public Object get(String key) {
				return null;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see dwf.utils.ParsedMap#get(java.lang.String, java.lang.Class)
			 */
			@Override
			public <T> Object get(String key, Class<T> expectedClass) {
				return null;
			}

		});
	}

	@Override
	public int countByFilter(ParsedMap filter) {
		Map<String, Object> params = new HashMap<String, Object>();
		String q = createQuery(filter, QueryReturnType.COUNT, params);
		return count(q, params);
	}

	public final List<?> findByPage(String hql, int offset, int fetchSize, Map<String, Object> params) {
		Query query = getSession().createQuery(hql).setReadOnly(true);
		for (Entry<String, Object> entry : params.entrySet()) {
			Object value = entry.getValue();
			if (value != null && value.getClass().isArray()) {
				query.setParameterList(entry.getKey(), (Object[]) value);
			} else if (value != null && (value instanceof Collection<?>)) {
				query.setParameterList(entry.getKey(), (Collection<?>) value);
			} else {
				query.setParameter(entry.getKey(), value);
			}
		}
		query.setFirstResult(offset);
		if (fetchSize >= 0)
			query.setMaxResults(fetchSize);
		return query.list();
	}

	public final int count(String hql, Map<String, Object> params) {
		Query query = getSession().createQuery(hql).setReadOnly(true);
		for (Entry<String, Object> entry : params.entrySet()) {
			Object value = entry.getValue();
			if (value != null && value.getClass().isArray()) {
				query.setParameterList(entry.getKey(), (Object[]) value);
			} else if (value != null && (value instanceof Collection<?>)) {
				query.setParameterList(entry.getKey(), (Collection<?>) value);
			} else {
				query.setParameter(entry.getKey(), value);
			}
		}
		Object count = query.uniqueResult();
		if (count != null && count instanceof Number) {
			return ((Number) count).intValue();
		}
		if (count == null)
			return 0;
		throw new RuntimeException("Invalid hql for counting: " + hql);
	}

	@Override
	@Transactional(rollbackFor = ValidationException.class)
	public D saveNew(D entity) throws ValidationException {
		prepareEntity(entity);
		validate(entity, ValidationGroups.MergePersist.class);
		validate(entity); // valida campos sem grupos definidos
		if (findByNaturalId(entity) != null) {
			throw new ValidationException("NaturalId repetido");
		}
		entity.setUpdateTime(new Date());
		entity.setCreationTime(new Date());
		getSession().persist(entity);
		activityLogService.log(entity, ActivityLogService.OPERATION_CREATE);
		return entity;
	}

	@Override
	@Transactional(rollbackFor = ValidationException.class)
	public D importFromFile(D entity) throws ValidationException {
		if (entity.getId() == null) {
			setIdForImport(entity);
		}
		prepareEntity(entity);
		validate(entity, ValidationGroups.ImportFromFile.class);

		D existent = entity.getId() != null ? findById(entity.getId()) : null;

		if (existent == null && entity.getId() != null) {
			entity.setId(null);
		}

		if (existent == null) {
			// novo
			entity = saveNew(entity);
		} else {
			// entity
			updateByAnnotation(entity, ValidationGroups.ImportFromFile.class);
		}
		return entity;
	}

	/**
	 * TODO - comentar!!!!!
	 * 
	 * @param entity
	 * @return
	 */
	protected void setIdForImport(D entity) {
	}
	
	public D updateByAnnotation(D entity, Class<?>... groups) throws ValidationException {
		return updateByAnnotation(entity, false, groups);
	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(rollbackFor = ValidationException.class)
	public D updateByAnnotation(D entity, boolean ignoreNulls, Class<?>... groups) throws ValidationException {
		prepareEntity(entity);
		validate(entity, groups);

		D retrievedEntity = find(entity);
		if(entity == retrievedEntity) {
			log.warn("Passed entity is connected - evicting it from the Session and retrieving a new instance");
			evict(entity);
			retrievedEntity = find(entity);
		}
		if (retrievedEntity == null) {
			throw new IllegalArgumentException("Id must be not-null");
		}

		if (groups != null) {
			for (Class<?> validGrpClazz : groups) {
				if (validGrpClazz.isAnnotationPresent(ConditionalGroup.class)) {
					//A ConditionalGroup class may implement EntityStateValidator. The validateState method may throw 
					//a ValidationException, cancelling any changes to the entity
					try {
						ConditionalGroup cg = validGrpClazz.getAnnotation(ConditionalGroup.class);
						if (cg.validatedBy() != null) {
							for (Class<? extends EntityStateValidator<?>> stateValidator : cg.validatedBy()) {
								EntityStateValidator<BaseEntity<?>> instance = (EntityStateValidator<BaseEntity<?>>) stateValidator.newInstance();
								instance.validateState(retrievedEntity);
							}
						}
					} catch (InstantiationException | IllegalAccessException e) {
						throw new RuntimeException(e);
					}
				}
			}
		}

		// initializes properties before eviction
		for (final PropertyDescriptor property : this.updatableProperties.keySet()) {
			UpdatableProperty annotation = this.updatableProperties.get(property);
			if (checkUpdateGroup(annotation, groups)) {
				try {
					Object value = PropertyUtils.getSimpleProperty(retrievedEntity, property.getName());
					Hibernate.initialize(value);
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					// Error copying the property
					throw new RuntimeException(e);
				}
			}
		}

		List<UpdatedProperty> loggedUpdatedProperties = new ArrayList<UpdatedProperty>();

		
		ClassMetadata cm = sessionFactory.getClassMetadata(clazz);
		

		for (final PropertyDescriptor property : this.updatableProperties.keySet()) {
			try {
				UpdatableProperty annotation = this.updatableProperties.get(property);
				if (checkUpdateGroup(annotation, groups)) {
					Type t = cm.getPropertyType(property.getName()); 
					
					Object value = PropertyUtils.getSimpleProperty(entity, property.getName());
					Object oldValue = PropertyUtils.getSimpleProperty(retrievedEntity, property.getName());

					if(value == null && ignoreNulls) {
						continue;
					}
					
//					if(value != null && t.isEntityType()  && !getSession().contains(value)) {
//						//it's an entity - retrieving connected value
//						ClassMetadata propertyCM = sessionFactory.getClassMetadata(t.getReturnedClass());
//						if(propertyCM != null) {
//							Serializable id = propertyCM.getIdentifier(value, (org.hibernate.engine.spi.SessionImplementor) getSession());
//							String entityName = propertyCM.getEntityName();
//							value = getSession().get(entityName, id);
//						}
//					} else if(t.isCollectionType() && t.isAssociationType()){
//						//é coleção de entidades - recuperar cada entidade
//						
//					}
					
					boolean isCollection = t.isCollectionType();
					if(isCollection) {
						boolean isList = t instanceof ListType;
						boolean isMap = t instanceof MapType;
						boolean isArray = t instanceof ArrayType;
						//TODO - outros tipos de coleções
						boolean isOldValueEmpty = oldValue == null ? true : isList ? ((List) oldValue).isEmpty() :  isMap? ((Map) oldValue).isEmpty() : ((Collection<?>) oldValue).isEmpty();
						
						if(value == null) {
							if(oldValue == null || isOldValueEmpty) {
								continue;
							}
						} else {
							if(isArray) {
								//
								if (ArrayUtils.isEquals((Array) value, (Array) oldValue)) {
									continue;
								}
							} else if (isList) {
								//TODO Listas - detectar se faz diferença a ordem
								if(CollectionUtils.isEqualCollection((Collection<?>) value, (Collection<?>) oldValue)) { //Desconsidera a ordem
									continue;
								}
							} else {
								if(value.equals(oldValue)) { //pode não funcionar, dependendo da implementação de equals (funciona para AbstractMap e AbstractList)
									continue;
								}
							}
						}
					} else {
						if (value == null) {
							if (oldValue == null) {
								continue; // dois nulos - não troca
							}
						} else {
							if (value.equals(oldValue)) {
								continue; // dois iguais - não troca
							}
						}
					}


					UpdatedProperty up = new UpdatedProperty();
					if (property.getReadMethod().getAnnotation(IgnoreActivityLog.class) == null) {
						if (property.getReadMethod().getAnnotation(HideActivityLogValues.class) != null) {
							up.setHiddenValues(true);
						} else if(t.isAssociationType()) {
							up.setNewValue(value != null ? t.toLoggableString(value, (SessionFactoryImplementor) sessionFactory) : "-");
							up.setOldValue(oldValue != null ? t.toLoggableString(oldValue, (SessionFactoryImplementor) sessionFactory) : "-");
							up.setHiddenValues(false);
						} else {
							up.setNewValue(value != null ? value.toString() : "-");
							up.setOldValue(oldValue != null ? oldValue.toString() : "-");
							up.setHiddenValues(false);
						}
						up.setPropertyName(property.getName());
						loggedUpdatedProperties.add(up);
					}
					
					BeanUtils.copyProperty(retrievedEntity, property.getName(), value);
				}
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				// Error copying the property
				throw new RuntimeException(e);
			}
		}
		
		if(!loggedUpdatedProperties.isEmpty()) {
			activityLogService.logEntityUpdate(entity, loggedUpdatedProperties, groups);
		}
		retrievedEntity.setUpdateTime(new Date());
		getSession().update(retrievedEntity);
		return retrievedEntity;

	}

	@Override
	@Transactional()
	public void delete(D entity, String comment) {
		D connectedEntity = findById(entity.getId());
		if (connectedEntity.isEnabled()) {
			activityLogService.log(entity, ActivityLogService.OPERATION_DELETE, comment);
			connectedEntity.setEnabled(false);
		}
		//delete em cascata
		for (NotSyncPropertyDescriptor pd : this.cascadeDeleteProperties) {
			Object value;
			try {
				value = PropertyUtils.getSimpleProperty(entity, pd.getName());
				if(value != null) {
					if(value instanceof BaseEntity<?> && ((BaseEntity<?>) value).isEnabled()) {
						activityLogService.log((BaseEntity<?>) value, ActivityLogService.OPERATION_CASCADE_DELETE, comment);
						((BaseEntity<?>) value).setEnabled(false);
					} else if (value instanceof Collection) {
						for (Object obj : (Collection<?>) value) {
							if(obj instanceof BaseEntity<?> && ((BaseEntity<?>) obj).isEnabled()) {
								activityLogService.log((BaseEntity<?>) obj, ActivityLogService.OPERATION_CASCADE_DELETE, comment);
								((BaseEntity<?>) obj).setEnabled(false);
							}
						}
					}
				} 
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	@Transactional()
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

	/**
	 * You may choose between override this method or createQuery()
	 * 
	 * @return
	 */
	protected QueryBuilder createQueryBuilder() {
		return new DefaultQueryBuilder(this);
	}

	/**
	 * Called before any validation or persistence
	 * 
	 * @param entity
	 */
	protected void prepareEntity(D entity) {
		try {

			// Trim string fields
			for (final Field fieldToTrim : fieldsToTrim) {
				String propertyValue;
				propertyValue = (String) BeanUtils.getProperty(entity, fieldToTrim.getName());
				if (propertyValue == null) {
					continue;
				} else {
					propertyValue = propertyValue.trim();
					if (propertyValue.length() == 0)
						propertyValue = null;
				}
				BeanUtils.setProperty(entity, fieldToTrim.getName(), propertyValue);
			}

			// @FillWithCurrentUser
			String currentUserId = DwfUserUtils.getCurrentUserId();
			if (currentUserId != null) {
				for (Map.Entry<NotSyncPropertyDescriptor, FillWithCurrentUser> prop : this.filledWithUser.entrySet()) {
					if (prop.getValue().force() || BeanUtils.getProperty(entity, prop.getKey().getName()) == null) {
						BeanUtils.setProperty(entity, prop.getKey().getName(), currentUserId);
					}
				}
			}
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
			throw new RuntimeException(ex);
		}

	}

	/**
	 * 
	 * @param annotation
	 * @param selectedGroups
	 * @return
	 */
	private boolean checkUpdateGroup(UpdatableProperty annotation, Class<?>... selectedGroups) {
		Class<?>[] propertyGroups = null;

		if (annotation == null || annotation.groups() == null || annotation.groups().length == 0) {
			propertyGroups = DEFAULT_VALIDATION_GROUP;
		} else {
			propertyGroups = annotation.groups();
		}
		if (selectedGroups == null || selectedGroups.length == 0) {
			selectedGroups = DEFAULT_VALIDATION_GROUP;
		}
		for (Class<?> selectedGroup : selectedGroups) {
			for (Class<?> annotationGroup : propertyGroups) {
				if (selectedGroup.equals(annotationGroup)) {
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dwf.persistence.dao.DAO#updateUpload(java.io.Serializable,
	 * java.io.InputStream, java.lang.String)
	 */
	@Override
	@Transactional(rollbackFor = ValidationException.class)
	public D updateUpload(Serializable id, InputStream inputStream, final String contentType, String originalFilename, String propertyName) throws IOException {
		D connectedEntity = findById(id);
		// get the UpdateGroup for the property
		PropertyDescriptor pd = entityProperties.get(propertyName);
		if (pd != null) {
			try {
				String oldValue = (String) BeanUtils.getProperty(connectedEntity, propertyName);

				String uploadKey = uploadManager.saveFile(inputStream, contentType, originalFilename, entityName + "/" + id);
				if (oldValue != null && !oldValue.equals(uploadKey)) {
					uploadManager.deleteFile(oldValue);
				}
				BeanUtils.setProperty(connectedEntity, propertyName, uploadKey);
				
				Image imageAnnotation = pd.getReadMethod().getAnnotation(Image.class);
				if (imageAnnotation != null) {

					for (String thumbnailProperty : imageAnnotation.thumbnail()) {
						//Apaga o thumbnail original
						String oldThumbValue = (String) BeanUtils.getProperty(connectedEntity, thumbnailProperty);
						if(!StringUtils.isBlank(oldThumbValue)) {
							uploadManager.deleteFile(oldThumbValue);
						}
						
						//define thumbnails temporariamente para a imagem original
						BeanUtils.setProperty(connectedEntity, thumbnailProperty, uploadKey);
					}

					sessionFactory.getCurrentSession().update(connectedEntity);
					sessionFactory.getCurrentSession().flush();
					imageResizer.resizeImage(id, entityName, propertyName);
				}
			} catch (IOException e) {
				throw e;
			} catch (Exception e) {
				// Error copying the property
				throw new RuntimeException(e);
			}
		}
		return connectedEntity;
	}

	/**
	 * The default implementation delegates the query creation to a
	 * queryBuilder, created by createQueryBuilder().
	 * 
	 * @param filter
	 * @param count
	 * @param params
	 * @return
	 */
	protected String createQuery(ParsedMap filter, QueryReturnType<?> returnType, Map<String, Object> params) {
		return createQueryBuilder().createQuery(filter, returnType, params);
	}

	@Override
	public void evict(D entity) {
		getSession().evict(entity);
	}

	/**
	 * @return the entityFullName
	 */
	public String getEntityFullName() {
		return entityFullName;
	}

	/**
	 * @return the propertyList.
	 */
	public List<NotSyncPropertyDescriptor> getPropertyList() {
		return propertyList;
	}

	public boolean hasPropertyWithName(String name) {
		return propertyNames.contains(name);
	}

	public Set<String> getPropertyNames() {
		return this.propertyNames;
	}

	@Override
	public Class<D> getEntityClass() {
		return clazz;
	}

	/**
	 * Searches based on naturalIds defined in the provided instance.
	 */
	@Override
	public D findByNaturalId(D instance) {
		ClassMetadata classMetadata = sessionFactory.getClassMetadata(getEntityClass());
		int[] natIds = classMetadata.getNaturalIdentifierProperties();
		if (natIds == null || natIds.length == 0)
			return null;
		Object[] propertyValues = classMetadata.getPropertyValues(instance);
		String[] propertyNames = classMetadata.getPropertyNames();
		NaturalIdLoadAccess natIdLoadAcc = null;

		for (int naturalIdIdx : natIds) {
			if (propertyValues[naturalIdIdx] != null) {
				if (natIdLoadAcc == null)
					natIdLoadAcc = getSession().byNaturalId(getEntityClass());
				natIdLoadAcc = natIdLoadAcc.using(propertyNames[naturalIdIdx], propertyValues[naturalIdIdx]);
			}
		}
		return natIdLoadAcc == null ? null : (D) natIdLoadAcc.load();
	}

	@Override
	public D findOrSaveNew(D instance) {
		if (instance == null)
			return null;
		D existent = instance.getId() != null ? findById(instance.getId()) : findByNaturalId(instance);
		return existent != null ? existent : saveNew(instance);
	}


	@Override
	public void setProperty(Serializable id, String propertyName, String stringValue) {
		D connectedEntity = findById(id);
		try {
			D entity = findById(id);
			String oldValue = (String) BeanUtils.getProperty(entity, propertyName);
			BeanUtils.setProperty(connectedEntity, propertyName, stringValue);
			sessionFactory.getCurrentSession().update(connectedEntity);
			sessionFactory.getCurrentSession().flush();
			
			PropertyDescriptor property = entityProperties.get(propertyName);
			if (property.getReadMethod().getAnnotation(IgnoreActivityLog.class) == null) {
				boolean hiddenValues = property.getReadMethod().getAnnotation(HideActivityLogValues.class) == null;
				activityLogService.logEntityPropertyUpdate(entity, new UpdatedProperty(propertyName, oldValue, stringValue, hiddenValues));
			}

		} catch (Exception e) {
			// Error copying the property
			throw new RuntimeException(e);
		}
	}

	
}
