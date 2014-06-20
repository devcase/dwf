<%@tag import="dwf.web.sitemesh.DumbHtmlPage"%><%
Object decoratedPage = this.getJspContext().findAttribute("dwf_decoratedPage");
if(decoratedPage != null && decoratedPage instanceof DumbHtmlPage) {
	((DumbHtmlPage) decoratedPage).writeTable(out);
}
%>