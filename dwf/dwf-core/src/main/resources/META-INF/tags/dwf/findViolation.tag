<%@tag import="javax.validation.ValidationException"%><%@tag import="java.util.Iterator"%><%@tag import="javax.validation.Path"%><%@tag
	import="javax.validation.ConstraintViolation"%><%@tag
	import="javax.validation.ConstraintViolationException"%><%@ taglib
	uri="http://java.sun.com/jsp/jstl/core" prefix="c"%><%@ taglib
	uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%><%@ attribute name="path"
	required="true" %><%@ attribute name="var" rtexprvalue="false"  
	required="true" %><%@ variable alias="result" name-from-attribute="var" variable-class="java.lang.Object" scope="AT_BEGIN" %>
<%
ValidationException validationException = (ValidationException) getJspContext().getAttribute("validationException");
String path = (String) getJspContext().getAttribute("path") + ".";

if(validationException != null && validationException instanceof ConstraintViolationException) {
	ConstraintViolationException el = (ConstraintViolationException) validationException;
	if(el.getConstraintViolations() != null) {
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
		getJspContext().setAttribute("result", found, PageContext.PAGE_SCOPE);
		return;	
	}
}
getJspContext().setAttribute("result", null, PageContext.REQUEST_SCOPE);
return;
%>