package test.dwf.web.mail;

import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import test.dwf.web.DwfWebRestTestApplication;
import dwf.web.mail.JspBasedMailBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {DwfWebRestTestApplication.class, JspBasedMailBuilderITCase.IntegrationTestController.class})
@WebIntegrationTest()
public class JspBasedMailBuilderITCase {
	@Value("${local.server.port}")
	private int serverPort;
	
	@Test
	public void testBuildMail() {
		TestRestTemplate restTemplate = new TestRestTemplate();
		ResponseEntity<String> responseEn = restTemplate.getForEntity("http://localhost:" + serverPort + "/jspBasedMailBuilder?locale=en_US", String.class);
		Assert.assertTrue(responseEn.getBody().contains("English"));
		Assert.assertTrue(responseEn.getBody().contains("SUPER STRING"));
		ResponseEntity<String> responsePt = restTemplate.getForEntity("http://localhost:" + serverPort + "/jspBasedMailBuilder?locale=pt_BR", String.class);
		Assert.assertTrue(responsePt.getBody().contains("Português"));
	}
	
	
	@Controller
	public static class IntegrationTestController {
		@Autowired 
		private JspBasedMailBuilder jspBasedMailBuilder;
		
		
		@RequestMapping("/jspBasedMailBuilder")
		@ResponseBody
		public String executedOnServer(HttpServletRequest request) throws Exception {
			
			Map<String, Object> model = new HashMap<String, Object>();
			model.put("testmodelattribute", "SUPER STRING");
			MimeMessage mimeMessage = jspBasedMailBuilder.buildMimeMessage("arbitraryfrom@devcase.com.br", new String[] {"arbitraryto@devcase.com.br" } , "Arbitrary Subject", "/WEB-INF/jsp/mailbody.jsp", model);
			return mimeMessage.getContent().toString();
		}
	}
	
}
