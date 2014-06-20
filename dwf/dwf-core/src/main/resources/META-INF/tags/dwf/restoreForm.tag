<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>

<c:choose> <%--_formAction--%>
	<c:when test="${empty attrMap.formaction}">
		<c:set var="_formAction" value="${appPath}/${entityName}/restore/${entity.id}"/>
	</c:when>
	<c:otherwise>
		<c:set var="_formAction" value="${attrMap.formaction}"/>
	</c:otherwise>
</c:choose><%--/_formAction--%>

<c:choose> <%--_entityName--%>
	<c:when test="${empty attrMap.entityName}">
		<dwf:resolveEL el="${entityName}" var="entity"/>
	</c:when>
	<c:otherwise>
		<dwf:resolveEL el="${attrMap.entityName}" var="entity"/>
	</c:otherwise>
</c:choose><%--/_formAction--%>


<h1><spring:message code="label.restoreForm.header" /> <spring:message code="domain.${entityName}"/>: ${entity}</h1>
<div class="panel panel-default">
	<div class="panel-body">
		<form class="validate" method="POST" action="${_formAction}" role="form">
			<input type="hidden" name="id" value="${entity.id}"/>
			<dwf:inputTextarea name="comments" required="true" maxlength="1000" labelKey="label.comments.restore"/>
			<div class="form-group">
				<div class="text-right">
		   			<button type="submit" class="btn btn-primary" data-loading-text="<spring:message code="action.wait" />">
		   				<span class="glyphicon glyphicon-open"></span>
		   				<spring:message code="action.restore" />
		   			</button>
		  		</div>
			</div>
		</form>
	</div><!-- /.panel-body -->
</div>
