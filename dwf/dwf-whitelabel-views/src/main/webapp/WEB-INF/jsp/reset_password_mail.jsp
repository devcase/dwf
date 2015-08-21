<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<html>
<body>
	<p>Você solicitou a troca do e-mail</p>
	<p>Por favor, clique no seguinte link: <a href="${environment.getProperty('travenup.resetpassword.host')}resetPassword/${passwordToken}"/>${environment.getProperty('travenup.resetpassword.host')}resetPassword/${passwordToken}</a></p>
</body>
</html>
