<%@tag import="java.util.ArrayList"%>
<%@tag import="java.util.Calendar"%>
<%@tag import="java.util.Date"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>
<dwf:mergeMaps map1="${attrMap}" map2="${attrMap.parentAttrMap}" var="attrMap"/>
<dwf:formGroup parentAttrMap="${attrMap}">
	<div class="form-control-static">
		<span class="${empty attrMap.styleClass ? '' :  attrMap.styleClass}">
			<c:choose>
				<c:when test="${value.getClass().isArray()}"><%-- VALOR É ARRAY --%>
					<c:forEach items="${value}" var="itemlista" varStatus="varStatusVar">
						<c:choose>
							<c:when test="${empty attrMap.enableHtml || attrMap.enableHtml eq 'false' }">
								<dwf:autoFormat value="${itemlista}${not varStatusVar.last? ',':''}"></dwf:autoFormat>${empty itemlista ? '&nbsp;' : '' }
							</c:when>
							<c:otherwise>${itemlista}${not varStatusVar.last? '&#44;':''}${empty itemlista ? '&nbsp;' : '' }</c:otherwise>
						</c:choose>
					</c:forEach>
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${empty attrMap.enableHtml || attrMap.enableHtml eq 'false' }">
							<dwf:autoFormat value="${value}"></dwf:autoFormat>
						</c:when>
						<c:otherwise>${value}${empty value ? '&nbsp;' : '' }</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>
			<jsp:doBody></jsp:doBody>
		</span>
	</div>
</dwf:formGroup>