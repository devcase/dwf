<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
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
<c:set var="violation" value="${validationException.violationsMap[attrMap.property]}"/><%-- VALIDATION ERROR --%>
<%-- LABEL --%>
<c:choose>
	<c:when test="${!empty attrMap.label}"><%-- LABEL DEFINIDA NA TAG --%>
		<c:set var="_label" value="${attrMap.label}"/>
	</c:when>
	<c:otherwise><%-- LABEL PADRÃO --%>
		<c:set var="_label" value="${entityName}.${attrMap.property}"/> 
	</c:otherwise>
</c:choose>
<div class="form-group">
	<div class="checkbox col-sm-9 col-sm-offset-3">
		<label >
			<input type="checkbox" value="true" name="${_name}"
				<c:if test="${_value}">checked</c:if>/><spring:message
					code="${_label}"/>
		</label>
	</div>
</div>
		