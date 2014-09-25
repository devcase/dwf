<%@tag import="java.util.Map"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@ tag dynamic-attributes="attrMap"%>
<%
//Monta querystring com o filtro, removendo fetchSize e pageNumber
StringBuilder sb = new StringBuilder();
for(Map.Entry<String, String[]> parameter : request.getParameterMap().entrySet()) {
	if("fetchSize".equals(parameter.getKey())
		|| "pageNumber".equals(parameter.getKey())) {
	} else {
		for(String value : parameter.getValue()) {
			sb.append("&").append(parameter.getKey()).append("=").append(value);
		}
	}
}
jspContext.setAttribute("_querystring", sb.toString());
%>
<c:if test="${pageCount > 1}">
	<ul class="pagination">
		<li class="${pageNumber > 0 ? '' : 'disabled'}"><a href="${attrMap.contentHref}&fetchSize=${fetchSize}&pageNumber=${pageNumber - 1}${_querystring}" dwf-toggle="paginator" >&laquo;</a></li>
		
		<c:forEach begin="0" end="${pageCount - 1}" var="page">
			<c:choose>
			<c:when test="${pageNumber eq page }">
				<li class="active"><a href="#" class="prevent-default-click">${page + 1}</a></li>
			</c:when>
			<c:otherwise>
				<li class=""><a href="${attrMap.contentHref}&fetchSize=${fetchSize}&pageNumber=${page}${_querystring}" dwf-toggle="paginator" >${page + 1}</a></li>
			</c:otherwise>
			</c:choose>
	  		
		</c:forEach>
		
		<li class="${pageNumber < (pageCount - 1) ? '' : 'disabled'}"><a href="${attrMap.contentHref}&fetchSize=${fetchSize}&pageNumber=${pageNumber + 1}${_querystring}" dwf-toggle="paginator">&raquo;</a></li>
	</ul>
</c:if>