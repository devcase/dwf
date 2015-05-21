package dwf.sample.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import dwf.web.controller.BaseController;

@Controller
public class HomeController extends BaseController {
	
	@RequestMapping("/")
	public String index() {
		return "/home/index";
	}
}
