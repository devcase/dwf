<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<dwf:resolveEL el="${entityName}" var="entity" />
<html>
<head>
<meta name="decorator" content="default" />
<title>${category}</title>
</head>
<body>
	<dwf:navCrudBar/>
	<dwf:viewPanel>
 		<dwf:outputText property="content"/>
 		<dwf:outputText property="baseUser"/>
 	</dwf:viewPanel>
 	
</body>
</html>
