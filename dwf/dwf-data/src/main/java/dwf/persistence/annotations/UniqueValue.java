package dwf.persistence.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import dwf.persistence.domain.BaseEntity;

/**
 * The element won't accept a value if there is another persisted element with
 * the same value.
 * 
 * @author Hirata
 * 
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueValue.Validator.class)
public @interface UniqueValue {
	Class<?>[] groups() default {};

	String message() default "{dwf.persistence.annotations.UniqueValue.message}";

	Class<? extends Payload>[] payload() default {};

	String field();

	/**
	 * Hibernate won't autowire the sessionFactory
	 * @author Hirata
	 *
	 */
	public class Validator implements ConstraintValidator<UniqueValue, BaseEntity<?>> {
		private Log log = LogFactory.getLog(getClass());

		@Autowired //vai ser preenchido
		protected SessionFactory sessionFactory;

		private String fieldName;
		private String messageTemplate;

		@Override
		public void initialize(UniqueValue constraintAnnotation) {
			this.fieldName = constraintAnnotation.field();
			this.messageTemplate = constraintAnnotation.message();
		}

		@Override
		public boolean isValid(BaseEntity<?> entity, ConstraintValidatorContext constraintContext) {
			if(sessionFactory == null) return true; //ignorando - quando chamado pelo Hibernate
			
			try {
				Object propertyValue = BeanUtils.getProperty(entity, fieldName);
				if(propertyValue == null) {
					return true;
				}
				Session session = sessionFactory.getCurrentSession();
				//monta busca de elemento no banco com mesmo nome
				Criteria crit = session.createCriteria(entity.getClass()).add(Restrictions.eq(fieldName, propertyValue));
				if(entity.getId() != null) {
					//e diferente id
					crit.add(Restrictions.ne("id", entity.getId()));
				}
				Number count = (Number) crit.setProjection(Projections.rowCount()).uniqueResult();
				
				boolean isValid = count.intValue() == 0; 

				//Creates a violation
				if(!isValid) {
		            constraintContext.disableDefaultConstraintViolation();
		            constraintContext.buildConstraintViolationWithTemplate( messageTemplate  )
		            	.addPropertyNode(fieldName).addConstraintViolation();
		        }
		        return isValid;
				
			}
			catch (final Exception ex)
	        {
	            // ignore
				log.fatal("Could not check UniqueValue constraint. Ignoring.", ex);
				return true;
	        }
		}
	}

	/**
	 * Defines several <code>@FieldMatch</code> annotations on the same element
	 * 
	 * @see FieldMatch
	 */
	@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
	@Retention(RetentionPolicy.RUNTIME)
	@Documented
	@interface List {
		UniqueValue[] value();
	}
}
