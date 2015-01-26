<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>
<%@ variable name-given="name" scope="AT_BEGIN" %>
<%@ variable name-given="value" scope="AT_BEGIN" variable-class="java.lang.Object"%>
<%@ variable name-given="label" scope="AT_BEGIN" variable-class="java.lang.String"%>
<dwf:mergeMaps map1="${attrMap}" map2="${attrMap.parentAttrMap}" var="attrMap"/>
<dwf:findViolation path="${attrMap.property}" var="violation"/>
<dwf:simpleLabel parentAttrMap="${attrMap}" var="label"/>
<%-- VALOR PADR�O E NOME DO INPUT --%>
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
<c:if test="${!attrMap.ignoreParams and !empty param[name]}"><%-- usa o par�metro do request no lugar da propriedade--%>
	<c:set var="value" value="${param[name]}"/>
</c:if>
<c:if test="${!empty attrMap.value}">
	<c:set var="value" value="${attrMap.value}"/>
</c:if>


<div class="form-group ${!empty violation ?  'has-error' : ''}">
	<c:choose>
		<c:when test="${attrMap.withoutLabel || label eq 'none'}"><%-- WITHOU LABEL--%>
			<div class="${formLayout eq 'horizontal' ? 'col-sm-8 col-sm-offset-4' : 'col-xs-12'}">
				<jsp:doBody/>
				<c:if test="${!empty violation}"><%-- VALIDATION ERROR --%>
					<span class="help-block">${violation.message}</span>
				</c:if>
		</c:when>
		<c:when test="${formLayout eq 'horizontal'}"><%-- LAYOUT HORIZONTAL --%>
			<label class="col-sm-4 control-label ${empty attrMap.labelStyleClass ? '' :  attrMap.labelStyleClass}"><strong>${label}<c:if test="${attrMap.required}">*</c:if></strong> </label>
			<div class="col-sm-8">
				<jsp:doBody/>
				<c:if test="${!empty violation}"><%-- VALIDATION ERROR --%>
					<span class="help-block">${violation.message}</span>
				</c:if>
		</c:when>
		<c:otherwise><%-- LAYOUT PADR�O --%>
			<label class="col-sm-12 control-label text-left ${empty attrMap.labelStyleClass ? '' :  attrMap.labelStyleClass}"><strong>${label}<c:if test="${attrMap.required}">*</c:if></strong> </label>
			<div class="col-sm-12">
				<jsp:doBody/>
				<c:if test="${!empty violation}"><%-- VALIDATION ERROR --%>
					<span class="help-block">${violation.message}</span>
				</c:if>
		</c:otherwise>
	</c:choose>
	<c:if test="${!empty attrMap.helpText}">
		<p class="help-block">${!empty attrMap.helpText}</p>
	</c:if>
	</div>
</div>
