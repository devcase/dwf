package dwf.web.user;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;

import dwf.web.controller.BaseController;

@Controller
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class SigninController extends BaseController {

	@RequestMapping(value = "/signin", method = RequestMethod.GET)
	public String getLoginPage(
			HttpServletRequest request, 
			Model model) {
		if (request.getParameter("error") != null) {
			model.addAttribute("loginerror", true);
		}
		if (request.getParameter("logout") != null) {
			model.addAttribute("logout", true);
		}
		return "signin";
	}
}
