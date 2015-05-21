<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<fmt:setBundle basename="labels" var="labelsBundle" />
<html>
<head>
	<meta name="decorator" content="crud" />
	<title><dwf:resolveEL el="${entityName}" var="name"></dwf:resolveEL> ${name}</title>
</head>
<body>
	<h1><spring:message code="label.log"/></h1>
	<div class="panel panel-default">
		<div class="panel-body">
			<c:forEach items="${logList}" var="activityLog">
				<small>
					[<fmt:formatDate value="${activityLog.timestamp}" type="both"/>]
					<c:choose>
						<c:when test="${!empty activityLog.user}">
							${activityLog.user}
						</c:when>
						<c:otherwise>
							<spring:message code="label.anonymous" />
						</c:otherwise>
					</c:choose>
					<spring:message code="log.operation.${activityLog.operation}" /> 
					<a href="${appPath}/${activityLog.entityName}/${activityLog.entityId}"><spring:message code="domain.${activityLog.entityName}" /> ${activityLog.entityDescription}</a>
				</small> 
				<blockquote>
					<dwf:escapeHtml value="${activityLog.comments}"></dwf:escapeHtml>
					<c:if test="${fn:length(activityLog.updatedProperties) > 0}">
						<ul>
							<c:forEach items="${activityLog.updatedProperties}" var="updatedProperty">
								<li><dwf:simpleLabel simple="true" property="${updatedProperty.propertyName}"/><c:if test="${!updatedProperty.hiddenValues}">: de <dwf:autoFormat value="${updatedProperty.oldValue}"></dwf:autoFormat>
								para <dwf:autoFormat value="${updatedProperty.newValue}"></dwf:autoFormat></c:if>
								</li>
							</c:forEach>
						</ul>
					</c:if>
				</blockquote>
			</c:forEach>
					
		</div>
	</div>
</body>
</html>
