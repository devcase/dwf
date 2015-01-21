package dwf.web;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.LocaleResolver;

public class DefaultLocaleResolver implements LocaleResolver{

	@Override
	public Locale resolveLocale(HttpServletRequest request) {
		if(request.getAttribute("dwf-locale") == null)
			return new Locale("pt", "BR");
		else 
			return (Locale) request.getAttribute("dwf-locale");
	}

	@Override
	public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
		request.setAttribute("dwf-locale", locale);
	}

}
