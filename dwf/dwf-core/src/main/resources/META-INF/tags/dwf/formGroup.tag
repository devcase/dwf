<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>
<c:set var="attrMap" value="${!empty attrMap.parentAttrMap ? attrMap.parentAttrMap : attrMap}"/><%-- opção para uso em outras tags --%>
<dwf:findViolation path="${attrMap.property}" var="violation"/>
<dwf:simpleLabel parentAttrMap="${attrMap}" var="labelText"/>
<%@ variable name-given="name" scope="AT_BEGIN" %>
<%@ variable name-given="value" scope="AT_BEGIN" variable-class="java.lang.Object"%>
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

<div class="form-group ${!empty violation ?  'has-error' : ''}">
	<c:choose>
		<c:when test="${attrMap.withoutLabel}"><%-- WITHOU LABEL--%>
			<div class="col-xs-12">
				<jsp:doBody/>
				<c:if test="${!empty violation}"><%-- VALIDATION ERROR --%>
					<span class="help-block">${violation.message}</span>
				</c:if>
			</div>
		</c:when>
		<c:when test="${formLayout eq 'horizontal'}"><%-- LAYOUT HORIZONTAL --%>
			<label class="col-sm-3 control-label ${empty attrMap.labelStyleClass ? '' :  attrMap.labelStyleClass}"><strong>${labelText}<c:if test="${attrMap.required}">*</c:if></strong> </label>
			<div class="col-sm-9">
				<jsp:doBody/>
				<c:if test="${!empty violation}"><%-- VALIDATION ERROR --%>
					<span class="help-block">${violation.message}</span>
				</c:if>
			</div>
		</c:when>
		<c:otherwise><%-- LAYOUT PADRÃO --%>
			<label class="col-sm-12 control-label text-left ${empty attrMap.labelStyleClass ? '' :  attrMap.labelStyleClass}"><strong>${labelText}<c:if test="${attrMap.required}">*</c:if></strong> </label>
			<div class="col-sm-12">
				<jsp:doBody/>
				<c:if test="${!empty violation}"><%-- VALIDATION ERROR --%>
					<span class="help-block">${violation.message}</span>
				</c:if>
			</div>
		
		</c:otherwise>
	</c:choose>
</div>
