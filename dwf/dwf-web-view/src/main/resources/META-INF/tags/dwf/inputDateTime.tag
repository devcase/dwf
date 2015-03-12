<%--
Ver dwf-core.js e DwfCustomDateEditor
--%>
<%@tag import="java.time.ZoneId"%>
<%@tag import="java.util.Map"%>
<%@tag import="java.util.TimeZone"%>
<%@tag import="org.springframework.context.i18n.LocaleContextHolder"%>
<%@tag import="java.util.Locale"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>
<%
Locale locale = LocaleContextHolder.getLocale();
String datePatternDatePicker;
String timePatternDatePicker;
String datePatternDateFormatter;
if(locale.equals(Locale.US)) {
	datePatternDatePicker="m/d/Y";
	timePatternDatePicker="h:i a";
	datePatternDateFormatter="MM/dd/yyyy hh:mm a";
} else if(locale.equals(Locale.JAPAN) || locale.equals(Locale.CHINA) || locale.equals(Locale.KOREAN)){
	datePatternDatePicker="Y/m/d";
	timePatternDatePicker="h:i a";
	datePatternDateFormatter="yyyy/MM/dd hh:mm a";
} else {
	datePatternDatePicker="d/m/Y";
	timePatternDatePicker="H:i";
	datePatternDateFormatter="dd/MM/yyyy HH:mm";
}
getJspContext().setAttribute("datePatternDatePicker", datePatternDatePicker);
getJspContext().setAttribute("timePatternDatePicker", timePatternDatePicker);
getJspContext().setAttribute("datePatternDateFormatter", datePatternDateFormatter);

Map<String, Object> attrMap = (Map<String, Object>) getJspContext().getAttribute("attrMap");
String timeZoneId = attrMap.containsKey("timeZone") ? (String) attrMap.get("timeZone") : null;
ZoneId zoneId = timeZoneId != null ? ZoneId.of(timeZoneId) : ZoneId.systemDefault();
TimeZone timeZone = timeZoneId != null ? TimeZone.getTimeZone(timeZoneId) : TimeZone.getDefault();
getJspContext().setAttribute("timeZone", timeZone);

getJspContext().setAttribute("zoneId", zoneId);

%>
<dwf:formGroup parentAttrMap="${attrMap}">
	<div class="row">
		<div class="col-xs-7">
			<div class="input-group">
				<div class="input-group-addon"><span class="glyphicon glyphicon-calendar text-muted"></span></div>
				<input type="text" value="<fmt:formatDate type="BOTH" value="${value}" pattern="${datePatternDateFormatter}" timeZone="${timeZone }"/>" name="${name}"
				<c:if test="${attrMap.required}">required="required"</c:if>
				class='form-control <c:if test="${attrMap.required}">required</c:if> date-time-picker' 
				datetimepicker-date-format="${datePatternDatePicker}"
				datetimepicker-time-format="${timePatternDatePicker}"
				timezoneoffset="${timeZone.rawOffset}"
				/>
			</div>
		</div>
		<div class="col-xs-5">
			<p class="help-block">(${zoneId})</p>
		</div>
	</div>
	 
	
</dwf:formGroup>
