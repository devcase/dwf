<%--
Atributos:

  * value:			
  * property:			
  * ignoreParams:			
  * labelStyleClass
  * withoutLabel:	para não mostrar nenhum label
  * label:			'none' para não mostrar nenhum label
  * formlayout: vertical/horizontal

 --%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>
<%@ variable name-given="name" scope="NESTED" %>
<%@ variable name-given="value" scope="NESTED" variable-class="java.lang.Object"%>
<%@ variable name-given="label" scope="NESTED" variable-class="java.lang.String"%>
<dwf:mergeMaps map1="${attrMap}" map2="${attrMap.parentAttrMap}" var="attrMap"/>
<dwf:simpleLabel parentAttrMap="${attrMap}" var="label"/>
<%-- NOME DO PARÂMETRO DO REQUEST E VALOR PREENCHIDO --%>
<c:choose>
	<c:when test="${!empty attrMap.name}">
		<dwf:resolveEL el="${attrMap.name}" var="value" />
		<c:set var="name" value="${attrMap.name}"/>		
	</c:when>
	<c:when test="${!empty attrMap.property}"><%-- PROPERTY --%>
		<dwf:resolveEL el="${entityName}.${attrMap.property}" var="value" />
		<c:set var="name" value="${attrMap.property}"/>
	</c:when>
</c:choose>
<c:if test="${!attrMap.ignoreParams and !empty pageContext.request.getParameterValues(name.concat('[]')) }">
	<c:set var="value" value="${pageContext.request.getParameterValues(name.concat('[]')) }"/>
</c:if>
<c:if test="${!attrMap.ignoreParams and !empty param[name]}"><%-- usa o parâmetro do request no lugar da propriedade--%>
	<c:set var="value" value="${param[name]}"/>
</c:if>
<c:if test="${!empty attrMap.value}">
	<c:set var="value" value="${attrMap.value}"/>
</c:if>
<dwf:findViolation path="${name}" var="violation"/>
<%-- Search for BindingResults, when using @Valid annotations --%>
<c:set var="bindingErrors" value="${requestScope['org.springframework.validation.BindingResult.form'].getFieldErrors(name)}"/>
<%-- label-width vs control-width --%>
<c:set var="labelWidth" value="${!empty attrMap.labelWidth ? attrMap.labelWidth : !empty parentFormAttrMap.labelWidth ? parentFormAttrMap.labelWidth : 4}"/>
<c:set var="controlWidth" value="${12 - labelWidth}"/>
<%-- Estilo do label --%>
<c:set var="labelStyleClasses" value="col-sm-${labelWidth} control-label ${empty attrMap.labelStyleClass ? '' :  attrMap.labelStyleClass}"/>
<c:if test="${labelWidth eq 0}">
	<c:set var="labelStyleClasses" value="sr-only"/>
</c:if>
<%-- Estilo do controle --%>
<c:set var="controlStyleClasses" value="col-sm-${controlWidth} form-group-content "/>
<c:if test="${attrMap.withoutLabel || label eq 'none'}">
	<c:set var="controlStyleClasses" value="${controlStyleClasses} col-sm-offset-${labelWidth}"/>
</c:if>
<%-- formulário com layout vertical --%>
<c:if test="${formlayout eq 'vertical'}">
	<c:set var="labelStyleClasses" value=""/>
	<c:set var="controlStyleClasses" value=""/>
	
</c:if>


<c:if test="${!empty value or empty attrMap.hideIfEmpty or !attrMap.hideIfEmpty}">
<div class="form-group ${(!empty violation || !empty bindingErrors) ?  'has-error' : ''}">
	<c:if test="${!(attrMap.withoutLabel || label eq 'none')}">
		<label class="${labelStyleClasses}"><strong>${label}<c:if test="${attrMap.required}">*</c:if></strong> </label>
	</c:if>
	<div class="${controlStyleClasses}">
		<jsp:doBody/>
		<c:if test="${!empty violation}"><%-- VALIDATION ERROR --%>
			<span class="help-block">${violation.message}</span>
		</c:if>
		<c:if test="${!empty bindingErrors}"><%-- SPRING MVC BINDING ERROR --%>
			<c:forEach items="${bindingErrors}" var="fieldError">
				<span class="help-block"><spring:message code="${fieldError.code}" text="${fieldError.defaultMessage}"/></span>
			</c:forEach>
		</c:if>
		<c:if test="${!empty attrMap.helpText}">
			<p class="help-block"><spring:message code="${attrMap.helpText}" text="${attrMap.helpText}"/></p>
		</c:if>
	</div>
</div>
</c:if>
