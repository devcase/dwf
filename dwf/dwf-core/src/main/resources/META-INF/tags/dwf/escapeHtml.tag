<%-- ATENวรO - Nใo quebre linhas! --%><%@tag import="org.apache.commons.lang3.StringUtils"%><%@tag import="org.apache.commons.lang3.StringEscapeUtils"%><%@ attribute name="value" required="true" %><%
	out.write(StringUtils.replace(StringEscapeUtils.escapeHtml4(value.toString()), "\n", "<br/>"));
%>