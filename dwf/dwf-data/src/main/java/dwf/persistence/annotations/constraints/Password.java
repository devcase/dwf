package dwf.persistence.annotations.constraints;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;

/**
 * Validates to ensure that the field complies with the following rules:
 * 
 *  
 *
 */
@Documented
@Constraint(validatedBy = {PasswordConstraintValidator.class})
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@ReportAsSingleViolation
public @interface Password {
	String message() default "{dwf.persistence.annotations.constraints.Password.message}";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
	int minLowerCase() default 0;
	int minUpperCase() default 0;
	int minDigits() default 0;
}
