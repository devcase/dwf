<%-- ATENÇÃO - Não quebre linhas! --%><%@tag import="java.util.Iterator"%>
<%@tag import="javax.validation.Path"%>
<%@tag
	import="javax.validation.ConstraintViolation"%><%@tag
	import="javax.validation.ConstraintViolationException"%><%@ taglib
	uri="http://java.sun.com/jsp/jstl/core" prefix="c"%><%@ taglib
	uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%><%@ attribute
	name="constraintViolation" required="true"
	type="javax.validation.ConstraintViolation"%><%@ attribute name="var"
	required="false"%>
<%
ConstraintViolation violation = (ConstraintViolation) getJspContext().getAttribute("constraintViolation");
String varName = (String) getJspContext().getAttribute("var");
String value = null;

if(violation != null) {
	StringBuilder pathBuilder = new StringBuilder();
	Path vioPath = violation.getPropertyPath();
	for(Iterator<Path.Node> it = vioPath.iterator(); it.hasNext();) {
		Path.Node node = it.next();
		if(pathBuilder.length() > 0)
			pathBuilder.append(".");
		pathBuilder.append(node.getName());
	}
	value = pathBuilder.toString();
}
if(varName != null && varName != "") {
	getJspContext().setAttribute(varName, value, PageContext.REQUEST_SCOPE);
} else {
	if(value != null)
		getJspContext().getOut().print(value);
}
%>