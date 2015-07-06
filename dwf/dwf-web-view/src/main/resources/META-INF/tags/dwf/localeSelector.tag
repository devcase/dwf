<%@tag import="java.util.Locale"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>
<dwf:locale var="userlocalename" format="underscore"/>
<c:set var="localeList" value="${!empty attrMap.localeList ? attrMap.localeList : 'pt-BR,en-US'}"/>
<form class="dwf-language-selector-form">
<select class="language-selector form-control" name="locale">
	<c:forTokens items="${localeList}" delims="," var="localename">
		<option value="${localename}" ${userlocalename eq localename ? 'selected' : ''}><spring:message code="locale.${localename}"/></option>
	</c:forTokens>
</select>
</form>