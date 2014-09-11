package dwf.persistence.dao;

import java.awt.image.BufferedImage;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
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

import javax.imageio.ImageIO;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import javax.validation.groups.Default;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.transaction.annotation.Transactional;

import dwf.activitylog.domain.UpdatedProperty;
import dwf.activitylog.service.ActivityLogService;
import dwf.persistence.annotations.ConditionalGroup;
import dwf.persistence.annotations.EntityStateValidator;
import dwf.persistence.annotations.FillWithCurrentUser;
import dwf.persistence.annotations.HideActivityLogValues;
import dwf.persistence.annotations.Image;
import dwf.persistence.annotations.NotEditableProperty;
import dwf.persistence.annotations.UpdatableProperty;
import dwf.persistence.domain.BaseEntity;
import dwf.persistence.utils.NotSyncPropertyDescriptor;
import dwf.security.DwfUserUtils;
import dwf.upload.UploadManager;
import dwf.user.domain.User;
import dwf.utils.ParsedMap;
import dwf.utils.SimpleParsedMap;
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
	
	private final static Class<?>[] DEFAULT_VALIDATION_GROUP = {Default.class};

	@Autowired
	protected SessionFactory sessionFactory;
	@Autowired
	protected ActivityLogService activityLogService;
	@Autowired
	protected Validator beanValidator;
	@Autowired(required=false)
	private UploadManager uploadManager;
	
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

	public BaseDAOImpl(Class<D> clazz)  {
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
		this.filledWithUser = new HashMap<NotSyncPropertyDescriptor, FillWithCurrentUser>();
		this.entityProperties = new HashMap<String, PropertyDescriptor>();

		try {
			processClazzFieldsRecursive(this.clazz);
		} catch (IllegalAccessException | InvocationTargetException | IntrospectionException e) {
			throw new RuntimeException("Couldn't create the DAO. Check the Entity configuration.", e);
		}
		
	}
	
	/**
	 * Search for String fields to build the fieldsToTrim list.
	 * <p>Search for annotated fields with @{@link UpdatableProperty} annotation.
	 * @param cl
	 * @throws IntrospectionException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	private void processClazzFieldsRecursive(Class<?> cl) throws IllegalAccessException, InvocationTargetException, IntrospectionException {
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

		for (PropertyDescriptor p1 : PropertyUtils.getPropertyDescriptors(cl)) {
			NotSyncPropertyDescriptor p = new NotSyncPropertyDescriptor(p1);
			propertyList.add(p);
			propertyNames.add(p.getName());
			entityProperties.put(p.getName(), p);
			
			Method writeMethod = p.getWriteMethod();
			Method readMethod = p.getReadMethod();
			if(writeMethod != null && readMethod.getAnnotation(FillWithCurrentUser.class) != null) {
				filledWithUser.put(p, readMethod.getAnnotation(FillWithCurrentUser.class));
			}
			
			if(readMethod.getAnnotation(UpdatableProperty.class) != null) {
				updatableProperties.put(p, readMethod.getAnnotation(UpdatableProperty.class));
			} else if(readMethod.getAnnotation(NotEditableProperty.class) != null || writeMethod == null) {
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
	 * @see dwf.persistence.dao.DAO#findByFilter(java.lang.Object[])
	 */
	@Override
	public List<?> findByFilter(Object... params) {
		return findByFilter(new SimpleParsedMap(params));
	}
	
	@Override
	public int countByFilter(Object... params) {
		return countByFilter(new SimpleParsedMap(params));
	}

	
	/* (non-Javadoc)
	 * @see dwf.persistence.dao.DAO#findFirstByFilter(java.lang.Object[])
	 */
	@SuppressWarnings("unchecked")
	@Override
	public D findFirstByFilter(Object... params) {
		List<?> list = findByFilter(params);
		if(list.isEmpty()) return null;
		else return (D) list.get(0);
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

			/* (non-Javadoc)
			 * @see dwf.utils.ParsedMap#get(java.lang.String)
			 */
			@Override
			public Object get(String key) {
				return null;
			}

			/* (non-Javadoc)
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
		String q = createQuery(filter, true, params);
		return count(q, params);
	}

	public final List<?> findByPage(String hql, int offset, int fetchSize,
			Map<String, Object> params) {
		Query query = getSession().createQuery(hql);
		for (Entry<String, Object> entry : params.entrySet()) {
			Object value = entry.getValue();
			if(value != null && value.getClass().isArray()) {
				query.setParameterList(entry.getKey(), (Object[]) value);
			} else if(value != null && (value instanceof Collection<?>)) {
				query.setParameterList(entry.getKey(), (Collection<?>) value);
			} else {
				query.setParameter(entry.getKey(), value);
			}
		}
		query.setFirstResult(offset);
		if(fetchSize >= 0)
			query.setMaxResults(fetchSize);
		return query.list();
	}

	public final int count(String hql, Map<String, Object> params) {
		Query query = getSession().createQuery(hql);
		for (Entry<String, Object> entry : params.entrySet()) {
			Object value = entry.getValue();
			if(value != null && value.getClass().isArray()) {
				query.setParameterList(entry.getKey(), (Object[]) value);
			} else if(value != null && (value instanceof Collection<?>)) {
				query.setParameterList(entry.getKey(), (Collection<?>) value);
			} else {
				query.setParameter(entry.getKey(), value);
			}
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
		prepareEntity(entity);
		validate(entity,ValidationGroups.MergePersist.class);
		validate(entity); //valida campos sem grupos definidos
		entity.setUpdateTime(new Date());
		entity.setCreationTime(new Date());
		getSession().persist(entity);
		activityLogService.log(entity, ActivityLogService.OPERATION_CREATE);
		return entity;
	}

	@Override
	@Transactional(rollbackFor=ValidationException.class)
	public D importFromFile(D entity) throws ValidationException {
		prepareEntity(entity);
		validate(entity,ValidationGroups.ImportFromFile.class);
		if(entity.getId() != null) {
			setIdForImport(entity);
		}
		
		D existent = entity.getId() != null ? findById(entity.getId()) : null;
		
		if(existent == null && entity.getId() != null) {
			entity.setId(null);
		}
		
		if(existent == null) {
			//novo
			entity = saveNew(entity);
		} else {
			//entity 
			updateByAnnotation(entity, ValidationGroups.ImportFromFile.class);
		}
		return entity;
	}
	
	/**
	 * 
	 * @param entity
	 * @return
	 */
	protected void setIdForImport(D entity) {
	}

	
//	@SuppressWarnings("unchecked")
//	@Transactional(rollbackFor=ValidationException.class)
//	public D merge(D entity) throws ValidationException {
//		prepareEntity(entity);
//		validate(entity,ValidationGroups.MergePersist.class);
//		validate(entity); //valida campos sem grupos definidos
//		entity.setUpdateTime(new Date());
//		entity = (D) getSession().merge(entity);
//		activityLogService.log(entity, "merge");
//		return entity;
//	}

	@SuppressWarnings("unchecked")
	@Override
	@Transactional(rollbackFor=ValidationException.class)
	public D updateByAnnotation(D entity, Class<?>... groups) throws ValidationException {
		prepareEntity(entity);
		validate(entity, groups);

		D retrievedEntity = findById(entity.getId());
		if(retrievedEntity == null) {
			throw new IllegalArgumentException("Id must be not-null");
		}
		
		if(groups != null) {
			for (Class<?> validGrpClazz : groups) {
				if(validGrpClazz.isAnnotationPresent(ConditionalGroup.class)) {
					try {
						ConditionalGroup cg = validGrpClazz.getAnnotation(ConditionalGroup.class);
						if(cg.validatedBy() != null) {
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

		//initializes properties before eviction
		for (final PropertyDescriptor property : this.updatableProperties.keySet()) {
			UpdatableProperty annotation = this.updatableProperties.get(property);
			if(checkUpdateGroup(annotation, groups)) {
				try {
					Object value = PropertyUtils.getSimpleProperty(retrievedEntity, property.getName());
					Hibernate.initialize(value);
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					//Error copying the property
					throw new RuntimeException(e);
				}
			}
		}
		getSession().evict(retrievedEntity);
		
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

	/**
	 * You may choose between override this method or createQuery()
	 * @return
	 */
	protected QueryBuilder createQueryBuilder() {
		return new DefaultQueryBuilder(this);
	}

	/**
	 * Called before any validation or persistence
	 * @param entity
	 */
	protected void prepareEntity(D entity) {
		try {
		
			//Trim string fields
			for (final Field fieldToTrim : fieldsToTrim) {
				String propertyValue;
				propertyValue = (String) BeanUtils.getProperty(entity, fieldToTrim.getName());
				if(propertyValue == null) {
					continue;
				} else {
					propertyValue = propertyValue.trim();
					if(propertyValue.length() == 0)
						propertyValue = null;
				}
				BeanUtils.setProperty(entity, fieldToTrim.getName(), propertyValue);
			}
				
			//@FillWithCurrentUser
			User currentUser = DwfUserUtils.getCurrentUser();
			if(currentUser != null) {
				for (Map.Entry<NotSyncPropertyDescriptor, FillWithCurrentUser> prop : this.filledWithUser.entrySet()) {
					if(prop.getValue().force() || BeanUtils.getProperty(entity, prop.getKey().getName()) == null) {
						BeanUtils.setProperty(entity, prop.getKey().getName(), currentUser);
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
		
		if(annotation == null || annotation.groups() == null || annotation.groups().length == 0) {
			propertyGroups = DEFAULT_VALIDATION_GROUP;
		} else {
			propertyGroups = annotation.groups();
		}
		if(selectedGroups == null || selectedGroups.length == 0) {
			selectedGroups = DEFAULT_VALIDATION_GROUP;
		}
		for (Class<?> selectedGroup : selectedGroups) {
			for (Class<?> annotationGroup : propertyGroups) {
				if(selectedGroup.equals(annotationGroup)) {
					return true;
				}
			}
		}
		return false;
	}
	
	
	
	/* (non-Javadoc)
	 * @see dwf.persistence.dao.DAO#updateUpload(java.io.Serializable, java.io.InputStream, java.lang.String)
	 */
	@Override
	public D updateUpload(Serializable id, InputStream inputStream, String contentType, String originalFilename, String propertyName) throws IOException {
		D entity = findById(id);
		//get the UpdateGroup for the property
		PropertyDescriptor pd = entityProperties.get(propertyName);
		if(pd != null) {
			Image imageAnnotation = pd.getReadMethod().getAnnotation(Image.class);
			try {
				String oldValue = (String) BeanUtils.getProperty(entity, propertyName);
				
				if(imageAnnotation == null) {
					String uploadKey = uploadManager.saveFile(inputStream, contentType, originalFilename, entityName + "/" + id);
					if(oldValue != null && !oldValue.equals(uploadKey)) {
						uploadManager.deleteFile(oldValue);
					}
					BeanUtils.setProperty(entity, propertyName, uploadKey);
				} else {
					//é imagem! fazer resize
					BufferedImage srcImg = ImageIO.read(inputStream);
					BufferedImage resizedImg = null;
					BufferedImage croppedImg = null;
					try {
						Mode resizeMode = (((double)srcImg.getHeight() / (double)srcImg.getWidth()) < ((double)imageAnnotation.targetHeight() / (double)imageAnnotation.targetWidth())) ? Mode.FIT_TO_HEIGHT : Mode.FIT_TO_WIDTH;

						resizedImg = Scalr.resize(srcImg, org.imgscalr.Scalr.Method.ULTRA_QUALITY, resizeMode, imageAnnotation.targetWidth(), imageAnnotation.targetHeight());
						
						int cropStartX = Math.max((resizedImg.getWidth() - imageAnnotation.targetWidth()) /2, 0);
						int cropStartY = Math.max((resizedImg.getHeight() - imageAnnotation.targetHeight()) /2, 0);
						
						croppedImg = Scalr.crop(resizedImg, cropStartX, cropStartY, imageAnnotation.targetWidth(), imageAnnotation.targetHeight());
						String uploadKey = uploadManager.saveImage(croppedImg, "img/jpeg", propertyName + ".jpg", entityName + "/" + id);
						if(oldValue != null && !oldValue.equals(uploadKey)) {
							uploadManager.deleteFile(oldValue);
						}
						BeanUtils.setProperty(entity, propertyName, uploadKey);
						
						for(String thumbProperty  : imageAnnotation.thumbnail()) {
							//thumbnail
							PropertyDescriptor thumbPd = entityProperties.get(thumbProperty);
							if(thumbPd != null) {
								String oldThumbValue = (String) BeanUtils.getProperty(entity, thumbProperty);
								
								
								Image thumbAnnotation = thumbPd.getReadMethod().getAnnotation(Image.class);
								int thumbWidth = thumbAnnotation != null ? thumbAnnotation.targetWidth() : 100;
								int thumbHeight = thumbAnnotation != null ? thumbAnnotation.targetHeight() : 100;
								
								resizeMode = ((double)croppedImg.getHeight() / (double)croppedImg.getWidth()) <  ((double)thumbHeight / (double)thumbWidth) ? Mode.FIT_TO_HEIGHT : Mode.FIT_TO_WIDTH;
								
								BufferedImage thumbImg = null;
								BufferedImage croppedThumbImg = null;
								try {
									thumbImg = Scalr.resize(croppedImg, org.imgscalr.Scalr.Method.ULTRA_QUALITY, resizeMode, thumbWidth, thumbHeight);
									cropStartX = (thumbImg.getWidth() - thumbWidth) /2;
									cropStartY = (thumbImg.getHeight() - thumbHeight) /2;

									croppedThumbImg = Scalr.crop(thumbImg, cropStartX, cropStartY, thumbWidth, thumbHeight);
									String thumbUpKey = uploadManager.saveImage(croppedThumbImg, "img/jpeg", thumbProperty + ".jpg", entityName + "/" + id);
									if(oldThumbValue != null && !oldThumbValue.equals(thumbUpKey)) {
										uploadManager.deleteFile(oldThumbValue);
									}
									
									BeanUtils.setProperty(entity, thumbProperty, thumbUpKey);
								} finally {
									if(thumbImg != null) resizedImg.flush();
									if(croppedThumbImg != null) croppedThumbImg.flush();
								}
							}
						}
					} finally {
						//limpa (?) dados da memória - TODO estudar mais
						if(srcImg != null) srcImg.flush();
						if(resizedImg != null) resizedImg.flush();
						if(croppedImg != null) croppedImg.flush();
					}
				}
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException  e) {
				//Error copying the property
				throw new RuntimeException(e);
			}
		}
		return entity;
	}

	/**
	 * The default implementation delegates the query creation to a queryBuilder, created by
	 * createQueryBuilder().
	 * @param filter
	 * @param count
	 * @param params
	 * @return
	 */
	protected String createQuery(ParsedMap filter, boolean count, Map<String, Object> params) {
		return createQueryBuilder().createQuery(filter, count, params);
	}

	/**
	 * @return the entityName
	 */
	public String getEntityName() {
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
	
	public Set<String> getPropertyNames(){
		return  this.propertyNames;
	}
}
