<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<html>
<head>
<meta name="decorator" content="default" />
<title><spring:message code="label.password.change"/></title>
</head>
<body>
	<dwf:editForm formaction="${appPath}/changePassword" labelKey="label.password.change">
		<dwf:inputPassword name="currentPassword" required="true" />
		<dwf:inputPassword name="newPassword" required="true" minlength="8"/>
		<dwf:inputPassword name="newPasswordConfirmation" required="true" minlength="8"/>
	</dwf:editForm>
</body>
</html>
