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
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.jongo.Aggregate;
import org.jongo.Find;
import org.jongo.Jongo;
import org.jongo.MongoCollection;
import org.jongo.MongoCursor;
import org.jongo.Oid;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.BasicDBObject;

import dwf.activitylog.domain.UpdatedProperty;
import dwf.activitylog.service.ActivityLogService;
import dwf.persistence.annotations.ConditionalGroup;
import dwf.persistence.annotations.EntityStateValidator;
import dwf.persistence.annotations.FillWithCurrentUser;
import dwf.persistence.annotations.HideActivityLogValues;
import dwf.persistence.annotations.IgnoreActivityLog;
import dwf.persistence.annotations.Image;
import dwf.persistence.annotations.MongoEntity;
import dwf.persistence.annotations.NotEditableProperty;
import dwf.persistence.annotations.UpdatableProperty;
import dwf.persistence.domain.BaseEntity;
import dwf.persistence.utils.NotSyncPropertyDescriptor;
import dwf.persistence.validation.ValidationGroups;
import dwf.upload.UploadManager;
import dwf.upload.image.ImageResizer;
import dwf.user.DwfUserUtils;
import dwf.utils.ParsedMap;
import dwf.utils.SearchstringUtils;
import dwf.utils.SimpleParsedMap;

public class BaseMongoDAOImpl<D extends BaseEntity<String>> implements MongoDAO<D> {
	private final static Class<?>[] DEFAULT_VALIDATION_GROUP = { Default.class };

	@Autowired
	protected ActivityLogService activityLogService;
	@Autowired
	protected Validator beanValidator;
	@Autowired
	private Jongo jongo;
	@Autowired(required = false)
	private UploadManager uploadManager;
	@Autowired(required = false)
	private ImageResizer imageResizer;



	protected final Class<D> clazz;
	protected final String entityFullName;
	protected final String entityName;

	
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
	
	protected MongoCollection mongoCollection;
	
	
	public BaseMongoDAOImpl(Class<D> clazz) {
		super();
		this.clazz = clazz;
		this.entityFullName = clazz.getName();
		this.entityName = StringUtils.uncapitalize(clazz.getSimpleName());

		fieldsToTrim = new ArrayList<Field>();
		updatableProperties = new HashMap<NotSyncPropertyDescriptor, UpdatableProperty>();
		this.propertyList = new ArrayList<NotSyncPropertyDescriptor>();
		this.propertyNames = new HashSet<String>();
		this.readAndWritePropertyNames = new HashSet<String>();
		this.filledWithUser = new HashMap<NotSyncPropertyDescriptor, FillWithCurrentUser>();
		this.entityProperties = new HashMap<String, PropertyDescriptor>();
		
		try {
			processClazzPropertiesRecursive(clazz);
		} catch (IllegalAccessException | InvocationTargetException | IntrospectionException e) {
			throw new RuntimeException("Couldn't create the DAO. Check the Entity configuration.", e);
		}

	}
	
	@Override
	public D findFirstByMap(Map<String, Object> mongoMap) {
		return getCollection().findOne(new BasicDBObject(mongoMap).toString()).as(clazz);
	}



