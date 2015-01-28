<%@tag import="org.springframework.context.i18n.LocaleContextHolder"%>
<%@tag import="java.util.Locale"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>
<%
Locale locale = LocaleContextHolder.getLocale();
String datePatternDatePicker;
if(locale.equals(Locale.US)) {
	datePatternDatePicker="mm/dd/yy";
} else if(locale.equals(Locale.JAPAN) || locale.equals(Locale.CHINA) || locale.equals(Locale.KOREAN)){
	datePatternDatePicker="yy/mm/dd";
} else {
	datePatternDatePicker="dd/mm/yy";
}
getJspContext().setAttribute("datePatternDatePicker", datePatternDatePicker);

%>
<dwf:formGroup parentAttrMap="${attrMap}">
	<input type="text" value="<dwf:autoFormat value='${value}'/>" name="${name}"
		<c:if test="${attrMap.required}">required="required"</c:if>
		class='form-control <c:if test="${attrMap.required}">required</c:if> date-picker' data-date-format="${datePatternDatePicker}"/>
</dwf:formGroup>
