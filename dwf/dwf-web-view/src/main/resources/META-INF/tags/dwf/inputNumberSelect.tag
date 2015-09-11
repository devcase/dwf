<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>
<dwf:formGroup parentAttrMap="${attrMap}">
	<select name="${name}"
		<c:if test="${attrMap.required}">required="required"</c:if>
		<c:if test="${!empty attrMap.minproperty}">minproperty="${attrMap.minproperty}"</c:if> 
		class="form-control" style="width: auto">
		<option value=""><spring:message code="label.select.empty${attrMap.required ? '.required' : ''}"/></option>
		<c:forEach begin="${!empty attrMap.min ? attrMap.min : 0}" end="${!empty attrMap.max ? attrMap.max : 100}" varStatus="loopStatus">
			<option value="${loopStatus.index}"
				<c:if test="${loopStatus.index eq value}">selected</c:if>
				>${loopStatus.index}
				<c:choose>
					<c:when test="${!empty attrMap.pluralunit and loopStatus.index ne 1}"> <spring:message code="${attrMap.pluralunit}" text="${attrMap.pluralunit}"/></c:when>
					<c:when test="${!empty attrMap.unit}"> <spring:message code="${attrMap.unit}" text="${attrMap.unit}"/></c:when>
				</c:choose>
				</option>
		</c:forEach>
	</select> 
</dwf:formGroup>