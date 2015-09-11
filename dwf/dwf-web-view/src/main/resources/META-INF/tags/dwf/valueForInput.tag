<%@ tag dynamic-attributes="attrMap"
%><%--
	Finds out the value, label and name for a <dwf:input*> and exposes it as variables
--%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" 
%><%@taglib uri="http://www.springframework.org/security/tags" prefix="sec" 
%><%@taglib uri="http://www.springframework.org/tags" prefix="spring"
%><%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" 
%><%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"
%><%@ variable name-given="name" scope="AT_BEGIN" 
%><%@ variable name-given="value" scope="AT_BEGIN" variable-class="java.lang.Object"
%><%@ variable name-given="label" scope="AT_BEGIN" variable-class="java.lang.String"
%><dwf:mergeMaps map1="${attrMap}" map2="${attrMap.parentAttrMap}" var="attrMap"/>
<dwf:simpleLabel parentAttrMap="${attrMap}" var="label"/>
<%-- VALOR PADRÃO E NOME DO INPUT --%>
<c:choose>
	<c:when test="${!empty attrMap.name}">
		<dwf:resolveEL el="${attrMap.name}" var="value" />
		<c:set var="name" value="${attrMap.name}"/>		
	</c:when>
	<c:when test="${!empty attrMap.property}"><%-- PROPERTY --%>
		<dwf:resolveEL el="${entityName}.${attrMap.property}" var="value" />
		<c:set var="name" value="${attrMap.property}"/>
	</c:when>
</c:choose>
<c:if test="${!attrMap.ignoreParams and !empty param[name]}"><%-- usa o parâmetro do request no lugar da propriedade--%>
	<c:set var="value" value="${param[name]}"/>
</c:if>
<c:if test="${!empty attrMap.value}">
	<c:set var="value" value="${attrMap.value}"/>
</c:if>