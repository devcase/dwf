<%@tag import="org.springframework.web.servlet.DispatcherServlet"%><%@tag import="org.springframework.web.servlet.LocaleResolver"%><%@tag import="java.util.Locale"
%><%@ attribute name="var" required="false" type="java.lang.String"
%><%@ attribute name="format" required="false" type="java.lang.String"
%><%
Locale locale = org.springframework.context.i18n.LocaleContextHolder.getLocale(); 
if(getJspContext().getAttribute("var") != null) {
	getJspContext().setAttribute((String) getJspContext().getAttribute("var"), locale, PageContext.REQUEST_SCOPE);
} else {
	if(locale != null) {
		if(getJspContext().getAttribute("format") == null) { 
			out.print(locale.toLanguageTag());
		} else if("dash".equals(getJspContext().getAttribute("format"))) { 
			out.print(locale.getLanguage());
			out.print("-");
			out.print(locale.getCountry());
		} else if("underscore".equals(getJspContext().getAttribute("format"))) { 
			out.print(locale.getLanguage());
			out.print("_");
			out.print(locale.getCountry());
		} else if("language".equals(getJspContext().getAttribute("format"))) { 
			out.print(locale.getLanguage());
		} else if("country".equals(getJspContext().getAttribute("format"))) {
			out.print(locale.getCountry());
		}
	}
}
%>