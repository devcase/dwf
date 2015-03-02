package dwf.user.domain;

import org.hibernate.validator.constraints.NotEmpty;

public class ChangePasswordBean {

	@NotEmpty
	private String currentPassword;
	
	@NotEmpty
	private String newPassword;
	
	@NotEmpty
	private String newPasswordConfirmation;

	public String getCurrentPassword() {
		return currentPassword;
	}

	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
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
		return newPassword != null && newPasswordConfirmation != null && currentPassword!= null
				&& newPassword.equals(newPasswordConfirmation) && !currentPassword.equals(newPassword);
	}
}
