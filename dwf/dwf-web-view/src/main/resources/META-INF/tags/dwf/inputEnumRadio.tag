<%@tag import="java.text.SimpleDateFormat"%>
<%@tag import="java.text.DateFormat"%>
<%@tag import="org.apache.commons.lang3.time.DateUtils"%>
<%@tag import="java.util.Date"%>
<%@tag import="java.util.ArrayList"%>
<%@tag import="java.util.List"%>
<%@tag import="java.util.Map"%>
<%@tag import="org.springframework.web.context.support.WebApplicationContextUtils"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>
<%
Map attrMap = (Map) getJspContext().getAttribute("attrMap");
String enumName = (String) attrMap.get("enumtype");
Class<Enum> clazz = (Class<Enum>) Class.forName(enumName);
Enum[] values = clazz.getEnumConstants();
getJspContext().setAttribute("enumValues", values);
%>
<dwf:formGroup parentAttrMap="${attrMap}">
	<div class="row" ><c:forEach items="${enumValues}" var="enumValue">
		<div class="col-12">
			<label class= "float-left btn btn-borderless ">
				<input type="radio" name="${name}" value="${enumValue}"
					${value == enumValue ? 'checked="checked"' : '' } ${attrMap.required? ' required="true"' : '' }>
					<spring:message code="${attrMap.enumtype}.${enumValue}" text="?${attrMap.enumtype}.${enumValue}?" />
			</label>
		</div>
	</c:forEach>
	</div>	
</dwf:formGroup>
