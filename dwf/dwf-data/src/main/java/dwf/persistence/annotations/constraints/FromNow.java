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

@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Constraint(validatedBy = { FromNowValidator.class})
@Documented
public @interface FromNow {
	public static final int  NOT_SET = Integer.MIN_VALUE;
	String message() default "{dwf.persistence.annotations.constraints.FromNow.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
	
	int minYearsPast() default NOT_SET;
	int minDaysFuture() default NOT_SET;
}
