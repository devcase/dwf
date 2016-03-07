<%@tag import="java.util.Collection"%>
<%@tag import="java.util.Arrays"%>
<%@tag import="java.util.Collections"%>
<%@tag import="dwf.utils.SimpleParsedMap"%>
<%@tag import="dwf.persistence.interfaces.HasIcon"%>
<%@tag import="java.util.List"%>
<%@tag import="dwf.persistence.dao.DAO"%>
<%@tag import="java.util.Map"%>
<%@tag import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>
<%
// IMPORTANT: targetEntity has to implement HasIcon

Map attrMap = (Map) getJspContext().getAttribute("attrMap");
String targetEntityName = (String) attrMap.get("targetEntity");
if(targetEntityName == null)
	targetEntityName = (String) attrMap.get("property");
DAO dao = (DAO) WebApplicationContextUtils.getWebApplicationContext(application).getBean(targetEntityName + "DAO");

String filter = (String) attrMap.get("filter");
if(filter == null) {
	List targetEntityList = dao.findAll();
	getJspContext().setAttribute("targetEntityList", targetEntityList);
} else {
	List targetEntityList = dao.findByFilter(new SimpleParsedMap(filter.split(";|=")));
	getJspContext().setAttribute("targetEntityList", targetEntityList);
}

//hasicon?
Class targetEntityClass = dao.getEntityClass();
getJspContext().setAttribute("hasIcon", HasIcon.class.isAssignableFrom(targetEntityClass));
%>
<c:set var="selectedIds" value="${attrMap.selectedIds}"/>
<%
Object rawselectedids = getJspContext().getAttribute("selectedIds");
if(rawselectedids == null) {
	getJspContext().setAttribute("selectedIds", Collections.EMPTY_LIST);
} else if(rawselectedids instanceof Collection) {
	//do nothing
} else if(rawselectedids instanceof Long[]) {
	getJspContext().setAttribute("selectedIds", Arrays.asList((Long[])rawselectedids));
} else if(rawselectedids instanceof Object[]) {
	getJspContext().setAttribute("selectedIds", Arrays.asList((Object[])rawselectedids));
} 
%>
<dwf:formGroup parentAttrMap="${attrMap}">
	<div class="btn-group" >
		<c:set var="inputname" value="${name}[].id"/>
		<c:forEach items="${targetEntityList}" var="targetEntity" varStatus="loopStatus">
			<c:set var="checked" value="${value.contains(targetEntity) || (!empty selectedIds && selectedIds.contains(targetEntity.id))}"/>
			
			<label class= "dwf-boxcheckbox">
				<input type="checkbox" name="${inputname}" value="${targetEntity.id }" ${checked ? 'checked="checked"' : '' } />
				<c:if test="${hasIcon and !empty targetEntity.smIconImage  and !attrMap.noIcons}"><img src="<dwf:remoteUrl uploadKey="${targetEntity.smIconImage}" />"/></c:if>
				<span><dwf:autoFormat value="${targetEntity}"/></span>
			</label>
		</c:forEach>
	</div>	
</dwf:formGroup>
