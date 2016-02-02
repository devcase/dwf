<%--
 
 --%><%@tag import="java.util.Map"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>
<dwf:formGroup parentAttrMap="${attrMap}" withoutLabel="${attrMap.style eq 'left' ? true :  attrMap.withoutLabel }">
	<c:if test="${attrMap.style eq 'left' and !attrMap.withoutLabel }">
		<label>
	</c:if>
	<input type="checkbox" value="true" name="${name}"
		<c:if test="${attrMap.required}">required="required"</c:if>
		class="<c:if test="${attrMap.required}">required</c:if> "
		<c:if test="${value}">checked</c:if>/>
		<c:if test="${attrMap.style eq 'left' and !attrMap.withoutLabel }">
			${label}
		</c:if>
	<c:if test="${attrMap.style eq 'left' and !attrMap.withoutLabel }">
		</label>
	</c:if>
</dwf:formGroup>
