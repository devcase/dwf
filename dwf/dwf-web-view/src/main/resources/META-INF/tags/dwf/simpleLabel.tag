<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%><%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%><%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%><%@taglib uri="http://www.springframework.org/tags" prefix="spring"%><%@ tag dynamic-attributes="attrMap"
%><c:set var="var" value="${attrMap.var}"/><%
%><dwf:mergeMaps map1="${attrMap}" map2="${attrMap.parentAttrMap}" var="attrMap"/><%
%><c:if test="${!empty attrMap.entityName}"><%-- NOME DA ENTIDADE DEFINIDO NA TAG --%><%
	%><c:set var="entityName" value="${attrMap.entityName}"/><%
%></c:if><%
%><c:if test="${!empty attrMap.label}"><%-- LABEL DEFINIDA NA TAG --%><%
	%><c:set var="_labelText" value="${attrMap.label }"/><%
%></c:if><%
%><c:if test="${!empty attrMap.labelKey}"><%-- LABEL DEFINIDA NA TAG --%><%
	%><spring:message code="${attrMap.labelKey}" var="_labelText" text=""/><%
%></c:if><%
%><c:if test="${empty _labelText}"><%-- LABEL ESPECÍFICA DA ENTIDADE --%><%
	%><spring:message code="${entityName}.${attrMap.property}" var="_labelText" text=""/><%
%></c:if><%
%><c:if test="${empty _labelText}"><%-- PROCURA POR LABEL GENÉRICO (ex: label.enabled) --%><%
	%><spring:message code="label.${attrMap.property}" var="_labelText"  text=""/><%
%></c:if><%
%><c:if test="${empty _labelText}"><%-- PROCURA POR LABEL GENÉRICO COM O ATRIBUTO NAME (ex: label.enabled) --%><%
	%><spring:message code="label.${attrMap.name}" var="_labelText"  text=""/><%
%></c:if><%
%><c:if test="${empty _labelText}"><%-- PROCURA POR NOME DE ENTIDADE --%><%
	%><spring:message code="domain.${attrMap.property}" var="_labelText"  text="${attrMap.property }"/><%
%></c:if><%
%><c:if test="${_labelText eq attrMap.property}"><%-- USA O NOME DA PROPRIEDADE --%><%
	String text =  (String) getJspContext().getAttribute("_labelText");
	if(text.length() > 0){
		text = text.replaceAll("(\\p{Ll})(\\p{Lu})","$1 $2");
		getJspContext().setAttribute("_labelText", (char)((int)text.charAt(0) + ('A'-'a')) + text.substring(1));
	}
%></c:if><%
%><%
String varName = (String) getJspContext().getAttribute("var");
Object value =  getJspContext().getAttribute("_labelText");

if(varName != null && varName != "") {
	getJspContext().setAttribute(varName, value, PageContext.REQUEST_SCOPE);
} else {
	if(value != null && value.toString() != null) {
		getJspContext().getOut().print(value);
	}
}
%>