<%-- ATENวรO - Nใo quebre linhas! --%><%@tag import="dwf.upload.UploadManager"%><%@tag import="org.springframework.web.context.support.WebApplicationContextUtils"%><%@tag import="org.springframework.web.context.WebApplicationContext"%><%@tag import="javax.el.ExpressionFactory"%><%@tag import="javax.el.ValueExpression"%><%@tag import="javax.el.ELContext"%><%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %><%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %><%@ attribute name="uploadKey" required="true" %><%@ attribute name="var" required="false" %><%
String uploadKey = (String) getJspContext().getAttribute("uploadKey");
String varName = (String) getJspContext().getAttribute("var");
WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(application);
UploadManager um = wac.getBean(UploadManager.class);
String remoteUrl = um.remoteUrl(uploadKey);

if(varName != null && varName != "") {
	getJspContext().setAttribute(varName, remoteUrl, PageContext.REQUEST_SCOPE);
} else {
	if(remoteUrl != null && remoteUrl.toString() != null) {
		getJspContext().getOut().print(remoteUrl);
	}
}
%>