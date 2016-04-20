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
	@Autowired
	private StandardFormatRequestParsedMapArgumentResolver standardFormatRequestParsedMapArgumentResolver;
	
    public Object postProcessBeforeInitialization(Object bean, String name) throws BeansException {
        if (bean instanceof RequestMappingHandlerAdapter) {
        	RequestMappingHandlerAdapter adapter = (RequestMappingHandlerAdapter) bean;
        	adapter.setIgnoreDefaultModelOnRedirect(true);
    		adapter.setCustomArgumentResolvers(new ArrayList<HandlerMethodArgumentResolver>());
    		adapter.getCustomArgumentResolvers().add(standardFormatRequestParsedMapArgumentResolver);
    		adapter.getCustomArgumentResolvers().add(parsedMapArgumentResolver);
    		adapter.getCustomArgumentResolvers().add(new DwfServletModelAttributeProcessor());
        }
        return bean;
    }

	@Override
	public Object postProcessAfterInitialization(Object bean, String name)
			throws BeansException {
		return bean;
	}

}
