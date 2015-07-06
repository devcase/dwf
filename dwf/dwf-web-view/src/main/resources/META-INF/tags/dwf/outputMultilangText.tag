<%@tag import="dwf.multilang.domain.BaseMultilangEntity"%>
<%@tag import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@tag import="dwf.multilang.TranslationManager"%>
<%@tag import="java.util.Calendar"%>
<%@tag import="java.util.Date"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>
<dwf:outputText parentAttrMap="${ attrMap}" >
	<c:forTokens items="pt-BR,en-US,es" delims="," var="localeCode">
		<c:if test="${localeCode ne entity.defaultLanguage }">
			<c:set var="translation" value="${translationManager.getTranslation(entity, attrMap.property, localeCode)}"/>
			<c:if test="${!empty translation}">
				<br/><strong><spring:message code="locale.${localeCode}"/>:</strong> <dwf:autoFormat value="${translation}"/>
			</c:if>
		</c:if>
	</c:forTokens>
	<jsp:doBody/>
</dwf:outputText>

