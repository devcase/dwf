<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>
<dwf:formGroup parentAttrMap="${attrMap}">
	<select name="${name}.id"
		<c:if test="${attrMap.required}">required="required"</c:if> class="form-control">
		<c:forEach begin="${attrMap.min }" end="${attrMap.max}" varStatus="loopStatus">
			<option value="${loopStatus.index}"
				<c:if test="${loopStatus.index eq value}">selected</c:if>
				>${loopStatus.index}</option>
		</c:forEach>
	</select>
</dwf:formGroup>