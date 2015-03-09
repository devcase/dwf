package dwf.user.domain;

import org.hibernate.validator.constraints.NotEmpty;

public class ResetPasswordBean {

	@NotEmpty
	private String newPassword;
	
	@NotEmpty
	private String newPasswordConfirmation;

	public ResetPasswordBean() {
	}
	
	public ResetPasswordBean(String newPassword, String newPasswordConfirmation) {
		this.newPassword = newPassword;
		this.newPasswordConfirmation = newPasswordConfirmation;
	}
	
	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getNewPasswordConfirmation() {
		return newPasswordConfirmation;
	}

	public void setNewPasswordConfirmation(String newPasswordConfirmation) {
		this.newPasswordConfirmation = newPasswordConfirmation;
	}
	
	public boolean isValidConfirmation() {
		return newPassword != null && newPasswordConfirmation != null
				&& newPassword.equals(newPasswordConfirmation);
	}
}