	@Override
	public List<D> findByMap(Map<String, Object> mongoMap) {
		MongoCursor<D> cursor = getCollection().find(new BasicDBObject(mongoMap).toString()).as(clazz);
		List<D> list = IteratorUtils.toList(cursor);
		try {
			cursor.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}


	@Override
	public <T> List<T> aggregateByListOfMap(List<Map<String, Object>> mongoAggregatePipeline, Class<T> resultClass) {
		// see MongoDB documentation on Aggregation Pipelines
		// http://docs.mongodb.org/manual/core/aggregation-introduction/
		
		// REMEMBER: $match has to have '"enabled": true' if you don't want deleted/disabled entries
		
		if (mongoAggregatePipeline != null && mongoAggregatePipeline.size() > 0) {
			Aggregate aggr = getCollection().aggregate(new BasicDBObject(mongoAggregatePipeline.get(0)).toString());
			for (int i = 1; i < mongoAggregatePipeline.size(); i++) {
				aggr.and(new BasicDBObject(mongoAggregatePipeline.get(i)).toString());
			}
			Aggregate.ResultsIterator<T> resultsIterator = aggr.as(resultClass);
			List<T> resultsList = new ArrayList<T>();
			resultsIterator.forEach(p -> resultsList.add(p));
			return resultsList;
		}
		return null;
	}

	private MongoCollection getCollection() {
		if (mongoCollection == null) {
			MongoEntity annotation = clazz.getAnnotation(MongoEntity.class);
			if (annotation == null || annotation.collectionName().equals(""))
				throw new Error("Entidades Mongo devem ter anotação MongoEntity com valor para collectionName!");
			mongoCollection = jongo.getCollection(annotation.collectionName());
			mongoCollection.ensureIndex(new BasicDBObject("enabled", 1).toString());
		}
		return mongoCollection;
	}
	
	@Override
	public D findById(Serializable id) {
		if (id instanceof String) {
			return getCollection().findOne(mongoQueryBuilder(new SimpleParsedMap("id", (String) id), true).toString()).as(clazz);
		}
		else
			throw new InvalidParameterException("id deve ser String");
	}

	@Override
	public List<D> findByFilter(ParsedMap filter) {
				
		MongoCursor<D> cursor = getCollection().find(mongoQueryBuilder(filter)).as(clazz);
		List<D> list = IteratorUtils.toList(cursor);
		try {
			cursor.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
		
	}

	@Override
	public List<D> findAll() {
		MongoCursor<D> cursor = getCollection().find(mongoQueryBuilder(null)).as(clazz);
		List<D> list = IteratorUtils.toList(cursor);
		try {
			cursor.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public D findFirstByFilter(ParsedMap filter) {
		return getCollection().findOne(mongoQueryBuilder(filter)).as(clazz);
	}

	@Override
	public int countByFilter(ParsedMap filter) {
		return (int) getCollection().count(mongoQueryBuilder(filter));
	}
	
	private final String mongoQueryBuilder(ParsedMap filter) {
		return mongoQueryBuilder(filter, false);
	}

	/**
	 * Transforma os parâmetros de filter em uma query mongo
	 * @param filter
	 * @param allowDisabled
	 * @return
	 */
	protected String mongoQueryBuilder(ParsedMap filter, boolean allowDisabled) {
                if(filter == null) {
                    filter = new SimpleParsedMap();
                }
		BasicDBObject obj = new BasicDBObject();
		
		if (filter.containsKey("searchstring")){
			// Se a busca é por searchstring, cria query com wildcards requisitados em searchwildcards
			String mongoRegexQuery;
			boolean wildCardStart = true;
			boolean wildCardEnd = true;
			if (filter.containsKey("searchwildcards") && StringUtils.isNotBlank(filter.getString("searchwildcards")) ){
				if (filter.getString("searchwildcards").toUpperCase().equals("NONE") ){
					wildCardStart = false;
					wildCardEnd = false;
				} else if (filter.getString("searchwildcards").toUpperCase().equals("BEFORE")){
					wildCardStart = true;
					wildCardEnd = false;
				} else if (filter.getString("searchwildcards").toUpperCase().equals("AFTER")){
					wildCardStart = false;
					wildCardEnd = true;
				} else if(filter.getString("searchwildcards").toUpperCase().equals("BOTH")){
					wildCardStart = true;
					wildCardEnd = true;
				}
							
			}
			
			String searchString = SearchstringUtils.prepareForSearch(filter.getString("searchstring"));
			mongoRegexQuery = "{searchstring: {$regex:" + "'(?i)" + (wildCardStart ? "" : "^") + searchString + (wildCardEnd ? "" : "$") + "'}" + (allowDisabled ? "" : ", enabled:true") + "}";
			
			return mongoRegexQuery;
			
		} else {
			
			if (!allowDisabled) {
				obj.append("enabled", true);
			}
			if (filter == null) return obj.toString();
			
			for (PropertyDescriptor pDescriptor : propertyList) {
				String pName = pDescriptor.getName();
				if(filter.containsKey(pName)) {
					if (pName.equals("id")) obj.append("_id", new BasicDBObject("$oid", filter.get(pName))); // se for id busca pelo ObjectId de key _id (padrão do Mongo)
					else obj.append(pName, filter.get(pName));
				} else if(filter.containsKey(pName+ ".id")) {
					//Não funciona para collection, só objeto único embedded
					obj.append(pName+".id", filter.get(pName+".id"));
				} else if (filter.containsKey(pName+".center") && filter.containsKey(pName+".radius")) {
					//busca por geolocalização
					
					// para buscar pro geolocalização, o filtro tem que ter as duas propriedades:
					// {propriedade}.center -> array de float ou double no formato [longitude, latitude]
					// {propriedade}.radius -> int, raio da busca a ser feita
					
					BasicDBObject geometry = new BasicDBObject("type", "Point").append("coordinates", filter.get(pName+".center"));
					obj.append(pName, new BasicDBObject("$near", new BasicDBObject("$geometry", geometry).append("$maxDistance", filter.get(pName+".radius"))));
				} else if (filter.containsKey(pName+".after") || filter.containsKey(pName+".gte")) {
					// filtrar após uma data ou maior que um número
					
					// {propriedade}.after -> Date
					// {propriedade}.gte -> numero
					
					Object key = filter.get(pName+".after");
					if (key == null) key = filter.get(pName+".gte");
					obj.append(pName, new BasicDBObject("$gte", key));
				} 
			}
			
			return obj.toString();
		}
		
		
	}

	@Override
	public D saveNew(D entity) {
		prepareEntity(entity);
		validate(entity, ValidationGroups.MergePersist.class);
		validate(entity); // valida campos sem grupos definidos
		entity.setUpdateTime(new Date());
		entity.setCreationTime(new Date());
		
		// gerar um novo id
		entity.setId(ObjectId.get().toString());
		
		getCollection().insert(entity);
		
		activityLogService.log(entity, ActivityLogService.OPERATION_CREATE);

		return entity;
	}

	@Override
	public List<D> findByFilter(ParsedMap filter,
			int offset, int fetchSize) {
		
		Find find;
					
		find = getCollection().find(mongoQueryBuilder(filter)).skip(offset>0?offset:0);
		if (fetchSize>0)
			find = find.limit(fetchSize);
		MongoCursor<D> cursor = find.as(clazz); 
		List<D> list = IteratorUtils.toList(cursor);
		try {
			cursor.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return list;
		
	}

	@Override
	public <T> List<T> findByFilter(ParsedMap filter,
			QueryReturnType<T> returnType, int pageNumber, int fetchSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<?> findByFilter(Object... params) {
		return findByFilter(new SimpleParsedMap(params));
	}

	@Override
	public D findFirstByFilter(Object... params) {
		return findFirstByFilter(new SimpleParsedMap(params));
	}

	@Override
	public D find(D copyWithId) {
		return getCollection().findOne(Oid.withOid(copyWithId.getId())).as(clazz);
	}

	@Override
	public <T> T findFirstByFilter(ParsedMap filter,
			QueryReturnType<T> returnType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T findFirstByFilter(QueryReturnType<T> returnType,
			Object... params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int countByFilter(Object... params) {
		return countByFilter(new SimpleParsedMap(params));
	}

	@Override
	public D updateByAnnotation(D entity,
			Class<?>... groups) throws ValidationException {
		prepareEntity(entity);
		validate(entity, groups);

		D retrievedEntity = find(entity);
		if (retrievedEntity == null) {
			throw new IllegalArgumentException("Id must be not-null");
		}

		if (groups != null) {
			for (Class<?> validGrpClazz : groups) {
				if (validGrpClazz.isAnnotationPresent(ConditionalGroup.class)) {
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

//		// initializes properties before eviction
//		for (final PropertyDescriptor property : this.updatableProperties.keySet()) {
//			UpdatableProperty annotation = this.updatableProperties.get(property);
//			if (checkUpdateGroup(annotation, groups)) {
//				try {
//					Object value = PropertyUtils.getSimpleProperty(retrievedEntity, property.getName());
//					Hibernate.initialize(value);
//				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
//					// Error copying the property
//					throw new RuntimeException(e);
//				}
//			}
//		}

		List<UpdatedProperty> updatedProperties = new ArrayList<UpdatedProperty>();

		
//		ClassMetadata cm = sessionFactory.getClassMetadata(clazz);
		

		for (final PropertyDescriptor property : this.updatableProperties.keySet()) {
			try {
				UpdatableProperty annotation = this.updatableProperties.get(property);
				if (checkUpdateGroup(annotation, groups)) {
//					Type t = cm.getPropertyType(property.getName()); 
					
					Object value = PropertyUtils.getSimpleProperty(entity, property.getName());
					Object oldValue = PropertyUtils.getSimpleProperty(retrievedEntity, property.getName());

					boolean isCollection = Collection.class.isAssignableFrom(property.getPropertyType());
					if(isCollection) {
						boolean isList = List.class.isAssignableFrom(property.getPropertyType());
						boolean isMap = Map.class.isAssignableFrom(property.getPropertyType());
						boolean isArray = Array.class.isAssignableFrom(property.getPropertyType());;
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
					if (property.getReadMethod().getAnnotation(HideActivityLogValues.class) != null) {
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
				// Error copying the property
				throw new RuntimeException(e);
			}
		}
		retrievedEntity.setUpdateTime(new Date());

		activityLogService.logEntityUpdate(entity, updatedProperties, groups);

//		getSession().update(retrievedEntity);
		getCollection().save(retrievedEntity);
		return retrievedEntity;
	}

	@Override
	public D importFromFile(D entity)
			throws ValidationException {
		prepareEntity(entity);
		validate(entity, ValidationGroups.ImportFromFile.class);
//		if (entity.getId() != null) {
//			setIdForImport(entity);
//		}

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

	@Override
	public void delete(D entity, String comment) {
		D retrievedEntity = findById(entity.getId());
		if (retrievedEntity.isEnabled()) {
			activityLogService.log(entity, "delete", comment);
			retrievedEntity.setEnabled(false);
			getCollection().save(retrievedEntity);
		}
	}

	@Override
	public D restore(D entity, String comment) {
		D retrievedEntity = findById(entity.getId());
		if (!retrievedEntity.isEnabled()) {
			activityLogService.log(entity, "restore", comment);
			retrievedEntity.setEnabled(true);
			getCollection().save(retrievedEntity);
		}
		return retrievedEntity;
	}

	@Override
	public void validate(D entity, Class<?>... groups)
			throws ValidationException {
		Set<ConstraintViolation<D>> violations = beanValidator.validate(entity, groups);
		if (!violations.isEmpty()) {
			throw new ConstraintViolationException(violations);
		}

		
	}

	@Override
	public D updateUpload(Serializable id,
			InputStream inputStream, String contentType,
			String originalFilename, String propertyName) throws IOException {
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
				getCollection().update(new ObjectId(id.toString())).with("{$set: {" + propertyName + ": #}}", uploadKey);
				
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
						
						getCollection().update(new ObjectId(id.toString())).with("{$set: {" + thumbnailProperty + ": #}}", uploadKey);
					}

//					sessionFactory.getCurrentSession().update(connectedEntity);
//					sessionFactory.getCurrentSession().flush();
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

	@Override
	public D retrieveCopy(Serializable id) {
		return findById(id);
	}

	@Override
	public void evict(D entity) {
		throw new Error("função evict(entity) não deve ser chamada em um DAO mongo");
	}

	@Override
	public Class<D> getEntityClass() {
		return clazz;
	}

	@Override
	public D findByNaturalId(D instance) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public D findOrSaveNew(D instance) {
		// TODO Auto-generated method stub
		return null;
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

			// ignorar propriedades transientes
			if (readMethod.isAnnotationPresent(Transient.class) 
					&& !readMethod.isAnnotationPresent(UpdatableProperty.class)) { //usado para dar override (ex: override do UpdatableProperty do getName de BaseMultilangEntity)
				continue;
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
	public D updateByAnnotation(D entity, boolean ignoreNullValues,
			Class<?>... groups) throws ValidationException {
		return updateByAnnotation(entity, false, groups);
	}

	@Override
	public void setProperty(Serializable id, String propertyName, String stringValue) {
		try {
			D entity = findById(id);
			String oldValue = (String) BeanUtils.getProperty(entity, propertyName);
			getCollection().update(new ObjectId(id.toString())).with("{$set: {" + propertyName + ": #}}", stringValue);
			
			PropertyDescriptor property = entityProperties.get(propertyName);
			
			if (property.getReadMethod().getAnnotation(IgnoreActivityLog.class) == null) {
				boolean hiddenValues = property.getReadMethod().getAnnotation(HideActivityLogValues.class) != null;
				activityLogService.logEntityPropertyUpdate(entity, new UpdatedProperty(propertyName, oldValue, stringValue, hiddenValues));
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	

	
}
