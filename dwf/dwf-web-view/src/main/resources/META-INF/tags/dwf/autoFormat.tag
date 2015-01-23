<%-- ATENÇÃO - NÃO quebre linhas! --%><%@tag import="org.springframework.web.context.support.WebApplicationContextUtils"%><%@tag import="dwf.multilang.TranslationManager"%><%@tag import="java.util.Locale"%><%@tag import="org.springframework.context.i18n.LocaleContextHolder"%><%@tag import="dwf.multilang.domain.BaseMultilangEntity"%><%@tag import="java.util.Calendar"%><%@tag import="java.util.Date"%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%><%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%><%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%><%@taglib uri="http://www.springframework.org/tags" prefix="spring"%><%@ attribute name="value" required="true" type="java.lang.Object"%><%
	Object value = getJspContext().getAttribute("value");

	if(value == null) {
		return;
	} else if (value instanceof CharSequence) {
		getJspContext().setAttribute("format", "string");
	} else if (value instanceof Number) {
		getJspContext().setAttribute("format", "number");
	} else if (value instanceof Date || value instanceof Calendar) {
		long timeinmillis = value instanceof Date ? ((Date) value).getTime() : ((Calendar) value).getTimeInMillis();
		if(timeinmillis <= 24*60*60*1000 && timeinmillis >= 0) {
			getJspContext().setAttribute("format", "time");
		} else if (timeinmillis % ((long) 24*60*60*1000) == 0) {
			getJspContext().setAttribute("format", "date");
		} else {
			getJspContext().setAttribute("format", "datetime");
		}
	} else if (value instanceof Boolean) {
		getJspContext().setAttribute("format", "boolean");
	} else if (value instanceof java.util.Collection){
		getJspContext().setAttribute("format", "collection");
	} else if(value != null && value.getClass().isEnum()) {
		getJspContext().setAttribute("format", "enum");
		getJspContext().setAttribute("enumClassName", value.getClass().getName());
	} else if(value instanceof BaseMultilangEntity) {
		//multilang!
		Locale locale = LocaleContextHolder.getLocale();
		String localeCode = locale.toLanguageTag();
		TranslationManager tm = WebApplicationContextUtils.getWebApplicationContext(application).getBean(TranslationManager.class);
		String translation = tm.getTranslation((BaseMultilangEntity) value, "name", localeCode);
		if(translation != null) {
			out.append(translation);
			return;
		}
	}
%><%-- ATENÇÃO - Não adicione quebras de linha à saída --%><c:choose><c:when test="${format eq 'string'}"><dwf:escapeHtml value="${value}" /></c:when><c:when 
	test="${format eq 'boolean'}"><dwf:yesNo value="${value}" /></c:when><c:when 
	test="${format eq 'date'}"><fmt:formatDate value="${value}" type="date"  pattern="${datePatternJava}" /></c:when><c:when 
	test="${format eq 'time'}"><fmt:formatDate value="${value}" type="time" timeStyle="SHORT" /></c:when><c:when 
	test="${format eq 'datetime'}"><fmt:formatDate value="${value}" type="date"  pattern="${datePatternJava}" /> <fmt:formatDate value="${value}" type="time" timeStyle="SHORT" /></c:when><c:when 
	test="${format eq 'number'}"><fmt:formatNumber value="${value}" maxFractionDigits="6"/></c:when><c:when 
	test="${format eq 'collection'}"><c:forEach items="${value}" var="item" varStatus="loopStatus">${loopStatus.count > 1 ? ', ' : ''}<dwf:autoFormat value="${item}"/></c:forEach></c:when><c:when 
	test="${format eq 'enum'}"><spring:message code="${enumClassName}.${value}" text="${enumClassName}.${value}"/></c:when><c:otherwise
	>${value}</c:otherwise></c:choose>