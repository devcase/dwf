package dwf.web.user;

import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.WebApplicationContext;

import dwf.user.service.BaseUserService;
import dwf.user.service.VerificationTokenService;
import dwf.web.controller.BaseController;
import dwf.web.message.UserMessageType;

@Controller
@Scope(WebApplicationContext.SCOPE_REQUEST)
@ConditionalOnClass(value=VerificationTokenService.class)
public class EmailConfirmationController extends BaseController {

	private final VerificationTokenService verificationTokenService;

	@Autowired
	public EmailConfirmationController(VerificationTokenService verificationTokenService,
			BaseUserService baseUserService) {
		this.verificationTokenService = verificationTokenService;
	}

	@RequestMapping("emailConfirmation/{token}")
	public String emailConfirmation(@PathVariable String token) {
		try {
			verificationTokenService.confirmToken(token);
		} catch (ValidationException e) {
			addUserMessage("dwf.user.registration.confirmation.error", UserMessageType.DANGER);
			return "confirmation";
		}
		
		addUserMessage("dwf.user.registration.confirmation.success", UserMessageType.SUCCESS);
		return "confirmation";
	}
}
