package dwf.web.spring;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.method.annotation.InitBinderDataBinderFactory;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

/**
 * Custom RequestMappingHandlerAdapter
 * @author Hirata
 *
 */
public class DwfRequestMappingHandlerAdapter extends RequestMappingHandlerAdapter {

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#createDataBinderFactory(java.util.List)
	 */
	@Override
	protected InitBinderDataBinderFactory createDataBinderFactory(List<InvocableHandlerMethod> binderMethods) throws Exception {
		return new DwfServletRequestDataBinderFactory(binderMethods, getWebBindingInitializer());
	}

}
