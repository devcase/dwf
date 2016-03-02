package dwf.web.user;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.WebApplicationContext;

import dwf.persistence.validation.ValidationGroups;
import dwf.user.domain.BaseUser;
import dwf.web.controller.BaseCrudController;
import dwf.web.message.UserMessageType;

@Controller
@Scope(WebApplicationContext.SCOPE_REQUEST)
@RequestMapping("/baseUser")
public class BaseUserController extends BaseCrudController<BaseUser, Long> {

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public BaseUserController() {
		super(BaseUser.class);
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
