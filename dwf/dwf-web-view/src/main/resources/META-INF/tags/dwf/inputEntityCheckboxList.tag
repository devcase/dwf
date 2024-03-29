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
	<c:forEach items="${targetEntityList}" var="targetEntity" varStatus="loopStatus">
		<div class="checkbox" style="display: inline-block; margin-right: 20px;">
			<c:set var="checked" value="${value.contains(targetEntity) || selectedIds.contains(targetEntity.id)}"/>
			<label >
				<input type="checkbox" name="${name}[].id" value="${targetEntity.id }" ${checked ? 'checked="checked"' : '' }>
					<c:if test="${hasIcon and !empty targetEntity.xsIconImage and !attrMap.noIcons}"><img src="<dwf:remoteUrl uploadKey="${targetEntity.xsIconImage}" />" style="width: 1em; height: 1em;"/></c:if>
					<dwf:autoFormat value="${targetEntity}"/></input>
			</label>
		</div>
	</c:forEach>
</dwf:formGroup>
