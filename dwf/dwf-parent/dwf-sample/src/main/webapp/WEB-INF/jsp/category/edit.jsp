<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<html>
<head>
<meta name="decorator" content="${!empty param.decorator ? param.decorator : 'crud' }" />
</head>
<body>
	<dwf:editForm>
		<dwf:inputText property="name" required="true" />
		<dwf:inputText property="description" required="true" />
	</dwf:editForm>
</body>
</html>