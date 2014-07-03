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
DateFormat dateformat = new SimpleDateFormat("HH:mm");
Date from = dateformat.parse((String) attrMap.get("from"));
Date to = dateformat.parse((String) attrMap.get("to"));
Date currentDate = from;

while(currentDate.compareTo(to) <= 0) {
	availableTimeList.add(currentDate);
	currentDate = DateUtils.addMinutes(currentDate, 30);
}

getJspContext().setAttribute("availableTimeList", availableTimeList);
%>
<dwf:formGroup parentAttrMap="${attrMap}">
	<div class="row" >
		<c:forEach items="${availableTimeList}" var="availableTime" varStatus="loopStatus">
			<fmt:formatDate value="${availableTime}" type="time" var="availableTimeDisplayed" timeStyle="SHORT"/>
			<div class="col-xs-4 col-sm-3 col-lg-2">
			<label class= "float-left btn btn-borderless ">
				<input type="checkbox" name="${name}[]" value="${availableTimeDisplayed}" ${value.contains(availableTime) ? 'checked="checked"' : '' }>
					${availableTimeDisplayed }</input>
			</label>
			</div>
		</c:forEach>
	</div>	
</dwf:formGroup>
