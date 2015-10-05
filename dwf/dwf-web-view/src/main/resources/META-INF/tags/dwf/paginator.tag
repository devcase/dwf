<%@tag import="java.util.Map"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@ tag dynamic-attributes="attrMap"%>
<c:set var="queryStringPrefix" value="${attrMap.contentHref.contains('?') ? '&' : '?'  }"/>
<jsp:useBean id="queryStringBuilder" class="dwf.utils.QueryStringBuilder"/>
<c:set var="queryStringBuilder" value="${queryStringBuilder.fromRequest(pageContext.request).without('decorator', 'pageNumber')}"/>

<c:if test="${pageCount > 1}">
	<ul class="pagination">
		<li class="${pageNumber > 0 ? '' : 'disabled'}">
			<a href="${attrMap.contentHref}${queryStringBuilder.setting('pageNumber', (pageNumber - 1)).buildStartingWith(queryStringPrefix)}" dwf-toggle="paginator" >&laquo;</a>
		</li>
		
		<c:forEach begin="0" end="${pageCount - 1}" var="page">
			<c:choose>
			<c:when test="${pageNumber eq page }">
				<li class="active"><a href="${attrMap.contentHref}${queryStringBuilder.setting('pageNumber', page).buildStartingWith(queryStringPrefix)}" class="prevent-default-click">${page + 1}</a></li>
			</c:when>
			<c:otherwise>
				<li class=""><a href="${attrMap.contentHref}${queryStringBuilder.setting('pageNumber', page).buildStartingWith(queryStringPrefix)}" dwf-toggle="paginator" >${page + 1}</a></li>
			</c:otherwise>
			</c:choose>
	  		
		</c:forEach>
		
		<li class="${pageNumber < (pageCount - 1) ? '' : 'disabled'}">
			<a href="${attrMap.contentHref}${queryStringBuilder.setting('pageNumber', (pageNumber + 1)).buildStartingWith(queryStringPrefix)}" dwf-toggle="paginator" >&raquo;</a>
		</li>
	</ul>
</c:if>