<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<html>
<head>
<meta name="decorator" content="${!empty param.decorator ? param.decorator : 'crud' }" />
</head>
<body>
	<div class="panel panel-default">
		<table class="table table-striped ">
			<thead>
				<tr>
					<th><spring:message code="domain.${entityName}"  /></th>
					<th><spring:message code="label.enabled"  /></th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${list}" var="entity">
					<tr>
						<td><a href="${appPath}/${entityName}/${entity.id}">${entity}</a></td>
						<td><span class="label ${entity.enabled ? 'label-success' : 'label-danger'}"><dwf:yesNo value="${entity.enabled}" /></span></td>
					</tr>
				</c:forEach>
			</tbody>
			<c:if test="${pageCount > 1}">
				<tfoot>
					<tr>
						<td colspan="2">
							<dwf:paginator contentHref="${appPath}/${entityName}/?decorator=table" fetchSize="${fetchSize}" pageNumber="${pageNumber}"/>
						</td>
					</tr>
				</tfoot>
			</c:if>
		</table>
	</div>
</body>
</html>
