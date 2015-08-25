<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<dwf:resolveEL el="${entityName}" var="entity"/>

<html>
	<head>
		<meta name="decorator" content="crud"/>	
		<title><spring:message code="label.download.header" /></title>
	</head>
	<body>
		<h1>Exportar Arquivo</h1>	
			<a href="${appPath}/${entityName}/export" class="btn btn-primary">Download</a>
	</body>
</html>
