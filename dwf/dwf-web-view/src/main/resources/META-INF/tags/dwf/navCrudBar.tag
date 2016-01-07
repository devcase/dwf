<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf" %>
<%@ tag dynamic-attributes="attrMap" %>
<dwf:resolveEL el="${entityName}" var="entity"/>

<div class="btn-group">
	<c:forEach items="${navCrud.items}" var="navCrudItem">
		<c:choose>
			<c:when test="${navCrudItem.hidden}">
			</c:when>
			<c:when test="${navCrudItem.operation eq navCrud.activeOperation}"> <%-- ACTIVE OPERATION --%>
				<a href="${appPath}/${entityName}/${navCrudItem.operation}/${navCrudItem.entity.id}" class="prevent-default-click active btn btn-app" >
				<c:if test="${!empty navCrudItem.icon }">
					<span class=" glyphicon glyphicon-${navCrudItem.icon }"></span>
				</c:if>
				<span class="hidden-sm">
 				<spring:message code="${navCrudItem.labelKey}" />
				</span>
				<c:if test="${!empty navCrudItem.badge}">
					<span class="badge">${navCrudItem.badge}</span>
				</c:if>
				</a>
			</c:when>
			<c:otherwise><%-- LINK TO OTHER OPERATION --%>
				<a class="btn btn-app " href="${appPath}/${entityName}/${navCrudItem.operation}/${navCrudItem.entity.id}" >
  					<c:if test="${!empty navCrudItem.icon }">
  						<span class=" glyphicon glyphicon-${navCrudItem.icon }"></span>
  					</c:if>
  					<span class="hidden-sm">
		  				<spring:message code="${navCrudItem.labelKey}" />
	  				</span>
	  				<c:if test="${!empty navCrudItem.badge}">
	  					<span class="badge">${navCrudItem.badge}</span>
	  				</c:if>
				</a>
			</c:otherwise>
		</c:choose>
	</c:forEach>
</div>

