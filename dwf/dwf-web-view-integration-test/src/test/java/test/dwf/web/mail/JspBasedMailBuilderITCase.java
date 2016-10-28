package test.dwf.web.mail;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.WebApplicationContext;

import test.dwf.web.application.DwfWebRestTestApplication;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {DwfWebRestTestApplication.class})
@WebIntegrationTest()
public class JspBasedMailBuilderITCase {
	@Value("${local.server.port}")
	private int serverPort;
	@Autowired
	private WebApplicationContext wac;
	
	@Test
	public void testBuildMail() {
		TestRestTemplate restTemplate = new TestRestTemplate();
		ResponseEntity<String> responseEn = restTemplate.getForEntity("http://localhost:" + serverPort + "/jspBasedMailBuilder?locale=en_US", String.class);
		System.out.println(responseEn.getBody());
		Assert.assertTrue(responseEn.getBody().contains("English"));
		Assert.assertTrue(responseEn.getBody().contains("SUPER STRING"));
		ResponseEntity<String> responsePt = restTemplate.getForEntity("http://localhost:" + serverPort + "/jspBasedMailBuilder?locale=pt_BR", String.class);
		Assert.assertTrue(responsePt.getBody().contains("Português"));
	}
	
}
