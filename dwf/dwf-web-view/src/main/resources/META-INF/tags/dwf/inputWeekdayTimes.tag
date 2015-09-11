<%@tag import="dwf.persistence.embeddable.WeekdayTime"%>
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
<%@ tag dynamic-attributes="attrMap"%>
<dwf:valueForInput parentAttrMap="${attrMap}"/><%-- Disponibiliza as variáveis `value`, `label` e `name` (preciso só do value) --%>
<c:set var="valueToString" value="${!empty value ? value.toString() : null}"/><%-- para fazer valueToString.contains(string) e marcar os checkboxes, já que value.contains(string) não funciona --%>
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
getJspContext().setAttribute("availableTimeStringList", availableTimeStringList);

//Decidindo qual período é mostrado primeiro (1 = madrugada, 2 = manhã etc)
String valueToString = (String) getJspContext().getAttribute("valueToString");
int activePeriod = 2; //padrão manhã
if(valueToString != null && !valueToString.isEmpty()) {
	for(int i = 0; i < availableTimeStringList.size(); i++) {
		if(valueToString.contains(availableTimeStringList.get(i))) {
			//Encontrei um período que já está marcado!
			activePeriod = 1 + (i / 12);
			break;
		}
	}
}
//disponibilizando a variável para uso el EL
getJspContext().setAttribute("activePeriod", activePeriod);
%>
<dwf:formGroup labelWidth="0" parentAttrMap="${attrMap}">
	
	
	<div class="carousel slide dwf-inputweekday-carousel" data-ride="carousel" data-interval="false" data-wrap="false" data-keyboard="false">
	  <!-- Wrapper for slides -->
	  <div class="carousel-inner" role="listbox">
		<c:forEach var="i" begin="1" end="4">
		    <div class="item ${i eq activePeriod ? 'active' : ''}">
				<table  class="table  table-bordered">
					<thead>
						<tr>
							<th></th>
	 						<c:forEach items="${availableTimeList}" var="time" begin="${(i-1)*12}" end="${(i-1)*12 + 11}" varStatus="loopStatus">
	 							<th class="text-center"><dwf:autoFormat value="${time}"/>
	 							</th>
	 						</c:forEach>
						</tr>
					</thead>
					<c:forTokens items="MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY,SATURDAY,SUNDAY" delims="," var="dow">
						<tr>
							<td>
								<spring:message code="java.time.DayOfWeek.${dow}"/>
							</td>
	 						<c:forEach items="${availableTimeStringList}" var="time" begin="${(i-1)*12}" end="${(i-1)*12 + 11}" varStatus="loopStatus">
								<td>
									<label style="width: 100%; height: 100%;" class="text-center">
								    	<c:set var="checkvalue" value="${dow.toLowerCase()}-${time}"/>
								    	<input type="checkbox" autocomplete="off"  ${valueToString.contains(checkvalue) ? 'checked' :''} value="${checkvalue}" name="${name}[]"/>
									</label>
								</td>
							</c:forEach>
						</tr>
					</c:forTokens>
				</table>
		    </div>
		</c:forEach>  
		</div><!-- /.carousel-inner -->
		
		<nav class="text-center">
		  <ul class="pagination carousel-indicators">
		  	<li class="${1 eq activePeriod ? 'active' : ''}"><a href="#"  data-target=".dwf-inputweekday-carousel" data-slide-to="0" >Madrugada</a></li>
		  	<li class="${2 eq activePeriod ? 'active' : ''}"><a href="#"  data-target=".dwf-inputweekday-carousel" data-slide-to="1" >Manhã</a></li>
		  	<li class="${3 eq activePeriod ? 'active' : ''}"><a href="#"  data-target=".dwf-inputweekday-carousel" data-slide-to="2" >Tarde</a></li>
		  	<li class="${4 eq activePeriod ? 'active' : ''}"><a href="#"  data-target=".dwf-inputweekday-carousel" data-slide-to="3" >Noite</a></li>
		  </ul>
		</nav>
	</div><!-- /.carousel -->
	
</dwf:formGroup>