<%--
Atributos:

  * formaction: 	Action da tag <form/>
  * title:			Cabeçalho do painel
  * labelKey: 		Chave para o cabeçalho do painel (se title não informado)
  * buttonLabelKey:	Chave para o texto do botão submit (padrão: action.save)
  * panelless:		Não imprime o painel ou o título - basicamente apenas o formulário

 --%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ tag dynamic-attributes="attrMap"%>
<%@ variable name-given="entity" scope="NESTED" variable-class="java.lang.Object"%>
<c:if test="${!empty entityName}">
	<dwf:resolveEL el="${entityName}" var="entity"/>
</c:if>
<dwf:mergeMaps map1="${attrMap}" map2="${attrMap.parentAttrMap}" var="attrMap"/>
<c:set var="formaction" value="${attrMap.formaction}"/>
<c:if test="${empty attrMap.formaction}">
	<c:set var="formaction" value="${appPath}/${entityName}/save"/>
</c:if>
<%-- DETERMINAR TÍTULO --%>
<c:choose>
	<c:when test="${!empty attrMap.title}">
		<c:set var="panelTitle" value="${attrMap.title}"/>
	</c:when>
	<c:when test="${!empty attrMap.labelKey}">
		<spring:message code="${attrMap.labelKey}" var="panelTitle"/>
	</c:when>
	<c:when test="${!empty entityName}">
		<spring:message code="label.editForm.header.${empty entity.id ? 'create' : 'edit'}" var="panelTitle"/>
		<spring:message code="domain.${entityName}" var="entityDisplayName"/>
		<c:set var="panelTitle" value="${panelTitle} ${entityDisplayName }"/>
	</c:when>
</c:choose><%-- /DETERMINAR TÍTULO --%>
<%-- DETERMINAR LABEL DO BOTÃO --%>
<c:set var="buttonLabelKey" value="${!empty attrMap.buttonLabelKey ? attrMap.buttonLabelKey : 'action.save'}"/>

<%-- DETERMINAR A POSIÇÃO DO BOTÃO --%>
<c:set var="buttonAlign" value="${!empty attrMap.buttonAlign ? attrMap.buttonAlign : 'right'}"/>

<c:if test="${!attrMap.panelless}">
	<c:if test="${empty attrMap.titleType or attrMap.titleType eq 'h1'}">
		<c:if test="${!empty panelTitle and panelTitle ne 'none'}"><h1>${panelTitle}</h1></c:if>
	</c:if>
	<div class="panel panel-default center-block" style="${attrMap.panelStyle}">
	<c:if test="${attrMap.titleType eq 'panel-heading'}">
		<c:if test="${!empty panelTitle and panelTitle ne 'none'}">
		<div class="panel-heading"><h3 class="panel-title">${panelTitle}</h3></div>
		</c:if>
	</c:if>
		<div class="panel-body">
</c:if>


		<form class="form-horizontal validate dwf-progressbaronsubmit" method="POST" action="${formaction}" role="form" <c:if test="${!empty attrMap.formId}">id="${attrMap.formId}"</c:if>
			<c:if test="${!empty attrMap.enctype}">enctype="${attrMap.enctype}"</c:if>>
			

			<sec:csrfInput />
			<c:if test="${!empty entity }">
				<input type="hidden" name="id" value="${entity.id}"/>
			</c:if>
			
			<c:set scope="request" var="parentFormAttrMap" value="${attrMap}"/>
			<jsp:doBody />
			<c:set scope="request" var="parentFormAttrMap" value="${null}"/>
			
			<c:if test="${attrMap.ommitbuttons ne true }">
				<div class="text-${buttonAlign}">
					<c:if test="${attrMap.closemodalbutton eq true}"><%-- Close button, when in a modal (see modal.tag) --%>
				 		<button type="button" class="btn btn-default" data-dismiss="modal"><spring:message code="action.cancel"/></button>
				 	</c:if>
					<button type="submit" class="btn btn-primary" data-loading-text="<spring:message code="action.wait"/>" formaction="${formaction}">
				 		<spring:message code="${buttonLabelKey}" text="${buttonLabelKey}"/>
					</button>
				</div>
			</c:if>
		</form>
		
<c:if test="${!attrMap.panelless}">
		</div><!-- /.box-content -->
	</div>
</c:if>
