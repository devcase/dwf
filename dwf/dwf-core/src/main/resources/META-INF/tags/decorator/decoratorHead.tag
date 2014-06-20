<%@tag import="com.opensymphony.module.sitemesh.HTMLPage"%><%@tag import="com.opensymphony.module.sitemesh.Page"%><%@tag import="dwf.web.sitemesh.DumbHtmlPage"%><%
Object decoratedPage = this.getJspContext().findAttribute("dwf_decoratedPage");
if(decoratedPage != null && decoratedPage instanceof HTMLPage) {
	((HTMLPage) decoratedPage).writeHead(out);
}
%>