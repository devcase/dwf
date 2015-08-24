package dwf.slack;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import dwf.slack.message.PostMessageRequest;

@Component(value="slackService")
@ConditionalOnProperty(prefix="slack", name="apitoken", matchIfMissing=false)
public class SlackServiceImpl implements SlackService {
	@Value("${slack.apitoken}")
	private String slackApiToken;
	@Value("${slack.apiurl:https://slack.com/api/}")
	private String slackApiUrl;
	

	public String getSlackApiToken() {
		return slackApiToken;
	}
	public void setSlackApiToken(String slackApiToken) {
		this.slackApiToken = slackApiToken;
	}
	public String getSlackApiUrl() {
		return slackApiUrl;
	}
	public void setSlackApiUrl(String slackApiUrl) {
		this.slackApiUrl = slackApiUrl;
	}
	
	public void postMessage(String channel, String text) {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.postForEntity(slackApiUrl + "chat.postMessage", new PostMessageRequest(slackApiToken, channel, text), String.class);
		
	}
}
