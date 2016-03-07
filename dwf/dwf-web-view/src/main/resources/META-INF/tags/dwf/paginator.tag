<%@tag import="java.util.Map"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@ tag dynamic-attributes="attrMap"%>
<c:set var="queryStringPrefix" value="${attrMap.contentHref.contains('?') ? '&' : '?'  }"/>
<jsp:useBean id="queryStringBuilder" class="dwf.utils.QueryStringBuilder"/>
<c:set var="queryStringBuilder" value="${queryStringBuilder.fromRequest(pageContext.request).without('decorator', 'pageNumber')}"/>
<c:set var="dwftarget" value=""/>
<c:if test="${!empty attrMap.target }">
	<c:set var="dwftarget" value="dwf-target='${attrMap.target}'"/>
</c:if>

<c:set var="firstpage" value="${pageNumber - 4}"/>
<c:set var="lastpage" value="${pageNumber + 5}"/>
<c:if test="${lastpage < 9}">
<c:set var="lastpage" value="9"/>
</c:if>
<c:if test="${lastpage > (pageCount - 1)}">
	<c:set var="lastpage" value="${pageCount - 1}"/>	
</c:if>
<c:if test="${lastpage - firstpage < 9}">
	<c:set var="firstpage" value="${lastpage - 9}"/>
</c:if> 
<c:if test="${firstpage < 0}">
	<c:set var="firstpage" value="0"/>
</c:if>


<c:if test="${pageCount > 1}">
	<ul class="pagination">
		<li class="${pageNumber > 0 ? '' : 'disabled'}">
			<a href="${attrMap.contentHref}${queryStringBuilder.setting('pageNumber', (pageNumber - 1)).buildStartingWith(queryStringPrefix)}" dwf-toggle="paginator" ${dwftarget}>&larr;</a>
		</li>

		<c:if test="${firstpage > 0}">
			<li class=""><a href="${attrMap.contentHref}${queryStringBuilder.setting('pageNumber', 0).buildStartingWith(queryStringPrefix)}" dwf-toggle="paginator" ${dwftarget}>1</a></li>
		</c:if>
		<c:if test="${firstpage > 1}">
			<li class=""><a href="${attrMap.contentHref}${queryStringBuilder.setting('pageNumber', firstpage).buildStartingWith(queryStringPrefix)}" dwf-toggle="paginator" ${dwftarget}>...</a></li>
		</c:if>		
		<c:forEach begin="${firstpage}" end="${lastpage}" var="page">
			<c:choose>
				<c:when test="${pageNumber eq page }">
					<li class="active"><a href="${attrMap.contentHref}${queryStringBuilder.setting('pageNumber', page).buildStartingWith(queryStringPrefix)}" class="prevent-default-click">${page + 1}</a></li>
				</c:when>
				<c:otherwise>
					<li class=""><a href="${attrMap.contentHref}${queryStringBuilder.setting('pageNumber', page).buildStartingWith(queryStringPrefix)}" dwf-toggle="paginator" ${dwftarget}>${page + 1}</a></li>
				</c:otherwise>
			</c:choose>
		</c:forEach>
		<c:if test="${lastpage < (pageCount - 2)}">
			<li class=""><a href="${attrMap.contentHref}${queryStringBuilder.setting('pageNumber', lastpage).buildStartingWith(queryStringPrefix)}" dwf-toggle="paginator" ${dwftarget}>...</a></li>
		</c:if>
		<c:if test="${lastpage < (pageCount - 1)}">
			<li class=""><a href="${attrMap.contentHref}${queryStringBuilder.setting('pageNumber', (pageCount - 1)).buildStartingWith(queryStringPrefix)}" dwf-toggle="paginator" ${dwftarget}>${pageCount}</a></li>
		</c:if>		
		
		<li class="${pageNumber < (pageCount - 1) ? '' : 'disabled'}">
			<a href="${attrMap.contentHref}${queryStringBuilder.setting('pageNumber', (pageNumber + 1)).buildStartingWith(queryStringPrefix)}" dwf-toggle="paginator" ${dwftarget}>&rarr;</a>
		</li>
	</ul>
</c:if>