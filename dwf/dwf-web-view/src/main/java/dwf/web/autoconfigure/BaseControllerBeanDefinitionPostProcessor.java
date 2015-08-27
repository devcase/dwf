package dwf.web.autoconfigure;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.web.context.WebApplicationContext;

import dwf.web.controller.BaseController;

/**
 * Assure that all beans that extends BaseController:
 * <ul>
 * <li>have the appropriate scope</li>
 * <li>are deregistered if it is not a web application environment</li>
 * </ul>
 * 
 * @author hirata
 *
 */
public class BaseControllerBeanDefinitionPostProcessor implements BeanFactoryPostProcessor {
	private Log logger = LogFactory.getLog(getClass());

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		String[] beanNames = beanFactory.getBeanDefinitionNames();
		for (String curName : beanNames) {
			BeanDefinition bd = beanFactory.getBeanDefinition(curName);
			if(!bd.getScope().equals(WebApplicationContext.SCOPE_REQUEST) ) {
				if(bd.getBeanClassName() == null){
					continue;
				}
				try {
					Class<?> defclass = Class.forName(bd.getBeanClassName());
					if(BaseController.class.isAssignableFrom(defclass)) {
						logger.debug("Definition of BaseController bean named " + curName + " is not request - changing it");
						bd.setScope(WebApplicationContext.SCOPE_REQUEST);
					}
					
				} catch (ClassNotFoundException e) {
					logger.warn("Classe não encontrada do bean " + curName, e);
				} 
			}
		}

	}

}
