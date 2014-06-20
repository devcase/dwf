<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>

<%-- VALUE --%>
<c:choose>
	<c:when test="${!empty attrMap.name}">
		<dwf:resolveEL el="${attrMap.name}" var="_value" />
		<c:set value="${attrMap.name}" var="_name" />		
	</c:when>
	<c:otherwise><%-- LABEL PADRÃO --%>
		<dwf:resolveEL el="${entityName}.${attrMap.property}" var="_value" />
		<c:set value="${attrMap.property}" var="_name" />
	</c:otherwise>
</c:choose>
<input type="hidden" value="${_value}" name="${_name}"/>
