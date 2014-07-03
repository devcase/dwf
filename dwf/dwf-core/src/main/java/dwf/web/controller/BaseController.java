package dwf.web.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.validation.ValidationException;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import dwf.security.DwfUserUtils;
import dwf.user.domain.User;
import dwf.web.DwfCustomDateEditor;
import dwf.web.filter.SetupLocaleFilter;
import dwf.web.message.UserMessage;
import dwf.web.message.UserMessageType;

@Scope(WebApplicationContext.SCOPE_REQUEST)
public abstract class BaseController implements ApplicationContextAware {
	protected static final String USER_MESSAGES_FLASH_MAP_KEY = "userMessagesList";
	protected static final String VALIDATION_EXCEPTION_FLASH_MAP_KEY = "validationException";
	protected static final String VIOLATIONS_MAP_EXCEPTION_FLASH_MAP_KEY = "violationsMap";
	protected static final String BACK_TO_URL_FLASH_MAP_KEY = "backToUrl";
	protected RedirectAttributes redirectAttributes;
	protected Model model;
	protected HttpServletResponse response;
	protected HttpServletRequest request;
	protected ApplicationContext applicationContext;

	/**
	 * Chamado pelo SpringMVC antes da execução de um método RedirectMapping
	 * deste controller.
	 * 
	 * @param redirectAttributes
	 */
	@ModelAttribute
	public void setupController(RedirectAttributes redirectAttributes, Model model, HttpServletResponse response, HttpServletRequest request) {
		this.model = model;
		this.redirectAttributes = redirectAttributes;
		this.response = response;
		this.request = request;
	}

	/**
	 * Cria instância de UserMessage e coloca em uma lista de mensagens. A lista
	 * de mensagens é guardada em um atributo flash (guardado na sessão mas
	 * utilizado no redirect seguinte)
	 * 
	 * @param key
	 * @param userMessageType
	 * @param model
	 */
	protected void addUserMessage(String key, UserMessageType userMessageType) {
		@SuppressWarnings("unchecked")
		List<UserMessage> list = (List<UserMessage>) redirectAttributes.getFlashAttributes().get(USER_MESSAGES_FLASH_MAP_KEY);
		if (list == null) {
			list = new ArrayList<UserMessage>();
			redirectAttributes.addFlashAttribute(USER_MESSAGES_FLASH_MAP_KEY, list);
		}
		model.addAttribute(USER_MESSAGES_FLASH_MAP_KEY, list);
		list.add(new UserMessage(key, userMessageType));
	}

	/**
	 * 
	 * @param validationException
	 */
	protected void addValidationExceptionMessage(ValidationException validationException) {
		redirectAttributes.addFlashAttribute(VALIDATION_EXCEPTION_FLASH_MAP_KEY, validationException);
		model.addAttribute(VALIDATION_EXCEPTION_FLASH_MAP_KEY, validationException);

		Map<String, ConstraintViolation<?>> violationsMap = new HashMap<String, ConstraintViolation<?>>();
		if (validationException != null && validationException instanceof ConstraintViolationException) {
			ConstraintViolationException constraintViolationException = (ConstraintViolationException) validationException;
			if (constraintViolationException.getConstraintViolations() != null) {
				for (ConstraintViolation<?> constraintViolation : constraintViolationException.getConstraintViolations()) {
					// path
					StringBuilder sb = new StringBuilder();
					for (Path.Node node : constraintViolation.getPropertyPath()) {
						if (sb.length() > 0)
							sb.append(".");
						sb.append(node.getName());
					}
					violationsMap.put(sb.toString(), constraintViolation);
				}
			}
		}

		redirectAttributes.addFlashAttribute(VIOLATIONS_MAP_EXCEPTION_FLASH_MAP_KEY, violationsMap);
		model.addAttribute(VIOLATIONS_MAP_EXCEPTION_FLASH_MAP_KEY, violationsMap);
	}
	
	/**
	 * 
	 * @param errors
	 */
	protected void addValidationErrorMessage(Object... violations) {
		Map<String, ConstraintViolation<?>> violationsMap = new HashMap<String, ConstraintViolation<?>>();
		for (int i = 0; i < violations.length; i+=2) {
			Object path = violations[i];
			Object violationObj = violations[i+1];
			ConstraintViolation<?> violation = null;
			
			violationsMap.put((String) path, violation);
		}

		redirectAttributes.addFlashAttribute(VIOLATIONS_MAP_EXCEPTION_FLASH_MAP_KEY, violationsMap);
		model.addAttribute(VIOLATIONS_MAP_EXCEPTION_FLASH_MAP_KEY, violationsMap);
	}	

	protected void setBackButton(String path) {
		if (path != null && path.length() > 0 && path.startsWith("/")) {
			path = path.substring(1);
		}
		model.addAttribute(BACK_TO_URL_FLASH_MAP_KEY, path);
		redirectAttributes.addFlashAttribute(BACK_TO_URL_FLASH_MAP_KEY, path);
	}

	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@InitBinder
	public void bindingPreparation(WebDataBinder binder, HttpServletRequest request) {
		Locale locale = RequestContextUtils.getLocale(request);
		DwfCustomDateEditor orderDateEditor = new DwfCustomDateEditor(locale);
		binder.registerCustomEditor(Date.class, orderDateEditor);
		binder.registerCustomEditor(Double.class, new CustomNumberEditor(Double.class, new DecimalFormat("0.0", new DecimalFormatSymbols(locale)), true));
	}
	
	protected User getCurrentUser(){
		return DwfUserUtils.getCurrentUser();
	}

}
