<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<dwf:resolveEL el="${entityName}" var="entity"/>

<html>
	<head>
		<meta name="decorator" content="crud"/>	
		<title><spring:message code="label.restoreForm.header" />: ${entity}</title>
	</head>
	<body>
		<dwf:restoreForm/>
	</body>
</html>
