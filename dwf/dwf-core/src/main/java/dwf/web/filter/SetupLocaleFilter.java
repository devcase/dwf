package dwf.web.filter;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.jstl.core.Config;

import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.LocaleResolver;

/**
 * Filtro que busca de UserLocaleAwareBean o locale e seta em uma variável de sessão que será usado
 * pelas tags fmt do JSTL.<br/>
 * @author Hirata
 *
 */
public class SetupLocaleFilter extends OncePerRequestFilter {
	public static String DATE_PATTERN_JAVA_ATTRIBUTE = "datePatternJava";

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		if(request.getAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE) == null) {
			request.setAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE, WebApplicationContextUtils.getWebApplicationContext(getServletContext()).getBean(LocaleResolver.class));
		}
		
		LocaleResolver localeResolver = (LocaleResolver) request.getAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE);
		
		if(request.getParameter("lang") != null && request.getParameter("country") != null) {
			Locale newLocale = new Locale(request.getParameter("lang"), request.getParameter("country"));
			localeResolver.setLocale(request, response, newLocale);
		}
		
		Locale locale = localeResolver.resolveLocale(request);
		Config.set( ((HttpServletRequest)request).getSession(true), Config.FMT_LOCALE, locale);
		((HttpServletRequest)request).getSession(true).setAttribute(Config.FMT_LOCALE, locale);
		request.setAttribute("javax.servlet.jsp.jstl.fmt.locale.request", locale);
		request.setAttribute("locale", locale);
		
		String datePatternJava;
		String datePattern;
		String datePatternDatePicker;
		if(locale.equals(Locale.US)) {
			datePattern = "mm/dd/yyyy";
			datePatternJava = "MM/dd/yyyy";
			datePatternDatePicker="mm/dd/yy";
		} else if(locale.equals(Locale.JAPAN) || locale.equals(Locale.CHINA) || locale.equals(Locale.KOREAN)){
			datePattern = "yyyy/mm/dd";
			datePatternJava = "yyyy/MM/dd";
			datePatternDatePicker="yy/mm/dd";
		} else {
			datePattern = "dd/mm/yyyy";
			datePatternJava = "dd/MM/yyyy";
			datePatternDatePicker="dd/mm/yy";
		}
		request.setAttribute("datePattern", datePattern);
		request.setAttribute("datePatternDatePicker", datePatternDatePicker);
		request.setAttribute(DATE_PATTERN_JAVA_ATTRIBUTE, datePatternJava);
		
		filterChain.doFilter(request, response);
	}
}
