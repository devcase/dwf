<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>
<dwf:formGroup parentAttrMap="${attrMap}">
	<input type="${empty attrMap.type ? 'text' : attrMap.type}" value="<dwf:autoFormat value='${value}'/>" name="${name}"
		<c:if test="${attrMap.required}">required="required"</c:if>
		<c:if test="${!empty attrMap.placeholder}">placeholder="<spring:message code="${attrMap.placeholder}" text="${attrMap.placeholder}"/>"</c:if>
		maxlength="254"
		class='form-control <c:if test="${attrMap.required}">required</c:if> validate-email dwf-auto-trimandlowercase' />
</dwf:formGroup>
