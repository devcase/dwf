package dwf.user.domain;

public enum TokenType {
	EMAIL_CONFIRMATION("emailConfirmation"),
	RESET_PASSWORD("resetPassword"),
	CHANGE_PASSWORD("savePassword");
	
	private final String url;
	
	private TokenType(String url) {
		this.url = url;
	}
	
	public String getUrl() {
		return url;
	}
}
