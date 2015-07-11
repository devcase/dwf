<%@tag import="java.util.Map"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ attribute name="columns" required="true" type="java.lang.String"%>
<%@ attribute name="var" required="true" rtexprvalue="false"%>
<%
	//Monta querystring com o filtro, removendo fetchSize e pageNumber
	StringBuilder sb = new StringBuilder();
	for (Map.Entry<String, String[]> parameter : request.getParameterMap().entrySet()) {
		if ("fetchSize".equals(parameter.getKey()) || "pageNumber".equals(parameter.getKey()) || "decorator".equals(parameter.getKey())) {
		} else {
			for (String value : parameter.getValue()) {
				sb.append("&").append(parameter.getKey()).append("=").append(value);
			}
		}
	}
	jspContext.setAttribute("_querystring", sb.toString());
%>

<div class="panel panel-default">
	<div class="panel-body">
		<div class="row">
			<div class="col-sm-8">
				<form class="form-inline" action="${appPath}/${entityName}/">
					<div class="form-group">
						<div class="input-group">
							<div class="input-group-addon">
								<span class="glyphicon glyphicon-search" aria-hidden="true"></span>
							</div>
							<input type="text" class="form-control" name="searchstring" placeholder="Pesquisar" value="${param.searchstring}">
						</div>
					</div>
				</form>
			</div>
			<div class="col-sm-4 text-right">
				Itens por página:
				<c:forTokens items="10,20,50,100" delims="," var="size" varStatus="loopResults">
					<c:if test="${loopResults.count > 1}">|</c:if>
					<c:choose>
						<c:when test="${fetchSize eq size }">
							<strong>${size}</strong>
						</c:when>
						<c:otherwise>
							<a href="${appPath}/${entityName}/?fetchSize=${size}${_querystring}">${size}</a>
						</c:otherwise>
					</c:choose>

				</c:forTokens>
			</div>
		</div>

	</div>
	<table class="table table-striped ">
		<thead>
			<tr>
				<c:set var="columnCount" value="${0}" />
				<c:forTokens items="${columns}" delims="," var="column">
					<c:set var="columnCount" value="${columnCount +1}" />
					<th><dwf:simpleLabel property="${column}" /></th>
				</c:forTokens>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${list}" var="entity">
				<%@ variable alias="item" name-from-attribute="var" scope="NESTED"%>
				<c:set var="item" value="${entity}" />
				<jsp:doBody var="bodyText" />
				<c:choose>
					<c:when test="${not empty bodyText}">
				    	${bodyText}
				  	</c:when>
					<c:otherwise><tr>
						<c:forTokens items="${columns}" delims="," var="column" varStatus="rowStatus">
							<td>
								<c:choose>
									<c:when test="${rowStatus.count eq 1}"><a href="${appPath}/${entityName}/${item.id}"><dwf:autoFormat value="${entity[column]}"/></a></c:when>
									<c:when test="${column eq 'enabled'}">
										<span class="label ${entity.enabled ? 'label-success' : 'label-danger'}"><dwf:yesNo value="${entity.enabled}" /></span>
									</c:when>
									<c:otherwise><dwf:autoFormat value="${entity[column]}"/></c:otherwise>
								</c:choose>
							</td>
						</c:forTokens></tr>
					</c:otherwise>
				</c:choose>
			</c:forEach>
		</tbody>
		<c:if test="${pageCount > 1}">
			<tfoot>
				<tr>
					<td colspan="${columnCount}" class="text-center"><dwf:paginator
							contentHref="${appPath}/${entityName}/?decorator=table" fetchSize="${fetchSize}" pageNumber="${pageNumber}" /></td>
				</tr>
			</tfoot>
		</c:if>
	</table>
</div>