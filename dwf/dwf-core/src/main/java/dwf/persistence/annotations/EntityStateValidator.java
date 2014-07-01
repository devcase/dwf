package dwf.persistence.annotations;

import javax.validation.ValidationException;

import dwf.persistence.domain.BaseEntity;

/**
 * If you pass an implementation of this class to 
 * {@code BaseDAOImpl#updateByAnnotation(BaseEntity, Class...)} as a validation group, the operation may be cancelled
 * if the method {@link #validateState(BaseEntity)} throws an {@link Exception}
 * @author Hirata
 *
 */
public interface EntityStateValidator<D extends BaseEntity<?>> {
	/**
	 * 
	 * @param connectedEntity
	 */
	void validateState(D connectedEntity) throws ValidationException;
}
