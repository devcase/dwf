<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap" %>
<c:choose>
<c:when test="${attrMap.value}"><spring:message code="Boolean.YES" /></c:when>
<c:otherwise><spring:message code="Boolean.NO" /></c:otherwise>
</c:choose>