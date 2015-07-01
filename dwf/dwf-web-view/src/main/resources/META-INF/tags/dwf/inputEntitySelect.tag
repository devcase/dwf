<%--
<dwf:inputEntitySelect  property="city" [targetEntity="city"] [required="true"] /> 
 --%>
<%@tag import="dwf.utils.SimpleParsedMap"%>
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
%>
<dwf:formGroup parentAttrMap="${attrMap}">
	<select name="${name}.id"
		<c:if test="${attrMap.required}">required="required"</c:if> class="form-control">
		<option value=""><spring:message code="label.select.empty${attrMap.required ? '.required' : ''}"/></option>
		<c:forEach items="${targetEntityList}" var="targetEntity">
			<option value="${targetEntity.id}"
				<c:if test="${targetEntity.id eq value.id}">selected</c:if>
				>${targetEntity.displayText}</option>
		</c:forEach>
	</select>
</dwf:formGroup>
