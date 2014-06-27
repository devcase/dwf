package dwf.web.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import dwf.web.controller.BaseController;

@Controller
public class LoginController extends BaseController {
	
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String login() {
		return "/login/login";
	}
	
	@RequestMapping(value="/logout", method=RequestMethod.GET)
	public String logout(HttpServletRequest request) {
		try {
			request.logout();
		} catch (ServletException e) {
		}
		return "redirect:/";
	}
}
