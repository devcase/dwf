/**
 * 
 */
package dwf.persistence.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Hirata
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Image {
	int targetWidth() default 0;
	int targetHeight() default 0;
	String[] thumbnail() default {};
	boolean noTransparency() default false;
	String transparencyColor() default "#FFFFFF";
	int maxHeight() default 0;
	int maxWidth() default 0;
	int fitMode() default FIT_CROP;
	
	public static final int FIT_INSIDE = 1;
	public static final int FIT_CROP = 0;
}
