<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>
<dwf:formGroup parentAttrMap="${attrMap}">
	<input type="file" name="${name}"
		<c:if test="${attrMap.required}">required="required"</c:if>
		accept="${!empty attrMap.accept ? attrMap.accept : 'image/*' }"
		class='form-control <c:if test="${attrMap.required}">required</c:if>' />
</dwf:formGroup>
