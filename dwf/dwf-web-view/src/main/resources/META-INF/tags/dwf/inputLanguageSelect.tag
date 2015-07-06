<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>
<dwf:formGroup parentAttrMap="${attrMap}">
	<select name="${name}"
		<c:if test="${attrMap.required}">required="required"</c:if> class="form-control">
		<option value=""><spring:message code="label.select.empty${attrMap.required ? '.required' : ''}"/></option>
		<c:forTokens items="pt-BR,en-US,es"
			delims="," var="localeCode">
			<option value="${localeCode}"
				<c:if test="${localeCode eq value}">selected</c:if>
				><spring:message code="locale.${localeCode}"/></option>
		</c:forTokens>
	</select>
</dwf:formGroup>