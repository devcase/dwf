package dwf.persistence.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotates a getter to be filled with the current user by the BaseDAOImpl.
 * 
 * @author Hirata
 * 
 */
@Target({ ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FillWithCurrentUser {
	boolean force() default false ;

}
