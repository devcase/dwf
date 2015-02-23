<%@tag import="java.util.Calendar"%>
<%@tag import="java.util.Date"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>
<dwf:formGroup parentAttrMap="${attrMap}">
	<div class="form-control-static">
		<span class="${empty attrMap.styleClass ? '' :  attrMap.styleClass}">
		<c:choose>
		<c:when test="${empty attrMap.enableHtml || attrMap.enableHtml eq 'false' }">
			<dwf:autoFormat value="${value}"></dwf:autoFormat>${empty value ? '&nbsp;' : '' }
		<jsp:doBody></jsp:doBody>
		</c:when>
			<c:otherwise>${value}</c:otherwise>
		</c:choose></span>
	</div>
</dwf:formGroup>