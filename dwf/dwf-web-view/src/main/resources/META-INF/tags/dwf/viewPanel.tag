<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>
<dwf:resolveEL el='${entityName}' var="entity"/>

<%-- DETERMINAR TÍTULO --%>
<c:choose>
	<c:when test="${!empty attrMap.title}">
		<c:set var="panelTitle" value="${attrMap.title}"/>
	</c:when>
	<c:when test="${!empty attrMap.labelKey}">
		<spring:message code="${attrMap.labelKey}" var="panelTitle"/>
	</c:when>
	<c:when test="${!empty entityName}">
		<spring:message code="label.editForm.header.${empty entity.id ? 'create' : 'edit'}" var="panelTitle"/>
		<spring:message code="domain.${entityName}" var="entityDisplayName"/>
		<c:set var="panelTitle" value="${panelTitle} ${entityDisplayName }"/>
	</c:when>
</c:choose><%-- /DETERMINAR TÍTULO --%>

<c:if test="${!attrMap.panelless}">
	<c:if test="${empty attrMap.titleType or attrMap.titleType eq 'h1'}">
		<c:if test="${!empty panelTitle and panelTitle ne 'none'}"><h1>${panelTitle}</h1></c:if>
	</c:if>
	<div class="panel panel-default center-block" style="${attrMap.panelStyle}">
	<c:if test="${attrMap.titleType eq 'panel-heading'}">
		<c:if test="${!empty panelTitle and panelTitle ne 'none'}">
		<div class="panel-heading"><h3 class="panel-title">${panelTitle}</h3></div>
		</c:if>
	</c:if>
		<div class="panel-body">
</c:if>




		<div class="form-horizontal" >
			<jsp:doBody />
			<%-- FIELDS ATTRIBUTE --%>
			<c:if test="${!empty attrMap.fields}">
				<c:forTokens items="${attrMap.fields}" delims="," var="fieldName"><dwf:outputText property="${fieldName}"/>
				</c:forTokens>
			</c:if>
			<%-- ENABLED - shows by default --%>
			<c:if test="${empty attrMap.showEnabledField || attrMap.showEnabledField eq true}"><dwf:outputText property="enabled" styleClass="label ${entity.enabled eq true ? 'label-success' : 'label-danger'}"></dwf:outputText></c:if>
		</div>
		
<c:if test="${!attrMap.panelless}">		
	</div>
</div>
</c:if>
