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

	String message() default "{constraint.uniquevalue}";

	Class<? extends Payload>[] payload() default {};

	String field();

	/**
	 * Used by hibernate validator. Spring will use the UniqueValueValidator implementation
	 * @author Hirata
	 *
	 */
	public class Validator implements ConstraintValidator<UniqueValue, BaseEntity<?>> {
		@Override
		public void initialize(UniqueValue constraintAnnotation) {
		}

		@Override
		public boolean isValid(BaseEntity<?> value, ConstraintValidatorContext context) {
			return true;
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
