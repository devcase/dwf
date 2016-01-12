<%--
 
 --%><%@tag import="java.util.Map"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>
<c:set var="labelTrue" value="${!empty attrMap.labelTrue ? attrMap.labelTrue : 'Boolean.YES'}"/>
<c:set var="labelFalse" value="${!empty attrMap.labelFalse ? attrMap.labelFalse : 'Boolean.NO'}"/>
<dwf:formGroup parentAttrMap="${attrMap}" >
		<div class="btn-group " data-toggle="buttons">
			<label class="btn btn-default dwf-btn-toggle-true ${value ? 'active' : '' }"> 
				<input <c:if test="${attrMap.required}">required="required"</c:if>
					 type="radio" name="${name}" autocomplete="off" value="true" <c:if test="${value}">checked</c:if>/> <spring:message code="${labelTrue}" text="${labelTrue}"/>
			</label>
			<label class="btn btn-default dwf-btn-toggle-false ${value eq false ? 'active' : '' }">
				<input <c:if test="${attrMap.required}">required="required"</c:if>
					 type="radio" name="${name}" autocomplete="off" value="false" <c:if test="${!value eq false }">checked</c:if>/> <spring:message code="${labelFalse}" text="${labelFalse}"/>
			</label>
		</div>
</dwf:formGroup>
