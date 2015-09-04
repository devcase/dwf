<%@tag import="java.text.DecimalFormatSymbols"
%><%@tag import="org.springframework.web.servlet.DispatcherServlet"%><%@tag import="org.springframework.web.servlet.LocaleResolver"%><%@tag import="java.util.Locale"
%><%@ attribute name="var" required="false" type="java.lang.String"
%><%@ attribute name="format" required="false" type="java.lang.String"
%><%
Locale locale = org.springframework.context.i18n.LocaleContextHolder.getLocale(); 
Object value;
if(locale == null) {
	value = null;
} else if("dash".equals(getJspContext().getAttribute("format"))) { 
	value = locale.getLanguage() + "-" + locale.getCountry();
} else if("underscore".equals(getJspContext().getAttribute("format"))) { 
	value = locale.getLanguage() + "_" + locale.getCountry();
} else if("language".equals(getJspContext().getAttribute("format"))) { 
	value = locale.getLanguage();
} else if("country".equals(getJspContext().getAttribute("format"))) {
	value = locale.getCountry();
} else if("groupingSeparator".equals(getJspContext().getAttribute("format"))) {
	value = DecimalFormatSymbols.getInstance(locale).getGroupingSeparator();
} else if("decimalSeparator".equals(getJspContext().getAttribute("format"))) {
	value = DecimalFormatSymbols.getInstance(locale).getDecimalSeparator();
} else {
	value = locale;
}

	
if(getJspContext().getAttribute("var") != null) {
	getJspContext().setAttribute((String) getJspContext().getAttribute("var"), value, PageContext.REQUEST_SCOPE);
} else {
	if(value == null) {
		return;
	}
	else if(value instanceof Locale) {
		out.print(((Locale) value).toLanguageTag());
	} else {
		out.print(value);
	}
}
%>