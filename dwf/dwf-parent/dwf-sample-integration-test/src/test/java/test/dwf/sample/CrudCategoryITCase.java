package test.dwf.sample;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
import dwf.sample.persistence.dao.CategoryDAO;
import dwf.sample.persistence.domain.Category;
import dwf.web.rest.spring.ParsedMapArgumentResolver.RequestParsedMap;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(profiles = { "test", "integration-test" })
@SpringApplicationConfiguration(classes = { SampleApplication.class, ITScenario1.class })
@WebIntegrationTest()
public class CrudCategoryITCase {
	@Autowired
	private WebApplicationContext wac;
	@Autowired
	private CategoryDAO categoryDAO;

	TestRestTemplate template = new TestRestTemplate();

	@Test
	public void testCategoryDAO() throws Exception {
		Category cat1 = new Category();
		cat1.setName("Value 1");
		cat1.setAdminOnly(false);
		cat1 = categoryDAO.findOrSaveNew(cat1);
		
		Category cat2 = new Category();
		cat2.setName("Value 2");
		cat2.setAdminOnly(true);
		cat2 = categoryDAO.findOrSaveNew(cat2);

		Category cat3 = new Category();
		cat3.setName("Value 3");
		cat3.setAdminOnly(true);
		cat3 = categoryDAO.findOrSaveNew(cat3);

		List<Category> searchResults1 = categoryDAO.findByFilter("name", "Value 1");
		Assert.assertTrue(searchResults1.contains(cat1));
		Assert.assertTrue(!searchResults1.contains(cat3));
		Assert.assertTrue(!searchResults1.contains(cat2));
		List<Category> searchResults2 = categoryDAO.findByFilter("name", new String[] {"Value 1", "Value 2"});
		Assert.assertTrue(searchResults2.contains(cat1));
		Assert.assertTrue(!searchResults2.contains(cat3));
		Assert.assertTrue(searchResults2.contains(cat2));

		
		Map<String, String[]> mockRequest3 = new HashMap<>();
		mockRequest3.put("name", new String[] {"Value 1", "Value 2"});
		List<Category> searchResults3 = categoryDAO.findByFilter(new RequestParsedMap(mockRequest3, "", Locale.US));
		Assert.assertTrue(searchResults3.contains(cat1));
		Assert.assertTrue(!searchResults3.contains(cat3));
		Assert.assertTrue(searchResults3.contains(cat2));

		Map<String, String[]> mockRequest4 = new HashMap<>();
		mockRequest4.put("name", new String[] {"Value 1" });
		List<Category> searchResults4 = categoryDAO.findByFilter(new RequestParsedMap(mockRequest4, "", Locale.US));
		Assert.assertTrue(searchResults4.contains(cat1));
		Assert.assertTrue(!searchResults4.contains(cat3));
		Assert.assertTrue(!searchResults4.contains(cat2));

	}
	
	@Test
	public void testAcessoCategoria() throws Exception {
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
			HtmlForm loginForm = page.getForms().get(0);
			loginForm.getInputByName("username").setValueAttribute(ITScenario1.TEST_USER_2_EMAIL);
			loginForm.getInputByName("password").setValueAttribute(ITScenario1.TEST_USER_2_PASSWORD);
			HtmlButton submit = loginForm.getOneHtmlElementByAttribute("button", "type", "submit");
			page = submit.click();
			
			Category catVisible = new Category();
			catVisible.setName("Visible!");
			catVisible.setAdminOnly(false);
			categoryDAO.findOrSaveNew(catVisible);
			
			Category catAdminOnly = new Category();
			catAdminOnly.setName("Admin Only!");
			catAdminOnly.setAdminOnly(true);
			categoryDAO.findOrSaveNew(catAdminOnly);

			//Acesso categoria visível
			page = webClient.getPage(host + "/category/" + catVisible.getId());
			page.asText().contains("Visible!");
			
			//Acesso categoria invisível para usuário normal
			try {
				page = webClient.getPage(host + "/category/" + catAdminOnly.getId());
				Assert.fail("Não deveria ser possível carregar a categoria! O PermissionEvaluator deste projeto não permitiria este acesso");
			} catch (FailingHttpStatusCodeException ex) {
				Assert.assertEquals(403, ex.getStatusCode());
			}
			
			
			
		} finally {
			webClient.close();
		}

	}
}
