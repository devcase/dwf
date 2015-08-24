package dwf.slack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component(value="slackService")
@ConditionalOnProperty(prefix="slack", name="apitoken", matchIfMissing=false)
public class SlackServiceImpl implements SlackService {
	private Log log = LogFactory.getLog(getClass());
	
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
		MultiValueMap<String, Object> request = new LinkedMultiValueMap<String, Object>();
		request.add("token", slackApiToken);
		request.add("text", text);
		request.add("channel", channel);
		
		ResponseEntity<String> resp = restTemplate.postForEntity(slackApiUrl + "chat.postMessage", request, String.class);
		log.info("Response got from SLACK: " + resp);
	}
}
