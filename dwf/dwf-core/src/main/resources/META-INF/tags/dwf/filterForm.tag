<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@ tag dynamic-attributes="attrMap"%>
<fmt:setBundle basename="labels" var="labelsBundle" />
<form method="GET" action="${appPath}/${entityName}/list" role="form" class="form-horizontal">
	<jsp:doBody />
	<button type="submit" class="btn btn-primary" data-loading-text="<fmt:message key="action.wait" bundle="${labelsBundle}"/>">
		 <span class="glyphicon glyphicon-filter"></span>
		 <fmt:message key="action.filter" bundle="${labelsBundle}"/>
	</button>
</form>
