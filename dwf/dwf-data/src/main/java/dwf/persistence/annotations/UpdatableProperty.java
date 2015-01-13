package dwf.persistence.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author Hirata
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UpdatableProperty {
	/**
	 * Can be used to choose different cases - for instance, the password field of a user could be part of
	 * two groups - one for a password change form (only the password) and another for a user creation form.  
	 * @return
	 */
	Class<?>[] groups() default {};
	boolean ignoreIfNull() default true;
}
