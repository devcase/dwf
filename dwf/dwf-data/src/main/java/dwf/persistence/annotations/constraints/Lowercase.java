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
import javax.validation.constraints.Pattern;

/**
 * Validates to ensure that the field complies with the following rules:
 * 
 *  
 *
 */
@Documented
@Constraint(validatedBy = {LowercaseConstraintValidator.class})
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@ReportAsSingleViolation
@Pattern(regexp = Lowercase.LOWERCASE_REGEXP)
public @interface Lowercase {
	public static String LOWERCASE_REGEXP ="^[^A-Z]*"; 
	String message() default "{dwf.persistence.annotations.constraints.Lowercase.message}";
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};

}
