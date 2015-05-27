package dwf.web.user;

import javax.validation.Valid;
import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import dwf.user.domain.ChangePasswordBean;
import dwf.user.service.BaseUserService;
import dwf.web.controller.BaseController;
import dwf.web.message.UserMessageType;

@Controller
public class ChangePasswordController extends BaseController {

	private final BaseUserService userService;
	
	@Autowired
	public ChangePasswordController(final BaseUserService userService) {
		this.userService = userService;
	}

	@RequestMapping(value = "/changePassword", method = RequestMethod.GET)
	public String openChangePassword(Model model) {
		model.addAttribute("changePasswordBean", new ChangePasswordBean());
		return "change_password";
	}
	
	@RequestMapping(value = "/changePassword", method = RequestMethod.POST)
	public String changePassword(@Valid ChangePasswordBean changePasswordBean, BindingResult result,
			RedirectAttributes attributes, Model model) {

		if (result.hasErrors()) {
			addUserMessage("message.password.change.error", UserMessageType.DANGER);
			model.addAttribute(BindingResult.MODEL_KEY_PREFIX + "form", result);
			return "change_password";
		}
		
		try {
			userService.changePassword(changePasswordBean);
		} catch (ValidationException e) {
			//addValidationExceptionMessage(e);
			addUserMessage("message.password.change.error", UserMessageType.DANGER);
			return "change_password";
		}
		
		addUserMessage("message.password.change.success", UserMessageType.SUCCESS);
		return "change_password";
	}
}
