<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<%@ tag dynamic-attributes="attrMap"%>
<dwf:formGroup parentAttrMap="${attrMap}">
<textarea class="ckeditor" name="${name}"
<%-- CKEditor ignora outros par�metro, por isso est�o comentados --%>
<%-- 		<c:if test="${!empty attrMap.pattern}">pattern="${attrMap.pattern}"</c:if> --%>
<%-- 		<c:if test="${attrMap.required}">required="required"</c:if> --%>
<%-- 		<c:if test="${!empty attrMap.maxlength}">maxlength="${attrMap.maxlength}"</c:if> --%>
<%-- 		<c:if test="${!empty attrMap.minlength}">minlength="${attrMap.minlength}"</c:if> --%>
<%-- 		class='form-control <c:if test="${attrMap.required}">required</c:if>'--%>
	>${value}</textarea>
</dwf:formGroup>