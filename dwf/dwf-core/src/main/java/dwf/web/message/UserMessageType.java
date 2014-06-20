package dwf.web.message;

public enum UserMessageType {
	INFO,
	DANGER,
	WARNING,
	SUCCESS;
	
	public String getLowerCase() {
		return this.name().toLowerCase();
	}
}
