<%-- ATENวรO - Nใo quebre linhas! --%><%@tag import="java.util.HashMap"
%><%@tag import="java.util.Map"
%><%@tag import="javax.el.ExpressionFactory"
%><%@tag import="javax.el.ValueExpression"
%><%@tag import="javax.el.ELContext"
%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" 
%><%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" 
%><%@ attribute name="map1" required="true" type="java.util.Map" 
%><%@ attribute name="map2" required="true" type="java.util.Map" 
%><%@ attribute name="var" required="true" rtexprvalue="false" 
%><%@ variable alias="result" name-from-attribute="var" variable-class="java.lang.Object" scope="AT_BEGIN" 
%><%
Map result = new HashMap();
Map map = (Map)getJspContext().getAttribute("map2");
if(map != null)
	result.putAll(map);
map = (Map)getJspContext().getAttribute("map1");
if(map != null)
	result.putAll(map);
getJspContext().setAttribute("result", result);
%>