<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>
<dwf:formGroup parentAttrMap="${attrMap}">
	<div class="row">
 		<div class="col-lg-3">
 		<select name="${name}.currencyCode"
			<c:if test="${attrMap.required}">required="required"</c:if> class="form-control">
			<option value=""><spring:message code="label.select.empty${attrMap.required ? '.required' : ''}"/></option>
			<c:forTokens items="BRL,USD,EUR"
				delims="," var="currencyCode">
				<option value="${currencyCode}"
					<c:if test="${currencyCode eq value.currencyCode}">selected</c:if>
					>${currencyCode }</option>
			</c:forTokens>
		</select>
		</div> <div class="col-lg-9">
	  <input type="text" value="<dwf:autoFormat value='${value.value}'/>" name="${name}.value"
		pattern="#.00"
		<c:if test="${attrMap.required}">required="required"</c:if>
		class='form-control <c:if test="${attrMap.required}">required</c:if>' />
	</div>
	</div>
</dwf:formGroup>
