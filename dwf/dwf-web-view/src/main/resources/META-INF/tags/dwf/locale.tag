<%@tag import="org.springframework.web.servlet.DispatcherServlet"%><%@tag import="org.springframework.web.servlet.LocaleResolver"%><%@tag import="java.util.Locale"
%><%@ attribute name="var" required="false" type="java.lang.String"
%><%@ attribute name="format" required="false" type="java.lang.String"
%><%
Locale locale = org.springframework.context.i18n.LocaleContextHolder.getLocale(); 
if(getJspContext().getAttribute("var") != null) {
	Object value;
	if(locale == null) {
		value = null;
	}
	else if("dash".equals(getJspContext().getAttribute("format"))) { 
		value = locale.getLanguage() + "-" + locale.getCountry();
	} else if("underscore".equals(getJspContext().getAttribute("format"))) { 
		value = locale.getLanguage() + "_" + locale.getCountry();
	} else if("language".equals(getJspContext().getAttribute("format"))) { 
		value = locale.getLanguage();
	} else if("country".equals(getJspContext().getAttribute("format"))) {
		value = locale.getCountry();
	} else {
		value = locale;
	}

	
	getJspContext().setAttribute((String) getJspContext().getAttribute("var"), value, PageContext.REQUEST_SCOPE);
} else {
	if(locale != null) {
		if("dash".equals(getJspContext().getAttribute("format"))) { 
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
		} else {
			out.print(locale.toLanguageTag());
		}
	}
}
%>