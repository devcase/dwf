<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>
<c:set var="availableValues" value="${attrMap.items}"/>
<%
if(jspContext.getAttribute("availableValues") instanceof String) {
	jspContext.setAttribute("availableValues", 
			((String) jspContext.getAttribute("availableValues")).split(","));
}
%>
<dwf:formGroup parentAttrMap="${attrMap}">
	<div class="row" ><c:forEach items="${availableValues}" var="option">
		<div class="col-12">
			<label class= "float-left btn btn-borderless ">
				<input type="checkbox" name="${name}[]" value="${option}" 
					${value.contains(option) ? 'checked="checked"' : '' }>${option }</input>
			</label>
		</div>
	</c:forEach>
	</div>	
</dwf:formGroup>