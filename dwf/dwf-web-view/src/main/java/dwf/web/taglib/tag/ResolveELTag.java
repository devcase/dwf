package dwf.web.taglib.tag;

import java.io.IOException;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.springframework.web.util.TagUtils;

/**
 * 
 * @author hirata
 *
 */
public class ResolveELTag extends SimpleTagSupport {

	private String var;
	private String el;
	private String scope = TagUtils.SCOPE_PAGE;

	public void setVar(String var) {
		this.var = var;
	}
	public void setEl(String el) {
		this.el = el;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	
	@Override
	public void doTag() throws JspException, IOException {
		ELContext elContext = getJspContext().getELContext();
		ExpressionFactory expFactory = ExpressionFactory.newInstance();
		Object value = expFactory.createValueExpression(elContext, "${" + el + "}", Object.class).getValue(elContext);
		if(var != null && var != "") {
			getJspContext().setAttribute(var, value, TagUtils.getScope(this.scope));
		} else {
			if(value != null && value.toString() != null) {
				getJspContext().getOut().print(value);
			}
		}
	}
}
