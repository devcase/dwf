package dwf.persistence.dao;

import java.io.Serializable;
import java.util.List;

import javax.validation.ValidationException;

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
	 * @param pageNumber inicia em 0
	 * @param maxResults tamanho da página
	 * @return
	 */
	List<?> findByFilter(ParsedMap filter, int pageNumber, int fetchSize);
	List<?> findByFilter(ParsedMap filter);
	List<?> findAll();
	
	int countByFilter(ParsedMap filter);
	D saveNew(D entity) throws ValidationException;
	/**
	 * Merge the passed entity (hibernate does the job)
	 * @param entity
	 * @return
	 * @throws ValidationException
	 */
	D merge(D entity) throws ValidationException;
	/**
	 * Merge only the annotated fields
	 * @param entity
	 * @param groups Empty will update fields with @{@link UpdatableProperty} without any group
	 * @return
	 * @throws ValidationException
	 */
	D updateByAnnotation(D entity, Class<?>... groups) throws ValidationException;
	void delete(D entity, String comments);
	D restore(D entity, String comments);
	void validate(D entity, Class<?>... groups) throws ValidationException;
}
