<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<dwf:resolveEL el="${entityName}" var="entity" />
<html>
<head>
<meta name="decorator" content="crud" />
<title>${service}</title>
</head>
<body>
	<dwf:viewPanel>
  		<dwf:outputText property="email"/>
  		<dwf:outputText property="expirationDate"/>
  		<dwf:outputText property="verified"/>
  		<dwf:outputText property="roles"/>
 	</dwf:viewPanel>
 	
 	<dwf:editForm formaction="${appPath}/baseUser/changePassword" labelKey="action.changepassword">
 		<dwf:inputPassword name="password" required="true"/>
 		<dwf:inputPassword name="confirmPassword" required="true"/>
 	</dwf:editForm>
</body>
</html>
