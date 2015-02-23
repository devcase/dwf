/**
 * 
 */
package dwf.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Levy
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ReCaptcha {
	/* 
	 * set recaptcha public and private key on application.properties
	 */
	@Deprecated
	String privateKey() default "";
	
}
