<%-- ATEN��O - N�O quebre linhas! --%><%@tag import="dwf.persistence.embeddable.Price"%><%@tag import="java.util.TimeZone"%><%@tag import="org.apache.commons.lang3.time.DateUtils"%><%@tag import="org.springframework.web.context.support.WebApplicationContextUtils"%><%@tag import="dwf.multilang.TranslationManager"%><%@tag import="java.util.Locale"%><%@tag import="org.springframework.context.i18n.LocaleContextHolder"%><%@tag import="dwf.multilang.domain.BaseMultilangEntity"%><%@tag import="java.util.Calendar"%><%@tag import="java.util.Date"%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%><%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%><%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%><%@taglib uri="http://www.springframework.org/tags" prefix="spring"
%><%@ attribute name="format" required="false" type="java.lang.String"%><%
%><%@ attribute name="value" required="true" type="java.lang.Object"%><%
	Object value = getJspContext().getAttribute("value");
	if(value == null) {
		return;
	}
	
	String format = (String) getJspContext().getAttribute("format");
	if(format != null && !format.isEmpty()) {
	} else if (value instanceof CharSequence) {
		format = "string";
	} else if (value instanceof Number) {
		if(value instanceof Integer || value instanceof Long) {
			format = "integer";
		} else {
			format = "number";
		}
	} else if (value instanceof Date || value instanceof Calendar) {
		long timeinmillis = value instanceof Date ? ((Date) value).getTime() : ((Calendar) value).getTimeInMillis();
		long dateinmillis = value instanceof Date ? DateUtils.truncate((Date) value, Calendar.DATE).getTime() : DateUtils.truncate((Calendar) value, Calendar.DATE).getTimeInMillis();
		boolean hasTime;
		boolean hasDate;
		
		int timezoneOffset = value instanceof Date ? DateUtils.toCalendar((Date) value).getTimeZone().getRawOffset() : ((Calendar) value).getTimeZone().getRawOffset();
		timeinmillis += timezoneOffset;
		dateinmillis += timezoneOffset;
		
		hasDate = dateinmillis != 0;
		hasTime = dateinmillis != timeinmillis;
		
		if(hasTime && hasDate) {
			format = "datetime";
		} else if (hasDate) {
			format = "date";
		} else {
			format = "time";
		}
	} else if (value instanceof TimeZone) {
		format = "timezone";
	} else if (value instanceof Boolean) {
		format = "boolean";
	} else if (value instanceof java.util.Collection){
		format = "collection";
	} else if(value != null && value.getClass().isEnum()) {
		format = "enum";
		getJspContext().setAttribute("enumClassName", value.getClass().getName());
	} else if(value instanceof Price) {
		format = "price";
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
	getJspContext().setAttribute("format", format);		
	
	if ("date".equals(format) || "datetime".equals(format)) {
		String datePatternJava;
		Locale locale = LocaleContextHolder.getLocale();
		if(locale.equals(Locale.US)) {
			datePatternJava = "MM/dd/yyyy";
		} else if(locale.equals(Locale.JAPAN) || locale.equals(Locale.CHINA) || locale.equals(Locale.KOREAN)){
			datePatternJava = "yyyy/MM/dd";
		} else {
			datePatternJava = "dd/MM/yyyy";
		}
		getJspContext().setAttribute("datePatternJava", datePatternJava);
	}
%><%-- ATEN��O - N�o adicione quebras de linha � sa�da --%><c:choose><c:when test="${format eq 'string'}"><dwf:escapeHtml value="${value}" /></c:when><c:when 
	test="${format eq 'boolean'}"><dwf:yesNo value="${value}" /></c:when><c:when 
	test="${format eq 'date'}"><fmt:formatDate value="${value}" type="date"  pattern="${datePatternJava}" /></c:when><c:when 
	test="${format eq 'time'}"><fmt:formatDate value="${value}" type="time" timeStyle="SHORT" /></c:when><c:when 
	test="${format eq 'datetime'}"><fmt:formatDate value="${value}" type="date"  pattern="${datePatternJava}" /> <fmt:formatDate value="${value}" type="time" timeStyle="SHORT" /></c:when><c:when
	test="${format eq 'timezone'}">${value.ID}</c:when><c:when 
	test="${format eq 'number'}"><fmt:formatNumber value="${value}" maxFractionDigits="6"/></c:when><c:when 
	test="${format eq 'integer'}"><fmt:formatNumber value="${value}" groupingUsed="false"/></c:when><c:when 
	test="${format eq 'collection'}"><c:forEach items="${value}" var="item" varStatus="loopStatus">${loopStatus.count > 1 ? ', ' : ''}<dwf:autoFormat value="${item}"/></c:forEach></c:when><c:when 
	test="${format eq 'price'}"><spring:message code="currency.symbol.${value.currencyCode}" text=""/> <fmt:formatNumber value="${value.value}" maxFractionDigits="2" minFractionDigits="2"/> </c:when><c:when 
	test="${format eq 'enum'}"><spring:message code="${enumClassName}.${value}" text="${enumClassName}.${value}"/></c:when><c:otherwise
	>${value}</c:otherwise></c:choose>