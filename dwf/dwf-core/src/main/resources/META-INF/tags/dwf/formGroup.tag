<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>
<%@ variable name-given="name" scope="AT_BEGIN" %>
<%@ variable name-given="value" scope="AT_BEGIN" variable-class="java.lang.Object"%>
<c:set var="attrMap" value="${!empty attrMap.parentAttrMap ? attrMap.parentAttrMap : attrMap}"/><%-- opção para uso em outras tags --%>
<dwf:findViolation path="${attrMap.property}" var="violation"/>
<dwf:simpleLabel parentAttrMap="${attrMap}" var="labelText"/>
<%-- VALOR PADRÃO E NOME DO INPUT --%>
<c:choose>
	<c:when test="${!empty attrMap.name}">
		<dwf:resolveEL el="${attrMap.name}" var="value" />
		<c:set var="name" value="${attrMap.name}"/>		
	</c:when>
	<c:otherwise><%-- PROPERTY --%>
		<dwf:resolveEL el="${entityName}.${attrMap.property}" var="value" />
		<c:set var="name" value="${attrMap.property}"/>
	</c:otherwise>
</c:choose>
<c:if test="${!attrMap.ignoreParams and !empty param[name]}"><%-- usa o parâmetro do request no lugar da propriedade--%>
	<c:set var="value" value="${param[name]}"/>
</c:if>


<div class="form-group ${!empty violation ?  'has-error' : ''}">
	<c:choose>
		<c:when test="${attrMap.withoutLabel}"><%-- WITHOU LABEL--%>
			<div class="col-xs-12">
				<jsp:doBody/>
				<c:if test="${!empty violation}"><%-- VALIDATION ERROR --%>
					<span class="help-block">${violation.message}</span>
				</c:if>
		</c:when>
		<c:when test="${formLayout eq 'horizontal'}"><%-- LAYOUT HORIZONTAL --%>
			<label class="col-sm-4 control-label ${empty attrMap.labelStyleClass ? '' :  attrMap.labelStyleClass}"><strong>${labelText}<c:if test="${attrMap.required}">*</c:if></strong> </label>
			<div class="col-sm-8">
				<jsp:doBody/>
				<c:if test="${!empty violation}"><%-- VALIDATION ERROR --%>
					<span class="help-block">${violation.message}</span>
				</c:if>
		</c:when>
		<c:otherwise><%-- LAYOUT PADRÃO --%>
			<label class="col-sm-12 control-label text-left ${empty attrMap.labelStyleClass ? '' :  attrMap.labelStyleClass}"><strong>${labelText}<c:if test="${attrMap.required}">*</c:if></strong> </label>
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
