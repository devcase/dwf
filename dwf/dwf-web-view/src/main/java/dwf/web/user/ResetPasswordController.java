package dwf.web.user;

import javax.validation.Valid;
import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import dwf.user.domain.ResetPasswordBean;
import dwf.user.domain.VerificationToken;
import dwf.user.service.BaseUserService;
import dwf.user.service.VerificationTokenService;
import dwf.web.controller.BaseController;
import dwf.web.message.UserMessageType;

@Controller
public class ResetPasswordController extends BaseController {

	private final BaseUserService userService;
	private final VerificationTokenService verificationTokenService;
	
	@Autowired
	public ResetPasswordController(BaseUserService userService, VerificationTokenService verificationTokenService) {
		this.userService = userService;
		this.verificationTokenService = verificationTokenService;
	}

	@RequestMapping(value = "/resetPassword", method = RequestMethod.GET)
	public String openResetPassword() {
		return "reset_password_request";
	}
	
	@RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
	public String doResetPassword(@RequestParam String email) {
		try {
			userService.resetPasswordRequest(email);
		} catch (ValidationException e) {
			addUserMessage("dwf.user.registration.reset.error", UserMessageType.DANGER);
			return "reset_password_request";
		}
		addUserMessage("dwf.user.registration.reset.success", UserMessageType.SUCCESS);
		return "confirmation";
	}
	
	@RequestMapping(value = "/resetPassword/{token}", method = RequestMethod.GET)
	public String openResetPasswordChange(@PathVariable String token, Model model) {
		try {
			final VerificationToken verificationToken = verificationTokenService.findByToken(token);
			if (verificationToken.isVerified()) {
				addUserMessage("dwf.user.registration.confirmation.error", UserMessageType.DANGER);
				return "confirmation";
			}
			model.addAttribute("token", token);
			return "reset_password_change";
		} catch (ValidationException e) {
			addUserMessage("dwf.user.registration.confirmation.error", UserMessageType.DANGER);
			return "confirmation";
		}
	}
	
	@RequestMapping(value = "/resetPassword/{token}", method = RequestMethod.POST)
	public String doResetPasswordChange(@PathVariable String token, @Valid ResetPasswordBean resetPasswordBean, 
			BindingResult result) {

		if (result.hasErrors()) {
			addUserMessage("message.password.change.error", UserMessageType.DANGER);
			return "reset_password_change";
		}
		
		try {
			userService.resetPasswordChange(token, resetPasswordBean);
		} catch (ValidationException e) {
			addUserMessage("message.password.change.error", UserMessageType.DANGER);
			return "reset_password_change";
		}
		
		addUserMessage("message.password.change.success", UserMessageType.SUCCESS);
		return "confirmation";
	}
}
