<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ tag dynamic-attributes="attrMap"%>
<dwf:formGroup parentAttrMap="${attrMap}">
	<select name="${name}"
		<c:if test="${attrMap.required}">required="required"</c:if> class="form-control">
		<option value=""><spring:message code="label.select.empty${attrMap.required ? '.required' : ''}"/></option>
		<c:forTokens items="654,246,25,641,213,19,29,0,740,107,31,739,96,318,752,248,218,65,36,204,394,237,225,M15,208,44,263,473,412,40,745,M08,241,M19,215,756,748,75,721,222,505,229,266,3,083-3,M21,707,300,495,494,M06,24,456,214,1,47,37,39,41,4,265,M03,224,626,M18,233,734,M07,612,M22,63,M11,604,320,653,630,077-9,249,M09,184,479,376,74,217,76,757,600,212,M12,389,746,M10,738,66,243,45,M17,623,611,613,094-2,643,724,735,638,M24,747,088-4,356,633,741,M16,72,453,422,33,250,743,749,366,637,12,464,082-5,M20,M13,634,M14,M23,655,610,370,21,719,755,744,73,78,69,70,092-2,104,477,081-7,097-3,085-x,099-x,090-2,089-2,087-6,098-1,487,751,64,62,399,168,492,652,341,79,488,14,753,086-8,254,409,230,091-4,84"
			delims="," var="bankCode">
			<option value="${bankCode}"
				<c:if test="${bankCode eq value}">selected</c:if>
				><spring:message code="brbank.name.${bankCode}"/> (${bankCode})</option>
		</c:forTokens>
	</select>
</dwf:formGroup>