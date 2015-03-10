package dwf.web.user;

import java.util.concurrent.Callable;

import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import dwf.user.domain.BaseUser;
import dwf.user.domain.TokenType;
import dwf.user.service.VerificationTokenService;
import dwf.web.controller.BaseCrudController;
import dwf.web.message.UserMessageType;

@Controller
@RequestMapping("/baseUser/")
public class BaseUserController extends BaseCrudController<BaseUser, Long> {

	private final VerificationTokenService verificationTokenService;
	
	@Autowired
	public BaseUserController(VerificationTokenService verificationTokenService) {
		super(BaseUser.class);
		this.verificationTokenService = verificationTokenService;
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public Callable<String> save(final BaseUser form, final BindingResult bindingResult) {
		return new Callable<String>() {
			public String call() throws Exception {
				try {
					if (form.getId() == null) {
						getDAO().saveNew(form);
						addUserMessage("crud.save.new.success", UserMessageType.SUCCESS);
						verificationTokenService.generateAndSendToken(form.getUsername(), TokenType.EMAIL_CONFIRMATION);
					} else {
						getDAO().updateByAnnotation(form);
						addUserMessage("crud.save.update.success", UserMessageType.SUCCESS);
					}
					return "redirect:/" + entityName + "/" + form.getId();
				} catch (ValidationException ex) {
					addValidationExceptionMessage(ex);
					model.addAttribute(entityName, form);
					if (form.getId() == null)
						setupNavCrud(OPERATION_CREATE, null);
					else
						setupNavCrud(OPERATION_EDIT, form);
					return "/" + entityName + "/edit";
				}
			}
		};
	}
}
