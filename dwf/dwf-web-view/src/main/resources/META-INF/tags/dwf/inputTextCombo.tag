<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>
<c:set var="availableValues" value="${attrMap.items}"/>
<%
if(jspContext.getAttribute("availableValues") instanceof String) {
	jspContext.setAttribute("availableValues", 
			((String) jspContext.getAttribute("availableValues")).split(","));
}
%>
<%@tag import="java.util.TimeZone"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>

<dwf:formGroup parentAttrMap="${attrMap}">
	<select name="${name}"
		<c:if test="${attrMap.required}">required="required"</c:if> class="form-control">
		<option value=""><spring:message code="label.select.empty${attrMap.required ? '.required' : ''}"/></option>
		<c:forEach items="${availableValues}" var="option">
		<option value="${option}"
				<c:if test="${option eq value}">selected</c:if>
				>${option}</option>
		</c:forEach>
	</select>
</dwf:formGroup>