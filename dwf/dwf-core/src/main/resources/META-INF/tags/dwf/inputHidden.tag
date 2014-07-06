<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>
<%-- VALOR PADRÃO E NOME DO INPUT --%>
<c:choose>
	<c:when test="${!empty attrMap.name}">
		<dwf:resolveEL el="${attrMap.name}" var="value" />
		<c:set var="name" value="${attrMap.name}"/>		
	</c:when>
	<c:otherwise><%-- PROPERTY --%>
		<dwf:resolveEL el="${entityName}.${attrMap.property}" var="value" />
		<c:set var="name" value="${attrMap.property}"/>
	</c:otherwise>
</c:choose>
<c:if test="${!attrMap.ignoreParams and !empty param[name]}"><%-- usa o parâmetro do request no lugar da propriedade--%>
	<c:set var="value" value="${param[name]}"/>
</c:if>

<input type="hidden" name="${name}" value="${value}"/>
