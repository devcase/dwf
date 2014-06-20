<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf" %>
<%@ tag dynamic-attributes="attrMap" %>
<fmt:setBundle basename="labels" var="labelsBundle"/>
<dwf:resolveEL el="${entityName}" var="entity"/>
<li class="${attrMap.active ? 'active' : ''}"><a href="${attrMap.path}/">${attrMap.label}
<c:if test="${!empty attrMap.badge}"><span class="badge">${attrMap.badge}</span>
</c:if>
</a></li>
		