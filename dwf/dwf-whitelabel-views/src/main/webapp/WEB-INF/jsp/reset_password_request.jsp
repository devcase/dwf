<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="sec"%>
<html>
<head>
<meta name="decorator" content="default" />
<title>Lost password</title>
</head>
<body>
	<dwf:editForm panelStyle="max-width: 600px;" titleType="panel-heading" 
		formaction="${appPath}/resetPassword" buttonLabelKey="action.reset.password.request" labelKey="action.reset.password">
		<dwf:inputText required="true" name="email" />
		<dwf:reCaptcha/>
	</dwf:editForm>
</body>
</html>
