<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>
<%@ variable name-given="entity" scope="NESTED" variable-class="java.lang.Object"%>
<dwf:resolveEL el="${entityName}" var="entity"/>
<c:set var="formaction" value="${attrMap.formaction}"/>
<c:if test="${empty attrMap.formaction}">
	<c:set var="formaction" value=""/>
</c:if>
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

<spring:message code="label.translateForm.title" arguments="${label}" var="panelTitle"/>
<dwf:editForm formaction="${appPath}/${entityName}/translate/${entity.id}/${name}" title="${panelTitle}" parentAttrMap="${attrMap}">
	<dwf:inputText name="text" label="Português" value="${entity.translations['pt-BR'].text[name]}"/>
	<input type="hidden" name="language" value="pt-BR"/>
	<dwf:inputText name="text" label="Inglês"  value="${entity.translations['en-US'].text[name]}"/>
	<input type="hidden" name="language" value="en-US"/>
	<dwf:inputText name="text" label="Espanhol"  value="${entity.translations['es'].text[name]}"/>
	<input type="hidden" name="language" value="es"/>
</dwf:editForm>