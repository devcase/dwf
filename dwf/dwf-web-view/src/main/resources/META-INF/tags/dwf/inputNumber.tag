<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>

<dwf:formGroup parentAttrMap="${attrMap}">
	<c:if test="${value.getClass().name ne 'java.lang.String'}">
		<fmt:formatNumber value="${value}" groupingUsed="false"  maxFractionDigits="7" var="value"/>
	</c:if>
	<input type="text" value='${value}' name="${name}"
		<c:if test="${!empty attrMap.pattern}">pattern="${attrMap.pattern}"</c:if>
		<c:if test="${attrMap.required}">required="required"</c:if>
		<c:if test="${!empty attrMap.maxlength}">maxlength="${attrMap.maxlength}"</c:if>
		<c:if test="${!empty attrMap.minlength}">minlength="${attrMap.minlength}"</c:if>
		<c:if test="${!empty attrMap.min}">min="${attrMap.min}"</c:if>
		<c:if test="${!empty attrMap.max}">min="${attrMap.max}"</c:if>
		class='form-control validate-number <c:if test="${attrMap.required}">required</c:if>' />
</dwf:formGroup>