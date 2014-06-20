<%-- ATENÇÃO - Não quebre linhas! --%><%@tag import="java.util.Iterator"%>
<%@tag import="javax.validation.Path"%>
<%@tag
	import="javax.validation.ConstraintViolation"%><%@tag
	import="javax.validation.ConstraintViolationException"%><%@ taglib
	uri="http://java.sun.com/jsp/jstl/core" prefix="c"%><%@ taglib
	uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%><%@ attribute
	name="constraintViolationException" required="true"
	type="javax.validation.ConstraintViolationException"%><%@ attribute name="var"
	required="false"%><%@ attribute name="path"
	required="true" %>
<%
ConstraintViolationException el = (ConstraintViolationException) getJspContext().getAttribute("constraintViolationException");
String varName = (String) getJspContext().getAttribute("var");
String path = (String) getJspContext().getAttribute("path") + ".";

if(el != null && el.getConstraintViolations() != null) {
	ConstraintViolation found = null; 
	for (ConstraintViolation violation : el.getConstraintViolations()) {
		StringBuilder pathComparer = new StringBuilder();
		Path vioPath = violation.getPropertyPath();
		for(Iterator<Path.Node> it = vioPath.iterator(); it.hasNext();) {
			Path.Node node = it.next();
			pathComparer.append(node.getName());
			pathComparer.append(".");
		}
		
		if(pathComparer.toString().equals(path)) {
			found = violation;
		}
	}
	getJspContext().setAttribute(varName, found, PageContext.REQUEST_SCOPE);
	return;	
}
getJspContext().setAttribute(varName, null, PageContext.REQUEST_SCOPE);
return;
%>