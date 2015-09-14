<%@tag import="dwf.persistence.embeddable.DayOfWeekTime"%>
<%@tag import="java.text.SimpleDateFormat"%>
<%@tag import="org.apache.commons.lang3.time.DateUtils"%>
<%@tag import="java.util.ArrayList"%>
<%@tag import="java.text.DateFormat"%>
<%@tag import="java.util.Date"%>
<%@tag import="java.util.List"%>
<%@tag import="java.util.Map"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ tag dynamic-attributes="attrMap"%>
<dwf:valueForInput parentAttrMap="${attrMap}"/><%-- Disponibiliza as variáveis `value`, `label` e `name` (preciso só do value) --%>
<c:set var="valueToString" value="${!empty value ? value.toString() : null}"/><%-- para fazer valueToString.contains(string) e marcar os checkboxes, já que value.contains(string) não funciona --%>
<c:set var="divisions" value="3"/>
<c:set var="divisionSize" value="${48/divisions}"/>
<%


//Monta lista com horários (como Date, para formatar de acordo com o locale, e como strings no formato HH:mm, para montar o valor dos checkbox)
List<String> availableTimeStringList = new ArrayList<String>();
List<Date> availableTimeList = new ArrayList<Date>();
DateFormat dateformat = new SimpleDateFormat("HH:mm");
Date from = dateformat.parse("00:00");
Date to   = dateformat.parse("23:59");
Date currentDate = from;
while(currentDate.compareTo(to) <= 0) {
	availableTimeList.add(currentDate);
	availableTimeStringList.add(dateformat.format(currentDate));
	currentDate = DateUtils.addMinutes(currentDate, 30);
}
//disponibilizando as listas para uso em ELs
getJspContext().setAttribute("availableTimeList", availableTimeList);

//Decidindo qual período é mostrado primeiro (1 = madrugada, 2 = manhã etc)
String valueToString = (String) getJspContext().getAttribute("valueToString");
int activePeriod = 2; //padrão manhã
if(valueToString != null && !valueToString.isEmpty()) {
	for(int i = 0; i < availableTimeStringList.size(); i++) {
		if(valueToString.contains(availableTimeStringList.get(i))) {
			//Encontrei um período que já está marcado!
			activePeriod = 1 + (i / 16);
			break;
		}
	}
}
//disponibilizando a variável para uso el EL
getJspContext().setAttribute("activePeriod", activePeriod);
%>
<dwf:formGroup parentAttrMap="${attrMap}">
	<div class="carousel slide dwf-inputdayofweektime-carousel" data-ride="carousel" data-interval="false" data-wrap="false" data-keyboard="false">
		<nav class="text-center">
		  <ul class="pagination carousel-indicators">
			<c:forEach var="i" begin="1" end="${divisions}">
			  	<li class="${i eq activePeriod ? 'active' : ''}"><a href="#"  data-target=".dwf-inputdayofweektime-carousel" data-slide-to="${i-1}" ><dwf:autoFormat value="${availableTimeList[(i-1)*divisionSize]}"/> ~ <dwf:autoFormat value="${availableTimeList[(i-1)*divisionSize + divisionSize -1]}"/></a></li>
			</c:forEach>
		  </ul>
		</nav>
	
	  <!-- Wrapper for slides -->
	  <div class="carousel-inner" role="listbox">
		<c:forEach var="i" begin="1" end="${divisions}">
		    <div class="item ${i eq activePeriod ? 'active' : ''}">
				<table  class="table  table-bordered table-striped dwf-inputdayofweektime-table">
					<thead>
						<tr>
							<th></th>
							<c:forTokens items="MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,SUNDAY" delims="," var="dow">
	 							<th class="text-center dwf-check-all-checkbox-from-column"><spring:message code="java.time.DayOfWeek.${dow}.abbr"/></th>
							</c:forTokens>
						</tr>
					</thead>
					<tbody>
					<c:forEach items="${availableTimeList}" var="time" begin="${(i-1)*divisionSize}" end="${(i-1)*divisionSize + divisionSize -1}" varStatus="loopStatus">
						<tr>
							<td  class="dwf-inputdayofweektime-labeltd">
								<span class="dwf-check-all-checkbox-from-row"><dwf:autoFormat value="${time}"/></span>
							</td>
							<c:forTokens items="MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,SUNDAY" delims="," var="dow">
								<td class="dwf-inputdayofweektime-inputtd dwf-checkbox-listener">
									<label class="text-center">
										<fmt:formatDate value="${time}" type="time" pattern="HH:mm" var="timeAsString"/>
								    	<c:set var="checkvalue" value="${dow.toLowerCase()}-${timeAsString}"/>
								    	<input type="checkbox" autocomplete="off"  ${valueToString.contains(checkvalue) ? 'checked' :''} value="${checkvalue}" name="${name}[]"/>
									</label>
								</td>
							</c:forTokens>
						</tr>
					</c:forEach>
					</tbody>
				</table>
		    </div>
		</c:forEach>  
		</div><!-- /.carousel-inner -->
		
	</div><!-- /.carousel -->
	
</dwf:formGroup>