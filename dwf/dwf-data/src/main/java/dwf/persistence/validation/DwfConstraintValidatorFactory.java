package dwf.persistence.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;

import org.hibernate.validator.internal.util.ReflectionHelper;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Search for the validator from the applicationContext
 * @author Hirata
 *
 */
public class DwfConstraintValidatorFactory implements ConstraintValidatorFactory, ApplicationContextAware {
	private ApplicationContext applicationContext;
	/**
	 * @param key The class of the constraint validator to instantiate
	 *
	 * @return A new constraint validator instance of the specified class
	 */

	@Override
	public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
		try {
			return applicationContext.getBean(key);
		} catch (Exception ex) {
			return ReflectionHelper.newInstance( key, "ConstraintValidator" );
		}
	}

	/**
	 * Signals {@code ConstraintValidatorFactory} that the instance is no longer
	 * being used by the Bean Validation provider.
	 *
	 * @param instance validator being released
	 *
	 * @since 1.1
	 */
	@Override
	public void releaseInstance(ConstraintValidator<?, ?> instance) {
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

}