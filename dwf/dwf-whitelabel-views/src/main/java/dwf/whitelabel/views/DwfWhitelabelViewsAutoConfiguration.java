package dwf.whitelabel.views;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.ParameterizableViewController;

import dwf.web.sitemesh.SitemeshView;

@Configuration
@ConditionalOnWebApplication
@ComponentScan
public class DwfWhitelabelViewsAutoConfiguration {

	static class DefaultHomeConfiguration {
		@Bean(name = "homeView")
		public View homeView() {
			return new SitemeshView("/WEB-INF/jsp/home.jsp");
		}
		
		@Bean
		public ParameterizableViewController homeViewController(View homeView) {
			ParameterizableViewController c = new ParameterizableViewController();
			c.setView(homeView);
			return c;
		}
		
		@Bean
		public SimpleUrlHandlerMapping homeHandlerMapping(ParameterizableViewController homeViewController) {
			Map<String, Object> urlMap = new LinkedHashMap<String, Object>();
			urlMap.put("/home", homeViewController);
			urlMap.put("/", homeViewController);
			SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
			handlerMapping.setOrder(0);
			handlerMapping.setUrlMap(urlMap);
			return handlerMapping;
		}
	}
}
