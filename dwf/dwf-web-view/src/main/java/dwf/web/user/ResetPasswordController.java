package dwf.web.user;

import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;
import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;

import dwf.user.domain.ResetPasswordBean;
import dwf.user.domain.VerificationToken;
import dwf.user.service.BaseUserService;
import dwf.user.service.VerificationTokenService;
import dwf.web.ReCaptcha;
import dwf.web.controller.BaseController;
import dwf.web.mail.JspBasedMailBuilder;
import dwf.web.message.UserMessageType;

@Controller
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class ResetPasswordController extends BaseController {

	@Autowired
	private BaseUserService userService;
	@Autowired
	private VerificationTokenService verificationTokenService;
	@Autowired
	private JspBasedMailBuilder jspBasedMailBuilder;
	
	@Value("${dwf.resetpassword.email.from:dwf@devcase.com.br}")
	private String from ="dwf@devcase.com.br";
	@Value("${dwf.resetpassword.email.subject:Password reset}")
	private String resetPasswordSubject ="Password reset";
	@Value("${dwf.resetpassword.email.template:/WEB-INF/jsp/reset_password_mail.jsp}")
	private String resetPasswordTemplate ="/WEB-INF/jsp/reset_password_mail.jsp";

	public ResetPasswordController() {
	}

	@RequestMapping(value = "/resetPassword", method = RequestMethod.GET)
	public String openResetPassword() {
		return "reset_password_request";
	}
	
	@RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
	@ReCaptcha
	public String doResetPassword(@RequestParam String email) {
		try {
			String passwordToken = userService.generateResetPasswordToken(email);
			Map <String, Object> m =  new HashMap<String, Object>(1);
			m.put("passwordToken", passwordToken);
			jspBasedMailBuilder.sendMail(from, new String[] {email}, resetPasswordSubject, resetPasswordTemplate, m);
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
			model.addAttribute(BindingResult.MODEL_KEY_PREFIX + "form", result);
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
