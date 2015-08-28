package test.dwf.web.application;

import java.util.HashMap;
import java.util.Map;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import dwf.web.controller.BaseController;
import dwf.web.mail.JspBasedMailBuilder;


@Configuration
@EnableAutoConfiguration
@ComponentScan
public class DwfWebRestTestApplication {
	public static void main(String[] args) {
		SpringApplication.run(DwfWebRestTestApplication.class, args);
	}
	
	@Configuration
	@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
	static class WebSecurityConfig extends WebSecurityConfigurerAdapter {

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.authorizeRequests().anyRequest().permitAll();
		}
		
	}
	
	
	@Controller("exampleController")
	@RequestMapping("/example")
	public static class ExampleController extends BaseController {
		
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

