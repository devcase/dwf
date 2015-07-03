<%--
Atributos:

  * formLayout: 	horizontal
  * value:			
  * property:			
  * ignoreParams:			
  * labelStyleClass
  * withoutLabel:	para não mostrar nenhum label
  * label:			'none' para não mostrar nenhum label

 --%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>
<%@ variable name-given="name" scope="AT_BEGIN" %>
<%@ variable name-given="value" scope="AT_BEGIN" variable-class="java.lang.Object"%>
<%@ variable name-given="label" scope="AT_BEGIN" variable-class="java.lang.String"%>
<dwf:mergeMaps map1="${attrMap}" map2="${attrMap.parentAttrMap}" var="attrMap"/>
<dwf:simpleLabel parentAttrMap="${attrMap}" var="label"/>
<%-- VALOR PADRÃO E NOME DO INPUT --%>
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
<c:if test="${!attrMap.ignoreParams and !empty param[name]}"><%-- usa o parâmetro do request no lugar da propriedade--%>
	<c:set var="value" value="${param[name]}"/>
</c:if>
<c:if test="${!empty attrMap.value}">
	<c:set var="value" value="${attrMap.value}"/>
</c:if>
<c:if test="${!empty attrMap.formLayout}">
	<c:set var="formLayout" value="${attrMap.formLayout}"/>
</c:if>
<dwf:findViolation path="${name}" var="violation"/>
<%-- Search for BindingResults, when using @Valid annotations --%>
<c:set var="bindingErrors" value="${requestScope['org.springframework.validation.BindingResult.form'].getFieldErrors(name)}"/>
<c:if test="${!empty value or empty attrMap.hideIfEmpty or !attrMap.hideIfEmpty}">
<div class="form-group ${(!empty violation || !empty bindingErrors) ?  'has-error' : ''}">
	<c:choose>
		<c:when test="${attrMap.withoutLabel || label eq 'none'}"><%-- WITHOU LABEL--%>
			<div class="${formLayout eq 'horizontal' ? 'col-sm-8 col-sm-offset-4' : 'col-xs-12'} form-group-content">
				<jsp:doBody/>
				<c:if test="${!empty violation}"><%-- VALIDATION ERROR --%>
					<span class="help-block">${violation.message}</span>
				</c:if>
				<c:if test="${!empty bindingErrors}"><%-- SPRING MVC BINDING ERROR --%>
					<c:forEach items="${bindingErrors}" var="fieldError">
						<span class="help-block">${fieldError.defaultMessage}</span>
					</c:forEach>
				</c:if>
		</c:when>
		<c:when test="${formLayout eq 'horizontal'}"><%-- LAYOUT HORIZONTAL --%>
			<label class="col-sm-4 control-label ${empty attrMap.labelStyleClass ? '' :  attrMap.labelStyleClass}"><strong>${label}<c:if test="${attrMap.required}">*</c:if></strong> </label>
			<div class="col-sm-8 form-group-content">
				<jsp:doBody/>
				<c:if test="${!empty violation}"><%-- VALIDATION ERROR --%>
					<span class="help-block">${violation.message}</span>
				</c:if>
				<c:if test="${!empty bindingErrors}"><%-- SPRING MVC BINDING ERROR --%>
					<c:forEach items="${bindingErrors}" var="fieldError">
						<span class="help-block"><spring:message code="${fieldError.code}" text="${fieldError.defaultMessage}"/></span>
					</c:forEach>
				</c:if>
		</c:when>
		<c:otherwise><%-- LAYOUT PADRÃO --%>
			<label class="control-label text-left ${empty attrMap.labelStyleClass ? '' :  attrMap.labelStyleClass}"><strong>${label}<c:if test="${attrMap.required}">*</c:if></strong> </label>
			<div class=" form-group-content">
				<jsp:doBody/>
				<c:if test="${!empty violation}"><%-- VALIDATION ERROR --%>
					<span class="help-block">${violation.message}</span>
				</c:if>
				<c:if test="${!empty bindingErrors}"><%-- SPRING MVC BINDING ERROR --%>
					<c:forEach items="${bindingErrors}" var="fieldError">
						<span class="help-block">${fieldError.code}</span>
					</c:forEach>
				</c:if>
		</c:otherwise>
	</c:choose>
	<c:if test="${!empty attrMap.helpText}">
		<p class="help-block"><spring:message code="${attrMap.helpText}" text="${attrMap.helpText}"/></p>
	</c:if>
	</div>
</div>
</c:if>
