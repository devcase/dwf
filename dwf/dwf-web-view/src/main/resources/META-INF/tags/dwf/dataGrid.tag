<%@tag import="java.util.Arrays"%>
<%@tag import="java.util.Map"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ attribute name="columns" required="true" type="java.lang.String"%>
<%@ attribute name="ordercolumns" required="false" type="java.lang.String"%>
<%@ attribute name="var" required="true" rtexprvalue="false"%>
<%@ attribute name="ommitsearchbox" required="false" rtexprvalue="true"%>
<c:set var="ordercolumns" value="${empty ordercolumns ? columns : ordercolumns}"/>
<%
	//monta lista com colunas para ordenar
	String[] ordercolumnsarray = ((String) jspContext.getAttribute("ordercolumns")).split("[,| ,|, ]");
	java.util.List<String> ordercolumnslist = Arrays.asList(ordercolumnsarray);
	jspContext.setAttribute("ordercolumns", ordercolumnslist);
%>
<jsp:useBean id="queryStringBuilder" class="dwf.utils.QueryStringBuilder"/>
<c:set var="queryStringBuilder" value="${queryStringBuilder.fromRequest(pageContext.request)}"/>

<div class="panel panel-default">
	<div class="panel-body">
		<div class="row">
			<div class="col-sm-7">
				<c:if test="${ommitsearchbox ne true}">
					<form class="form-inline" action="${appPath}/${entityName}/">
						<div class="form-group">
							<div class="input-group">
								<div class="input-group-addon">
									<span class="glyphicon glyphicon-search" aria-hidden="true"></span>
								</div>
								<input type="text" class="form-control" name="searchstring" placeholder="Pesquisar" value="${param.searchstring}">
							</div><!-- /.input-group -->
						</div>
					</form>
				</c:if>
			</div>
			<div class="col-sm-5 text-right">
				${count} resultado${count > 1 ? 's' : '' } encontrado${count > 1 ? 's' : '' } |
				Itens por página:
				<c:forTokens items="10,20,50,100" delims="," var="size" varStatus="loopResults">
					<c:if test="${loopResults.count > 1}">|</c:if>
					<c:choose>
						<c:when test="${fetchSize eq size }">
							<strong>${size}</strong>
						</c:when>
						<c:otherwise>
							<a href="${appPath}/${entityName}/${queryStringBuilder.without('fetchSize', 'decorator', 'pageNumber').setting('fetchSize', size).buildStartingWith('?')}">${size}</a>
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
					<th>
						<dwf:simpleLabel property="${column}" var="columnName" labelKey="${column }"/>
						<c:choose>
							<c:when test="${!ordercolumns.contains(column)}">
								<%-- NO ORDER BY --%>
								${columnName}
							</c:when>
							<c:when test="${param['orderBy'] ne column }">
								<%-- ORDER BY - NOT CURRENT--%>
								<a href="${appPath}/${entityName}/${queryStringBuilder.without('orderBy', 'orderByDirection', 'decorator').setting('orderBy', column, 'orderByDirection', 'ASC').buildStartingWith('?')}" class="orderby">
									${columnName}
								</a>
							</c:when>
							<c:when test="${param['orderByDirection'] eq 'DESC'}">
								<%-- ORDER BY - CURRENT DESC--%>
								<a href="${appPath}/${entityName}/${queryStringBuilder.setting('orderByDirection', 'ASC').buildStartingWith('?')}" class="orderby">
									${columnName}
									<span class="glyphicon glyphicon-chevron-down" aria-hidden="true"></span>
								</a>
							</c:when>
							<c:otherwise>
								<%-- ORDER BY - CURRENT ASC --%>
								<a href="${appPath}/${entityName}/${queryStringBuilder.setting('orderByDirection', 'DESC').buildStartingWith('?')}" class="orderby">
									${columnName}
									<span class="glyphicon glyphicon-chevron-up" aria-hidden="true"></span>
								</a>
							</c:otherwise>
						</c:choose>
					</th>
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
							<td class="dwf-ellipsis" style="max-width: 300px">
								<c:choose>
									<c:when test="${rowStatus.count eq 1}"><a href="${appPath}/${entityName}/${item.id}"><dwf:autoFormat value="${entity[column]}"/></a></c:when>
									<c:when test="${column eq 'enabled'}">
										<span class="label ${entity.enabled ? 'label-success' : 'label-danger'}"><dwf:yesNo value="${entity.enabled}" /></span>
									</c:when>
									<c:otherwise><dwf:resolveEL var="property" el="item.${column}"/><dwf:autoFormat value="${property}"/></c:otherwise>
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
					<td colspan="${columnCount}" class="text-center"><dwf:paginator contentHref="${appPath}/${entityName}/?decorator=table" fetchSize="${fetchSize}" pageNumber="${pageNumber}" /></td>
				</tr>
			</tfoot>
		</c:if>
	</table>
</div>