<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>

<%-- VALUE --%>
<c:choose>
	<c:when test="${!empty attrMap.name}">
		<dwf:resolveEL el="${attrMap.name}" var="_value" />
		<c:set var="_name" value="${attrMap.name}"/>		
	</c:when>
	<c:otherwise><%-- PROPERTY --%>
		<dwf:resolveEL el="${entityName}.${attrMap.property}" var="_value" />
		<c:set var="_name" value="${attrMap.property}"/>		
	</c:otherwise>
</c:choose>
<%-- VIOLATION --%>
<dwf:findViolation constraintViolationException="${validationException}" path="${attrMap.property}" var="violation"/>
<%-- LABEL --%>
<c:choose>
	<c:when test="${!empty attrMap.label}"><%-- LABEL DEFINIDA NA TAG --%>
		<c:set var="labelKey" value="${attrMap.label}"/>
	</c:when>
	<c:otherwise><%-- LABEL PADRÃO --%>
		<c:set var="labelKey" value="${entityName}.${attrMap.property}"/> 
	</c:otherwise>
</c:choose>

<div class="form-group ${!empty violation ?  'has-error' : ''}">
	<label
		class="col-sm-3 control-label ${empty attrMap.labelStyleClass ? '' :  attrMap.labelStyleClass}"><strong><spring:message
					code="${labelKey}"/>
			<c:if test="${attrMap.required}">*</c:if></strong> </label>
	<div class="col-sm-9">
		<input type="password" value="${_value}" name="${_name}"
			<c:if test="${!empty attrMap.pattern}">pattern="${attrMap.pattern}"</c:if>
			<c:if test="${attrMap.required}">required="required"</c:if>
			<c:if test="${!empty attrMap.maxlength}">maxlength="${attrMap.maxlength}"</c:if>
			class='form-control <c:if test="${attrMap.required}">required</c:if>' />
		<c:if test="${!empty violation}"><%-- VALIDATION ERROR --%>
			<span class="help-block">${violation.message}</span>
		</c:if>
	</div>
</div>
