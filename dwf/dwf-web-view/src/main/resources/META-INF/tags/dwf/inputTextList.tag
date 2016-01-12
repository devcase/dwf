<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>
<dwf:formGroup parentAttrMap="${attrMap}">
	<div class="dwf-inputtextlist-container" 
		dwf-inputtextlist-placeholder="${attrMap.placeholder}" 
		dwf-inputtextlist-name="${name}[]" 
		dwf-inputtextlist-maxlength="${!empty attrMap.maxlength ? attrMap.maxlength : '2000'}"
		dwf-inputtextlist-class='form-control dwf-inputtextlist-textarea dwf-dont-send-empty'>
		<c:forEach items="${value}" var="item">
			<textarea 
				placeholder="${attrMap.placeholder}"
				name="${name}[]"
				maxlength="${!empty attrMap.maxlength ? attrMap.maxlength : '2000'}"
				class='form-control dwf-inputtextlist-textarea dwf-dont-send-empty'
				rows="3"
				><c:out value="${item}"/></textarea>
		</c:forEach>
		<%-- ITEM EM BRANCO --%>
		<textarea 
				placeholder="${attrMap.placeholder}"
				name="${name}[]"
				maxlength="${!empty attrMap.maxlength ? attrMap.maxlength : '2000'}"
				class='form-control dwf-inputtextlist-textarea dwf-dont-send-empty'
				rows="3"></textarea>
	</div>
</dwf:formGroup>
