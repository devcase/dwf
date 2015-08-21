<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf" %>
<html>
	<head>
		<meta name="decorator" content="crud"/>
	</head>
	<body>
		<dwf:editForm>
			<dwf:inputText property="email" required="true"/>
			<dwf:inputDate property="expirationDate"/>
			<dwf:inputBooleanCheckbox property="verified"/>
			<dwf:inputTextCheckboxList property="roles" items="ROLE_BACKOFFICE_ADMIN,ROLE_BACKOFFICE_USER,ROLE_TU_INSIDER,ROLE_SUPERUSER"/>
		</dwf:editForm>
	</body>
</html>
