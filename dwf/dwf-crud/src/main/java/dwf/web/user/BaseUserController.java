package dwf.web.user;

import java.util.concurrent.Callable;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;

import dwf.persistence.annotations.constraints.Password;
import dwf.persistence.validation.ValidationGroups;
import dwf.user.domain.BaseUser;
import dwf.user.domain.TokenType;
import dwf.user.service.VerificationTokenService;
import dwf.web.controller.BaseCrudController;
import dwf.web.message.UserMessageType;

@Controller
@Scope(WebApplicationContext.SCOPE_REQUEST)
@RequestMapping("/baseUser")
public class BaseUserController extends BaseCrudController<BaseUser, Long> {

	@Autowired
	private VerificationTokenService verificationTokenService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public BaseUserController() {
		super(BaseUser.class);
	}

	@PreAuthorize("hasPermission(#form.id,  'baseUser', 'save')")
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public Callable<String> save(@Validated(Default.class) final BaseUser form, final BindingResult bindingResult) {
		return new Callable<String>() {
			public String call() throws Exception {
				if(bindingResult.hasErrors()) {
					model.addAttribute(entityName, form);
					if (form.getId() == null)
						setupNavCrud(OPERATION_CREATE, null);
					else
						setupNavCrud(OPERATION_EDIT, form);
					return "/" + entityName + "/edit";
				}
				try {
					if (form.getId() == null) {
						getDAO().saveNew(form);
						addUserMessage("crud.save.new.success", UserMessageType.SUCCESS);
						verificationTokenService.generateAndSendToken(form.getEmail(), TokenType.EMAIL_CONFIRMATION);
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

	@PreAuthorize("hasPermission(#form.id, 'baseUser', 'changePassword')")
	@RequestMapping(value = "/changePassword", method=RequestMethod.POST)
	public String changePassword(@Valid ChangeUserPasswordForm form, BindingResult bindingResult) {
		if(!bindingResult.hasErrors() && !form.getConfirmPassword().equals(form.getPassword())) {
			bindingResult.addError(new FieldError("form", "confirmPassword", getMessage("message.confirmPassword.error")));
		}
		
		if(bindingResult.hasErrors()) {
			model.addAttribute(BindingResult.MODEL_KEY_PREFIX + "form", bindingResult);
			BaseUser entity = getDAO().findById(form.getId());
			model.addAttribute(entityName, entity);
			model.addAttribute("entity", entity);
			setupNavCrud(OPERATION_VIEW, entity);
			return "/baseUser/view";
		}
		BaseUser b = new BaseUser();
		b.setId(form.getId());
		b.setHashedpass(passwordEncoder.encode(form.getPassword()));
		getDAO().updateByAnnotation(b, ValidationGroups.ChangePassword.class);
		addUserMessage("message.password.change.success", UserMessageType.SUCCESS);
		return "redirect:/baseUser/view/" + form.getId();
	}
	
	public static class ChangeUserPasswordForm {
		@NotNull
		private Long id;
		@NotNull
		private String password;
		@NotNull
		private String confirmPassword;
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getPassword() {
			return password;
		}
		public void setPassword(String password) {
			this.password = password;
		}
		public String getConfirmPassword() {
			return confirmPassword;
		}
		public void setConfirmPassword(String confirmPassword) {
			this.confirmPassword = confirmPassword;
		}
		
	}
	
}
