<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<dwf:resolveEL el="${entityName}" var="entity" />
<html>
<head>
<meta name="decorator" content="default"/>
<title>${category}</title>
</head>
<body>
	<dwf:navCrudBar/>
	<dwf:viewPanel>
		<dwf:outputText property="name" >
			<br/><a href="#" data-toggle="modal" data-target="#translateName" class="btn btn-default btn-xs"><i class="fa fa-language"></i> traduzir</a>
		</dwf:outputText>
 		<dwf:outputText property="description"/>
 		<dwf:outputText property="adminOnly"/>
 	</dwf:viewPanel>
 	
 	<%-- TRANSLATE NAME POPUP --%>
	<dwf:simpleLabel property="name" var="nameLabel"/>
	<spring:message code="label.translateForm.title" arguments="${nameLabel}" var="panelTitle"/>
	<dwf:modal title="${panelTitle}" id="translateName">
		<dwf:translateForm property="name" panelless="true" closemodalbutton="true"/>
	</dwf:modal>
</body>
</html>
