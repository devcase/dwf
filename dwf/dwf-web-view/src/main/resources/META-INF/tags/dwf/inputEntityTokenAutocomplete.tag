<%@tag import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@tag import="dwf.web.AjaxHashKeyManager"%>
<%--<%@attribute name="targetEntity" required="true" type="java.lang.String"%>
<%@attribute name="theme" type="java.lang.String" description="theme: null, facebook or mac" %>
<%@attribute name="filter" type="java.lang.String" description="filtro da busca" %>
<%@attribute name="maxTokens" type="java.lang.Integer" description="número máximo de elementos adicionados" %>
<%@attribute name="property" type="java.lang.String"%> --%>
<%@ tag dynamic-attributes="attrMap"%>
<%@taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<c:set var="targetEntity" value="${attrMap.targetEntity}"/>
<c:set var="filter" value="${attrMap.filter}"/>
<c:set var="maxTokens" value="${attrMap.maxTokens}"/>

<%
String entityName = (String) getJspContext().getAttribute("targetEntity");
String filter = (String) getJspContext().getAttribute("filter");
if (filter == null) filter = "";
AjaxHashKeyManager keyManager = (AjaxHashKeyManager) WebApplicationContextUtils.getWebApplicationContext(application).getBean("ajaxHashKeyManager");

getJspContext().setAttribute("hashkey", keyManager.generateHashKey(entityName, filter));
%>
<c:set var="theme" value="${empty attrMap.theme ? 'facebook' : attrMap.theme }"/>

<dwf:formGroup parentAttrMap="${attrMap}">
	<div class="token-input-container" >
		<input type="text" autocomplete="off" class="form-control" theme="${theme}" property="${name}" <c:if test="${!empty maxTokens}">maxTokens="${maxTokens}"</c:if> hashkey="${hashkey}" />
		<c:forEach items="${value}" var="item">
			<input type="hidden" token-id="${item.id}" class="init-token-id" value="${item.id}" />
			<input type="hidden" token-id="${item.id}" class="init-token-name" value="${item.displayText}" />
		</c:forEach>
		<c:if test="${!empty attrMap.exampleList}">
			<p class="help-block">Exemplos: 
				<c:forEach items="${attrMap.exampleList}" var="example" varStatus="loopStatus">${loopStatus.count >1 ? ',':'' }
				<a href="#" class="token-input-example" id="${example.id}" name="${example}">${example}</a></c:forEach>
			</p>
		</c:if>
	</div>
</dwf:formGroup>
