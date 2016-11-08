package dwf.persistence.dao;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

import javax.validation.ValidationException;

import dwf.persistence.annotations.UpdatableProperty;
import dwf.persistence.domain.BaseEntity;
import dwf.utils.ParsedMap;

/**
 * Interface base para implementação de DAOs
 * @author Hirata
 *
 * @param <D>
 */
public interface DAO<D extends BaseEntity<?>> {
	D findById(Serializable id);
	/**
	 * 
	 * @param filter
	 * @param offset inicia em 0
	 * @param maxResults tamanho da página
	 * @return
	 */
	List<D> findByFilter(ParsedMap filter, int offset, int fetchSize);
	<T> List<T> findByFilter(ParsedMap filter, QueryReturnType<T> returnType, int offset, int fetchSize);
	
	List<D> findByFilter(ParsedMap filter);
	List<D> findAll();
//	List<?> findByFilter(Object... params);
	<T> List<T> findByFilter(Object... params);
	D findFirstByFilter(Object... params);
	D findFirstByFilter(ParsedMap filter);
	D find(D copyWithId);
	<T> T findFirstByFilter(ParsedMap filter, QueryReturnType<T> returnType);
	<T> T findFirstByFilter(QueryReturnType<T> returnType, Object... params);
	int countByFilter(ParsedMap filter);
	int countByFilter(Object... params);

	D saveNew(D entity) throws ValidationException;
	/**
	 * Merge only the annotated fields
	 * @param entity
	 * @param groups Empty will update fields with @{@link UpdatableProperty} without any group
	 * @return
	 * @throws ValidationException
	 */
	D updateByAnnotation(D entity, Class<?>... groups) throws ValidationException;
	D updateByAnnotation(D entity, boolean ignoreNullValues, Class<?>... groups) throws ValidationException;
	D importFromFile(D entity) throws ValidationException;
	void delete(D entity, String comments);
	D restore(D entity, String comments);
	void validate(D entity, Class<?>... groups) throws ValidationException;
	
	D updateUpload(Serializable id, InputStream inputStream, String contentType, String originalFilename, String propertyName) throws IOException;
	D retrieveCopy(Serializable id);
	void evict(D entity);
	Class<D> getEntityClass();
	
	D findByNaturalId(D instance);
	D findOrSaveNew(D instance);
	
	<T> void setProperty(Serializable id, String propertyName, T value);
}
