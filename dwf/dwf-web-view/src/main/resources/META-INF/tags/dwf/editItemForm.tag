<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@ tag dynamic-attributes="attrMap"%>
<%-- guarda o valor de ${entityName} do request em '' --%>
<c:set var="_previousEntityName" value="${entityName}" scope="page"/>
<%-- substitui o valor de entityName no request --%>
<c:set var="entityName" value="${attrMap.itemName}" scope="request"/>
<dwf:resolveEL el='${entityName}.id' var="entityId"/>
<form class="form-horizontal" method="POST" action="${attrMap.action}" role="form">
	<sec:csrfInput />
	<input type="hidden" name="id" value="${entityId}"/>
	<jsp:doBody />
	<div class="form-group">
		<div class="col-sm-offset-3 col-sm-10">
			<button type="submit" class="btn btn-primary" data-loading-text="<fmt:message key="action.wait" bundle="${labelsBundle}"/>">
				 <span class="glyphicon glyphicon-floppy-disk"></span>
				 <spring:message code="action.save" />
			</button>
  		</div>
	</div>
</form>
<%-- restaura o valor de entityName ao request --%>
<c:set var="entityName" value="${_previousEntityName}" scope="request"/>
