<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>
<c:set var="formLayout" value="horizontal" scope="request"/>
<dwf:resolveEL el='${entityName}' var="entity"/>
<h1><i class="fa fa-edit"></i><spring:message code="domain.${entityName}"/>: ${entity.displayText}</h1>
<div class="panel panel-default">
	<div class="panel-body">
		<form class="form-horizontal" method="POST" action="${appPath}/${entityName}/edit/${entity.id}" role="form">
			<jsp:doBody />
			<dwf:outputText property="enabled" styleClass="label ${entity.enabled eq true ? 'label-success' : 'label-danger'}"></dwf:outputText>
		</form>
	</div>
</div>
<c:set var="formLayout" value="" scope="request"/>