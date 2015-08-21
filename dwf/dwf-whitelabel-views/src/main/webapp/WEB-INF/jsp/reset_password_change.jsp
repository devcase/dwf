<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<html>
<head>
<meta name="decorator" content="default" />
<title>Reset Password</title>
</head>
<body>
		<dwf:editForm formaction="${appPath}/resetPassword/${token}" 
			buttonLabelKey="action.changepassword"  
			labelKey="label.password.change"
			panelStyle="max-width: 600px;" titleType="panel-heading">
			<dwf:inputPassword required="true" minlength="6" name="newPassword"/>
			<dwf:inputPassword required="true" minlength="6" name="newPasswordConfirmation"/>
		</dwf:editForm>
</body>
</html>
