<%@tag import="com.opensymphony.module.sitemesh.Page"%><%@tag import="dwf.web.sitemesh.DumbHtmlPage"%><%
Object decoratedPage = this.getJspContext().findAttribute("dwf_decoratedPage");
if(decoratedPage != null && decoratedPage instanceof Page) {
	((Page) decoratedPage).writePage(out);
}
%>