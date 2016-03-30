package dwf.web.message;

import java.io.Serializable;

public class UserMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 164697474503480253L;
	private String key;
	private UserMessageType type;
	private Object[] arguments;
	public UserMessage(String key, UserMessageType type) {
		super();
		this.key = key;
		this.type = type;
	}
	
	public UserMessage(String key, UserMessageType type, Object[] arguments) {
		super();
		this.key = key;
		this.type = type;
		this.arguments = arguments;
	}


	public Object[] getArguments() {
		return arguments;
	}


	public void setArguments(Object[] arguments) {
		this.arguments = arguments;
	}


	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public UserMessageType getType() {
		return type;
	}
	public void setType(UserMessageType type) {
		this.type = type;
	}
	
}
