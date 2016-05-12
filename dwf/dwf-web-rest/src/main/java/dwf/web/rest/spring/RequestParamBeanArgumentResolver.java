package dwf.web.rest.spring;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 
 * @author hirata
 *
 */
@Component
public class RequestParamBeanArgumentResolver implements HandlerMethodArgumentResolver {
	Log log = LogFactory.getLog(getClass());

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(RequestParamBean.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) throws Exception {
		RequestParamBean annotation = parameter.getParameterAnnotation(RequestParamBean.class);
		
		String methodArgumentName = parameter.getParameterName();
		String paramName = StringUtils.isBlank(annotation.paramPrefix()) ? methodArgumentName :  annotation.paramPrefix();
		
		Map<String, String[]> params = new HashMap<String, String[]> ();
		for (Iterator<String> iterator = webRequest.getParameterNames(); iterator.hasNext();) {
			String n = iterator.next();
			if(n.startsWith(paramName + ".")) {
				params.put(n.substring(paramName.length() + 1), webRequest.getParameterValues(n));
			}
		}
		if(params.isEmpty()) return null;
		
		Object target = BeanUtils.instantiateClass(parameter.getParameterType());
		WebDataBinder dataBinder = binderFactory.createBinder(webRequest, target, methodArgumentName);
		MutablePropertyValues mpvs = new MutablePropertyValues(params);
		dataBinder.bind(mpvs);
		if(dataBinder.getBindingResult().hasErrors()) {
			BindingResult bindingResult = dataBinder.getBindingResult();
			log.warn("Erro ao fazer o binding:" + bindingResult.getAllErrors());
		}
		return target;
	}

	
}
