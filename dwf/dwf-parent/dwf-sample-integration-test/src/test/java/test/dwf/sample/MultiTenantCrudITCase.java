package test.dwf.sample;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import dwf.sample.SampleApplication;
import dwf.sample.persistence.dao.NotebookDAO;
import dwf.sample.persistence.domain.Notebook;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = { "test", "integration-test" })
@SpringApplicationConfiguration(classes = { SampleApplication.class, ITScenario1.class })
@WebIntegrationTest()
public class MultiTenantCrudITCase {
	@Autowired
	private WebApplicationContext wac;
	@Autowired
	private NotebookDAO notebookDAO;

	TestRestTemplate template = new TestRestTemplate();

	@Test
	public void testMultiTenancy() throws Exception {
		final int localPort = Integer.parseInt(wac.getEnvironment().getProperty("local.server.port"));
		final String host = "http://localhost:" + localPort;
		
		MockMvc mockMvc = MockMvcBuilders
				.webAppContextSetup(wac)
				.apply(SecurityMockMvcConfigurers.springSecurity())
				.build();

		WebClient webClient = new WebClient();
		try {
	
			mockMvc.perform(MockMvcRequestBuilders.get("/"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("http://localhost/signin"));
	
			mockMvc.perform(MockMvcRequestBuilders.get("/signin"))
				.andExpect(status().is2xxSuccessful());
	
			//Login
			HtmlPage page = webClient.getPage(host + "/signin");
			page.getForms().get(0).getInputByName("username").setValueAttribute(ITScenario1.TEST_USER_3_EMAIL);
			page.getForms().get(0).getInputByName("password").setValueAttribute(ITScenario1.TEST_USER_3_PASSWORD);
			page = page.getForms().get(0).getOneHtmlElementByAttribute("button", "type", "submit").click();
			
			//ver lista de notebooks
			page = webClient.getPage(host + "/notebook/");
			
			//cadastrar notebook
			page = webClient.getPage(host + "/notebook/create");
			page.getForms().get(0).getInputByName("content").setValueAttribute("Notebook criado por teste automático 1");
			page = page.getForms().get(0).getOneHtmlElementByAttribute("button", "type", "submit").click();
			
			//checa se existe, e se preencheu corretamente com o e-mail do usuário logado
			Notebook notebook1 = notebookDAO.findFirstByFilter("content", "Notebook criado por teste automático 1");
			Assert.assertNotNull(notebook1);
			Assert.assertEquals(ITScenario1.TEST_USER_3_EMAIL, notebook1.getBaseUser().getEmail());
			
			//ver lista de notebooks
			page = webClient.getPage(host + "/notebook/");
			Assert.assertTrue(page.asText().contains("Notebook criado por teste automático 1"));
			
			//Login outro usuário
			page = webClient.getPage(host + "/signin");
			page.getForms().get(0).getInputByName("username").setValueAttribute(ITScenario1.TEST_USER_1_EMAIL);
			page.getForms().get(0).getInputByName("password").setValueAttribute(ITScenario1.TEST_USER_1_PASSWORD);
			page = page.getForms().get(0).getOneHtmlElementByAttribute("button", "type", "submit").click();
			
			//ver lista de notebooks
			page = webClient.getPage(host + "/notebook/");
			Assert.assertFalse(page.asText().contains("Notebook criado por teste automático 1"));
			
			//cadastrar notebook 2
			page = webClient.getPage(host + "/notebook/create");
			page.getForms().get(0).getInputByName("content").setValueAttribute("Notebook criado por teste automático 2");
			page = page.getForms().get(0).getOneHtmlElementByAttribute("button", "type", "submit").click();
			
			//ver lista de notebooks
			page = webClient.getPage(host + "/notebook/");
			Assert.assertTrue(page.asText().contains("Notebook criado por teste automático 2"));
			Assert.assertFalse(page.asText().contains("Notebook criado por teste automático 1"));
			
			//tentar acessar o notebook1 de outro usuário
			page = webClient.getPage(host + "/notebook/" + notebook1.getId());
			Assert.assertFalse(page.asText().contains("Notebook criado por teste automático 1"));
			
		} finally {
			webClient.close();
		}

	}
}
