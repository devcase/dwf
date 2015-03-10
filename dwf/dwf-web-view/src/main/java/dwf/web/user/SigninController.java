package dwf.web.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import dwf.web.controller.BaseController;

@Controller
public class SigninController extends BaseController {

	@RequestMapping(value = "/signin", method = RequestMethod.GET)
	public String getLoginPage(
			@RequestParam(required = false) String error,
			@RequestParam(required = false) String logout, 
			Model model) {
		
		if (error != null) {
			model.addAttribute("error", true);
		}
		if (logout != null) {
			model.addAttribute("logout", true);
		}
		return "signin";
	}
}
