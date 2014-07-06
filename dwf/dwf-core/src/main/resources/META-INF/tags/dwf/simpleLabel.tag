<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%><%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%><%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%><%@taglib uri="http://www.springframework.org/tags" prefix="spring"%><%@ tag dynamic-attributes="attrMap"%>
<c:set var="var" value="${attrMap.var}"/>

<dwf:mergeMaps map1="${attrMap}" map2="${attrMap.parentAttrMap}" var="attrMap"/>

<c:if test="${!empty attrMap.label}"><%-- LABEL DEFINIDA NA TAG --%>
	<c:set var="_labelText" value="${attrMap.label }"/>
</c:if>
<c:if test="${!empty attrMap.labelKey}"><%-- LABEL DEFINIDA NA TAG --%>
	<spring:message code="${attrMap.labelKey}" var="_labelText" text=""/>
</c:if>
<c:if test="${empty _labelText}"><%-- LABEL ESPECÍFICA DA ENTIDADE --%>
	<spring:message code="${entityName}.${attrMap.property}" var="_labelText" text=""/>
</c:if>
<c:if test="${empty _labelText}"><%-- PROCURA POR LABEL GENÉRICO (ex: label.enabled) --%>
	<spring:message code="label.${attrMap.property}" var="_labelText"  text=""/>
</c:if>
<c:if test="${empty _labelText}"><%-- PROCURA POR LABEL GENÉRICO COM O ATRIBUTO NAME (ex: label.enabled) --%>
	<spring:message code="label.${attrMap.name}" var="_labelText"  text="?${entityName}.${attrMap.property}?"/>
</c:if>
<%
String varName = (String) getJspContext().getAttribute("var");
Object value =  (String) getJspContext().getAttribute("_labelText");

if(varName != null && varName != "") {
	getJspContext().setAttribute(varName, value, PageContext.REQUEST_SCOPE);
} else {
	if(value != null && value.toString() != null) {
		getJspContext().getOut().print(value);
	}
}
%>