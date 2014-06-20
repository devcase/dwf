<%-- ATENÇÃO - Não quebre linhas! --%><%@tag import="java.util.Calendar"%><%@tag import="java.util.Date"%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%><%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%><%@ taglib uri="http://dwf.devcase.com.br/dwf" prefix="dwf"%><%@taglib uri="http://www.springframework.org/tags" prefix="spring"%><%@ attribute name="value" required="true" type="java.lang.Object"%><%
	Object value = getJspContext().getAttribute("value");
	if (value instanceof CharSequence) {
		getJspContext().setAttribute("format", "string");
	} else if (value instanceof Number) {
		getJspContext().setAttribute("format", "number");
	} else if (value instanceof Date || value instanceof Calendar) {
		getJspContext().setAttribute("format", "date");
	} else if (value instanceof Boolean) {
		getJspContext().setAttribute("format", "boolean");
	} else {
	}
%><c:choose><c:when test="${format eq 'string'}"><dwf:escapeHtml value="${value}" /></c:when><c:when 
	test="${format eq 'boolean'}"><dwf:yesNo value="${value}" /></c:when><c:when 
	test="${format eq 'date'}"><fmt:formatDate value="${value}" type="date" pattern="${datePatternJava}" /></c:when><c:when 
	test="${format eq 'number'}"><fmt:formatNumber value="${value}" /></c:when><c:otherwise
	>${value}</c:otherwise></c:choose>