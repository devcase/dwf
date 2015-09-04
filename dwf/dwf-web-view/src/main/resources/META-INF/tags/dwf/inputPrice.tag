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
	<div class="input-group dwf-input-price">
		<c:choose>
			<c:when test="${!empty attrMap.currencyCode }">
				<%-- PRE-DEFINED CURRENCY CODE --%>
				<span class="input-group-addon"><spring:message code="currency.symbol.${attrMap.currencyCode }" /><input type="hidden" value="${attrMap.currencyCode}" name="${name }.currencyCode"/></span>
				<%-- / PRE-DEFINED CURRENCY CODE --%>
			</c:when>
			<c:otherwise>
				<%-- USER CHOOSES CURRENCY CODE --%>
				<div class="input-group-btn">
					<button type="button" class="btn btn-default dropdown-toggle dwf-input-price-dropdown-button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
						<span class="dwf-input-price-dropdown-button-text">
							<c:if test="${!empty value.currencyCode}"><spring:message code="currency.symbol.${value.currency}" /></c:if>
							<c:if test="${empty value.currencyCode}">-</c:if>
						</span> <span class="caret"></span>
					</button>
					<ul class="dropdown-menu">
						<c:forTokens items="BRL,USD,EUR" delims="," var="currency">
							<li><a class="dwf-input-price-dropdown-item"><label>
								<input type="radio" value="${currency}" name="${name}.currencyCode" ${ value.currencyCode eq currency ? 'checked' : ''} labelText="<spring:message code="currency.symbol.${currency}" />"/>
								<spring:message code="currency.symbol.${currency}" /></label>
							</a></li>
						</c:forTokens>
					</ul>
				</div>
				<%-- /USER CHOOSES CURRENCY CODE --%>
			</c:otherwise>
			
		</c:choose>
 		<input type="text" value="<fmt:formatNumber value='${value.value}' pattern='#.00' />" name="${name}.value"
			decimalSeparator="${decimal}" groupingSeparator="${grouping}"
			<c:if test="${attrMap.required}">required="required"</c:if>
			class='form-control price-format validate-number <c:if test="${attrMap.required}">required</c:if>' />
	</div>
</dwf:formGroup>	
