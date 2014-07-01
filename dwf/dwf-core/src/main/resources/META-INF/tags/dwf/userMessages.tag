<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap" %>

<c:if test="${!empty userMessagesList}">
	<c:forEach items="${userMessagesList}" var="userMessage">
		<div class="alert alert-${userMessage.type.lowerCase} alert-dismissable fade in">
		<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
		<spring:message code="${userMessage.key}" /></div>
	</c:forEach>
</c:if>

<c:if test="${!empty validationException}">
	<div class="alert alert-danger alert-dismissable  fade in">
		<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
		<c:choose>
			<c:when test="${validationException.getClass().name eq 'javax.validation.ConstraintViolationException' }">
				<spring:message code="validationexception.message" />
				<ul>
					<c:forEach items="${validationException.constraintViolations}" var="violation" varStatus="loopStatus">
						<li>
							<dwf:violationNodePath constraintViolation="${violation}" var="nodePath"></dwf:violationNodePath>
							<strong><spring:message code="${entityName}.${nodePath}" /></strong>:
							<dwf:escapeHtml value="${violation.message }"/>
						</li>
					</c:forEach>
				</ul>
			</c:when>
			<c:otherwise>
				${validationException.message }
			</c:otherwise>
		</c:choose>
		
	</div>
	
</c:if>
