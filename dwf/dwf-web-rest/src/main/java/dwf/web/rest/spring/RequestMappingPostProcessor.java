package dwf.web.rest.spring;

import java.util.ArrayList;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

@Component
public class RequestMappingPostProcessor implements BeanPostProcessor {

	@Autowired
	private ParsedMapArgumentResolver parsedMapArgumentResolver;
	
    public Object postProcessBeforeInitialization(Object bean, String name) throws BeansException {
        if (bean instanceof RequestMappingHandlerAdapter) {
        	RequestMappingHandlerAdapter requestMappingHandlerAdapter = (RequestMappingHandlerAdapter) bean;
        	requestMappingHandlerAdapter.setIgnoreDefaultModelOnRedirect(true);
    		requestMappingHandlerAdapter.setCustomArgumentResolvers(new ArrayList<HandlerMethodArgumentResolver>());
    		requestMappingHandlerAdapter.getCustomArgumentResolvers().add(parsedMapArgumentResolver);
        }
        return bean;
    }

	@Override
	public Object postProcessAfterInitialization(Object bean, String name)
			throws BeansException {
		return bean;
	}

}
