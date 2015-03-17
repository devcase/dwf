<%@tag import="java.text.DecimalFormatSymbols"%>
<%@tag import="org.springframework.context.i18n.LocaleContextHolder"%>
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
<dwf:formGroup parentAttrMap="${attrMap}">
	<div class="row">
 		<div class="col-lg-3">
	 		<input type="text" value="<fmt:formatNumber value='${value.value}' pattern='#.00' />" name="${name}.value"
			decimalSeparator="${decimal}" groupingSeparator="${grouping}"
			<c:if test="${attrMap.required}">required="required"</c:if>
			class='form-control price-format <c:if test="${attrMap.required}">required</c:if>' />
		</div>
	 	<div class="col-lg-9">
		  	<select name="${name}.currencyCode"
				<c:if test="${attrMap.required}">required="required"</c:if> class="form-control">
				<option value=""><spring:message code="label.select.empty${attrMap.required ? '.required' : ''}"/></option>
				<c:forTokens items="BRL,USD,EUR"
					delims="," var="currencyCode">
					<option value="${currencyCode}"
						<c:if test="${currencyCode eq value.currencyCode}">selected</c:if>
						><spring:message code="currency.${currencyCode }" /></option>
				</c:forTokens>
			</select>
		</div>
	</div>
</dwf:formGroup>
