package dwf.slack;

public interface SlackService {
	
	/**
	 * @see {@link https://api.slack.com/methods/chat.postMessage}
	 */
	void postMessage(String channel, String text, String... extraArgs);
}
