<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>
<dwf:formGroup parentAttrMap="${attrMap}">
	<input type="text" value="<dwf:autoFormat value='${value}'/>" name="${name}"
		<c:if test="${attrMap.required}">required="required"</c:if>
		class='form-control <c:if test="${attrMap.required}">required</c:if> date-picker' data-date-format="${datePatternDatePicker}"/>
</dwf:formGroup>
