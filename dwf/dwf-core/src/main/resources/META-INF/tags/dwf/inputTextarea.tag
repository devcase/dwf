<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>
<dwf:formGroup parentAttrMap="${attrMap}">
	<textarea name="${name}"
		<c:if test="${!empty attrMap.pattern}">pattern="${attrMap.pattern}"</c:if>
		<c:if test="${attrMap.required}">required="required"</c:if>
		<c:if test="${!empty attrMap.maxlength}">maxlength="${attrMap.maxlength}"</c:if>
			rows="${!empty attrMap.maxlength ? (attrMap.maxlength /100 <= 6 ? attrMap.maxlength /100 : 6) : '6'}"
			class='form-control <c:if test="${attrMap.required}">required</c:if>' ><c:out value="${value}"/></textarea>
</dwf:formGroup>