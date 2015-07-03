<%@tag import="org.springframework.context.i18n.LocaleContextHolder"%>
<%@tag import="java.text.DecimalFormatSymbols"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>
<%
DecimalFormatSymbols symbols = new DecimalFormatSymbols(LocaleContextHolder.getLocale());
char decimal = symbols.getDecimalSeparator();
char grouping = symbols.getGroupingSeparator();

getJspContext().setAttribute("decimal", decimal);
getJspContext().setAttribute("grouping", grouping);
%>
<%-- Recupera o valor e o nome do request parameter para a unidade de tempo --%>
<c:choose>
	<c:when test="${!empty attrMap.unityProperty}">
		<c:set var="unitName" value="${attrMap.unitProperty}"/>
	</c:when>
	<c:otherwise>
		<c:set var="unitName" value="${attrMap.property}Unit"/>
	</c:otherwise>
</c:choose>
<dwf:resolveEL el="${entityName}.${unitName}" var="unitValue" />

<%-- --%>

<dwf:formGroup parentAttrMap="${attrMap}">
	<div class="row">
 		<div class="col-xs-6 col-sm-4 col-md-3">
	 		<input type="text" value="<fmt:formatNumber value='${value}' pattern='${empty value ? "": "0"}' />" name="${name}"
			decimalSeparator="${decimal}" groupingSeparator="${grouping}"
			<c:if test="${attrMap.required}">required="required"</c:if>
			class='form-control validate-digits <c:if test="${attrMap.required}">required</c:if>' />
		</div>
	 	<div class="col-xs-6 col-sm-3">
		  	<select name="${unitName}"
				<c:if test="${attrMap.required}">required="required"</c:if> class="form-control">
				<option value=""><spring:message code="label.select.empty${attrMap.required ? '.required' : ''}"/></option>
				<c:forTokens items="MINUTES,HOURS"
					delims="," var="timeUnit">
					<option value="${timeUnit}"
						<c:if test="${timeUnit eq unitValue}">selected</c:if>
						><spring:message code="dwf.persistence.enumeration.TimeUnit.${timeUnit }" /></option>
				</c:forTokens>
			</select>
		</div>
	</div>
</dwf:formGroup>
