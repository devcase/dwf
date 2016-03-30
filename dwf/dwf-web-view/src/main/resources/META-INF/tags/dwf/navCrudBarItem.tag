<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf" %>
<%@ tag dynamic-attributes="attrMap" %>
<a href="${attrMap.path}"
	class="${attrMap.active ? 'prevent-default-click active ' : ''}btn btn-app"> <c:if test="${!empty attrMap.icon }">
		<span class=" ${attrMap.icon }"></span>
	</c:if> <span class="hidden-sm"> <spring:message code="${attrMap.labelKey}" />
</span> <c:if test="${!empty attrMap.badge}">
		<span class="badge">${attrMap.badge}</span>
	</c:if>
</a>
