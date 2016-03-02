<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf" %>
<html>
	<head>
		<meta name="decorator" content="default"/>
	</head>
	<body>
		<dwf:navCrudBar/>
		<dwf:editForm>
			<dwf:inputText property="email" required="true"/>
			<dwf:inputText property="firstName" />
			<dwf:inputText property="lastName" />
			<dwf:inputTextCheckboxList property="roles" items="ROLE_SUPERUSER, ROLE_BACKOFFICE_ADMIN"/>
		</dwf:editForm>
	</body>
</html>
