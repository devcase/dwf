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
List targetEntityList = dao.findAll();
getJspContext().setAttribute("targetEntityList", targetEntityList);
%>
<dwf:formGroup parentAttrMap="${attrMap}">
	<div class="btn-group" data-toggle="buttons">
		<c:forEach items="${targetEntityList}" var="targetEntity">
			<label class= "btn btn-default">
				<input type="checkbox" name="${name}.id" value="${targetEntity.id }">${targetEntity.displayText}</input>
			</label>
		</c:forEach>
	</div>	
</dwf:formGroup>
