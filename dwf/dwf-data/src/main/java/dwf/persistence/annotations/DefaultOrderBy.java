package dwf.persistence.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used by dwf.persistence.dao.DefaultQueryBuilder
 * @author Hirata
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DefaultOrderBy {
	String value() default "autocomplete";
}
