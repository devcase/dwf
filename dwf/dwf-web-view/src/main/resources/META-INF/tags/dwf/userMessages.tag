<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ tag dynamic-attributes="attrMap" %>
<c:set var="bindingResults" value="${requestScope['org.springframework.validation.BindingResult.form']}"/>

<c:if test="${!empty userMessagesList}">
	<c:forEach items="${userMessagesList}" var="userMessage">
		<div class="alert alert-${userMessage.type.lowerCase} alert-dismissable fade in">
		
		<spring:message code="${userMessage.key}" arguments="${userMessage.arguments }"/></div>
	</c:forEach>
</c:if>

<c:if test="${loginErrorMessage != null}">
	<div class="alert alert-danger" role="alert alert-dismissable fade in">
		<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
		${loginErrorMessage}
	</div>
</c:if>

<c:if test="${!empty userException }">
	<div class="alert alert-danger" role="alert alert-dismissable fade in">
		<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
		${userException}
	</div>
</c:if>


<c:if test="${!empty validationException || bindingResults.errorCount > 0}">
	<div class="alert alert-danger alert-dismissable  fade in">
		<button type="button" class="close" data-dismiss="alert" aria-hidden="true">&times;</button>
		<c:choose>
			<c:when test="${empty validationException}"><%-- bindingResults --%>
				<spring:message code="validationexception.message" />
				<ul>
					<c:forEach items="${bindingResults.allErrors}" var="objectError" varStatus="loopStatus">
						<li>
							<dwf:escapeHtml value="${objectError.defaultMessage}"/>
						</li>
					</c:forEach>
				</ul>
			</c:when>
			<c:when test="${validationException.getClass().name eq 'javax.validation.ConstraintViolationException' }">
				<spring:message code="validationexception.message" />
				<ul>
					<c:forEach items="${validationException.constraintViolations}" var="violation" varStatus="loopStatus">
						<li>
							<dwf:violationNodePath constraintViolation="${violation}" var="nodePath"></dwf:violationNodePath>
							<strong><dwf:simpleLabel property="${nodePath}" /></strong>:
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
