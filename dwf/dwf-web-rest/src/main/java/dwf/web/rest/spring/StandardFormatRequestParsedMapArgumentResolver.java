package dwf.web.rest.spring;

import java.util.Map;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import dwf.web.rest.spring.ParsedMapArgumentResolver.RequestParsedMap;

/**
 * Habilita o uso de StandardFormatRequestParsedMap como argumento de controllers 
 * @author hirata
 *
 */
@Component
public class StandardFormatRequestParsedMapArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return StandardFormatRequestParsedMap.class.isAssignableFrom(parameter.getParameterType());
	}

	@Override
	public Object resolveArgument(MethodParameter parameter,
			ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) throws Exception {
		//String parameterPrefix = parameter.getParameterName().concat(".");
		Map<String, String[]> parameterMap = webRequest.getParameterMap();
		return new StandardFormatRequestParsedMap(parameterMap, "");
	}

}
