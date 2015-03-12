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
List<Date> availableTimeList = new ArrayList<Date>();
if(attrMap.containsKey("items")) {
	getJspContext().setAttribute("availableTimeList", attrMap.get("items"));
	
} else {
	DateFormat dateformat = new SimpleDateFormat("HH:mm");
	Date from = attrMap.containsKey("from") ? dateformat.parse((String) attrMap.get("from")) : dateformat.parse("00:00");
	Date to = attrMap.containsKey("to") ? dateformat.parse((String) attrMap.get("to")) : dateformat.parse("23:59");
	Date currentDate = from;

	while(currentDate.compareTo(to) <= 0) {
		availableTimeList.add(currentDate);
		currentDate = DateUtils.addMinutes(currentDate, 30);
	}

	getJspContext().setAttribute("availableTimeList", availableTimeList);
}

%>
<dwf:formGroup parentAttrMap="${attrMap}">
	<select name="${name}"
		<c:if test="${attrMap.required}">required="required"</c:if> class="form-control">
		<c:forEach items="${availableTimeList}" var="availableTime" varStatus="loopStatus">
			<fmt:formatDate value="${availableTime}" type="time" var="availableTimeDisplayed" timeStyle="SHORT"/>
			<option value="${availableTimeDisplayed}"
				<c:if test="${availableTime eq value}">selected</c:if>
				>${availableTimeDisplayed}</option>
		</c:forEach>
	</select>
</dwf:formGroup>
