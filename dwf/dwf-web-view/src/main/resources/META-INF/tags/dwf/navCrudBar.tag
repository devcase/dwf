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
			<c:otherwise><%-- LINK TO OTHER OPERATION --%>
				<dwf:navCrudBarItem path="${appPath}/${entityName}/${navCrudItem.operation}/${navCrudItem.entity.id}"
					active="${navCrudItem.operation eq navCrud.activeOperation}" icon="${navCrudItem.icon}" 
					labelKey="${navCrudItem.labelKey}" badge="${navCrudItem.badge }"/>
			</c:otherwise>
		</c:choose>
	</c:forEach>
	<jsp:doBody/>
</div>
