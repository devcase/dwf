<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<dwf:resolveEL el="${entityName}" var="entity"/>
<ol class="breadcrumb">
	<c:choose>
		<c:when test="${empty entity.id}"><%-- NOVO REGISTRO --%>
			  	<li><a href="${appPath}"><spring:message code="action.home" /></a></li>
			  	<li class="active"><spring:message code="domain.${entityName}.plural" /></li>
		</c:when>
		<c:otherwise><%-- EDITAR REGISTRO EXISTENTE --%>
			  	<li><a href="${appPath}"><spring:message code="action.home" /></a></li>
			  	<li><a href="${appPath}/${entityName}/"><spring:message code="domain.${entityName}.plural" /></a></li>
			  	<li class="active">${entity.name}</li>
		</c:otherwise>
	</c:choose>
</ol>