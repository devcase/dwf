<!DOCTYPE html>

<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<html>
<head>
<meta name="decorator" content="default" />
<title>Erro ${status}</title>
</head>
<body>
	<h1>Erro ${status}</h1>
	<div class="alert alert-danger alert-dismissable"><spring:message code="error.http.${status}" text="error.http.${status}"/>
	</div>
</body>
</html>
