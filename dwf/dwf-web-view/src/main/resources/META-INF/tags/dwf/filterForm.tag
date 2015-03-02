<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ tag dynamic-attributes="attrMap"%>
<fmt:setBundle basename="labels" var="labelsBundle" />
<form method="GET" action="${appPath}/${entityName}/list" role="form" class="form-horizontal">
	<sec:csrfInput />
	<jsp:doBody />
	<button type="submit" class="btn btn-primary" data-loading-text="<fmt:message key="action.wait" bundle="${labelsBundle}"/>">
		 <span class="glyphicon glyphicon-filter"></span>
		 <fmt:message key="action.filter" bundle="${labelsBundle}"/>
	</button>
</form>
