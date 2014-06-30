<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>
<%@ variable name-given="entity" scope="AT_BEGIN" variable-class="java.lang.Object"%>
<dwf:resolveEL el="${entityName}" var="entity"/>
<c:set var="formaction" value="${attrMap.formaction}"/>
<c:if test="${empty attrMap.formaction}">
	<c:set var="formaction" value="${appPath}/${entityName}/save"/>
</c:if>

<c:if test="${empty attrMap.labelKey}">
	<spring:message code="label.editForm.header.${empty entity.id ? 'create' : 'edit'}" var="panelTitle"/>
	<spring:message code="domain.${entityName}" var="entityDisplayName"/>
	<c:set var="panelTitle" value="${panelTitle} ${entityDisplayName }"/>
</c:if>
<c:if test="${!empty attrMap.labelKey}">
	<spring:message code="${attrMap.labelKey}" var="panelTitle"/>
</c:if>
<c:set var="formLayout" value="horizontal" scope="request"/>
<h1>${panelTitle} </h1>
<div class="panel panel-default">
	<div class="panel-body">
		<form class="form-horizontal validate" method="POST" action="${formaction}" role="form">
			<input type="hidden" name="id" value="${entity.id}"/>
			<jsp:doBody />
			<div class="text-right">
				<button type="submit" class="btn btn-primary" data-loading-text="<spring:message code="action.wait"/>" formaction="${formaction}">
			 		<span class="glyphicon glyphicon-floppy-disk"></span>
			 		<spring:message code="action.save"/>
				</button>
			</div>
		</form>
	</div><!-- /.box-content -->
</div>
<c:set var="formLayout" value="" scope="request"/>