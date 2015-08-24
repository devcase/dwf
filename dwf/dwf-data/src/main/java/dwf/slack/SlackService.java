package dwf.slack;

public interface SlackService {
	void postMessage(String channel, String text);
}
