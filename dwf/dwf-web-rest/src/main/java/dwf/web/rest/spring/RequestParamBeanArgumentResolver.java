package dwf.web.rest.spring;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
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
		
		Object target = BeanUtils.instantiateClass(parameter.getParameterType());
		WebDataBinder dataBinder = binderFactory.createBinder(webRequest, target, methodArgumentName);
		MutablePropertyValues mpvs = new MutablePropertyValues(params);
		dataBinder.bind(mpvs);
		validateIfApplicable(dataBinder, parameter);
		
		mavContainer.addAttribute(methodArgumentName, target);
		mavContainer.addAttribute(BindingResult.MODEL_KEY_PREFIX + methodArgumentName, dataBinder.getBindingResult());
		return target;
	}

	/**
	 * Validate the model attribute if applicable.
	 * <p>The default implementation checks for {@code @javax.validation.Valid},
	 * Spring's {@link org.springframework.validation.annotation.Validated},
	 * and custom annotations whose name starts with "Valid".
	 * @param binder the DataBinder to be used
	 * @param methodParam the method parameter
	 */
	protected void validateIfApplicable(WebDataBinder binder, MethodParameter methodParam) {
		Annotation[] annotations = methodParam.getParameterAnnotations();
		for (Annotation ann : annotations) {
			Validated validatedAnn = AnnotationUtils.getAnnotation(ann, Validated.class);
			if (validatedAnn != null || ann.annotationType().getSimpleName().startsWith("Valid")) {
				Object hints = (validatedAnn != null ? validatedAnn.value() : AnnotationUtils.getValue(ann));
				Object[] validationHints = (hints instanceof Object[] ? (Object[]) hints : new Object[] {hints});
				binder.validate(validationHints);
				break;
			}
		}
	}
}
