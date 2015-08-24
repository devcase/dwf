package dwf.slack.message;

import java.io.Serializable;

public class PostMessageRequest implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2709760333797211364L;
	
	private String token;
	private String channel;
	private String text;
	
	public PostMessageRequest() {
		super();
	}
	public PostMessageRequest(String token, String channel, String text) {
		super();
		this.token = token;
		this.channel = channel;
		this.text = text;
	}


	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	
}
