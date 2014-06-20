<%-- ATENวรO - Nใo quebre linhas! --%><%@tag import="javax.el.ExpressionFactory"%><%@tag import="javax.el.ValueExpression"%><%@tag import="javax.el.ELContext"%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %><%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %><%@ attribute name="el" required="true" %><%@ attribute name="var" required="false" %><%
String el = (String) getJspContext().getAttribute("el");
String varName = (String) getJspContext().getAttribute("var");

ELContext elContext = getJspContext().getELContext();
ExpressionFactory expFactory = ExpressionFactory.newInstance();
Object value = expFactory.createValueExpression(elContext, "${" + el + "}", Object.class).getValue(elContext);

if(varName != null && varName != "") {
	getJspContext().setAttribute(varName, value, PageContext.REQUEST_SCOPE);
} else {
	if(value != null && value.toString() != null) {
		getJspContext().getOut().print(value);
	}
}
%>