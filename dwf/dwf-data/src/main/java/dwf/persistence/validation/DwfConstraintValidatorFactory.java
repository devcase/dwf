package dwf.persistence.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

/**
 * Search for the validator from the applicationContext
 * @author Hirata
 *
 */
public class DwfConstraintValidatorFactory implements ConstraintValidatorFactory, ApplicationContextAware {
	private ApplicationContext applicationContext;
//	/**
//	 * @param key The class of the constraint validator to instantiate
//	 *
//	 * @return A new constraint validator instance of the specified class
//	 */
//
//	@Override
//	public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
//		try {
//			return applicationContext.getBean(key);
//		} catch (Exception ex) {
//			System.out.println(key);
//			return ReflectionHelper.newInstance( key, "ConstraintValidator" );
//		}
//	}
//
//	/**
//	 * Signals {@code ConstraintValidatorFactory} that the instance is no longer
//	 * being used by the Bean Validation provider.
//	 *
//	 * @param instance validator being released
//	 *
//	 * @since 1.1
//	 */
//	@Override
//	public void releaseInstance(ConstraintValidator<?, ?> instance) {
//	}
//
	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}
//
	

	private final AutowireCapableBeanFactory beanFactory;


	/**
	 * Create a new SpringConstraintValidatorFactory for the given BeanFactory.
	 * @param beanFactory the target BeanFactory
	 */
	public DwfConstraintValidatorFactory(ApplicationContext applicationContext) {
		
//		Assert.notNull(beanFactory, "BeanFactory must not be null");
		this.beanFactory = applicationContext.getAutowireCapableBeanFactory();
	}


	@Override
	public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> key) {
		System.out.println(key + "====");
		try {
			T t =  applicationContext.getBean(key);
			System.out.println(t + "====2");
			return t;
		} catch (Exception ex) {
			T t = this.beanFactory.createBean(key);
			System.out.println(t + "====3");
			return t;
		}
	}

	public void releaseInstance(ConstraintValidator<?, ?> instance) {
		this.beanFactory.destroyBean(instance);
	}

}
