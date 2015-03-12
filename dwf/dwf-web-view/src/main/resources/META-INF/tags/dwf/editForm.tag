<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ tag dynamic-attributes="attrMap"%>
<%@ variable name-given="entity" scope="NESTED" variable-class="java.lang.Object"%>
<c:if test="${!empty entityName}">
	<dwf:resolveEL el="${entityName}" var="entity"/>
</c:if>
<dwf:mergeMaps map1="${attrMap}" map2="${attrMap.parentAttrMap}" var="attrMap"/>
<c:set var="formaction" value="${attrMap.formaction}"/>
<c:if test="${empty attrMap.formaction}">
	<c:set var="formaction" value="${appPath}/${entityName}/save"/>
</c:if>
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
<%-- DETERMINAR LABEL DO BOTÃO --%>
<c:set var="buttonLabelKey" value="${!empty attrMap.buttonLabelKey ? attrMap.buttonLabelKey : 'action.save'}"/>

<c:set var="formLayout" value="${!empty formLayout ? formLayout : 'horizontal'}" scope="request"/>

<c:if test="${!attrMap.panelless}">
	<c:if test="${!empty panelTitle and panelTitle ne 'none'}"><h1>${panelTitle} </h1></c:if>
	<div class="panel panel-default">
		<div class="panel-body">
</c:if>


		<form class="form-horizontal validate" method="POST" action="${formaction}" role="form" <c:if test="${!empty attrMap.formId}">id="${attrMap.formId}"</c:if>>
			<sec:csrfInput />
			<c:if test="${!empty entity }">
				<input type="hidden" name="id" value="${entity.id}"/>
			</c:if>
			<jsp:doBody />
			<div class="text-right">
				<c:if test="${attrMap.closemodalbutton eq true}"><%-- Close button, when in a modal (see modal.tag) --%>
			 		<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="action.cancel"/></button>
			 	</c:if>
				<button type="submit" class="btn btn-primary" data-loading-text="<spring:message code="action.wait"/>" formaction="${formaction}">
			 		<spring:message code="${buttonLabelKey}"/>
				</button>
			</div>
		
		
<c:if test="${!attrMap.panelless}">
		</div><!-- /.box-content -->
	</div>
</c:if>
<c:set var="formLayout" value="" scope="request"/>