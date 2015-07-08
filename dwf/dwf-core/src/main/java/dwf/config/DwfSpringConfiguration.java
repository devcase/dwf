package dwf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Deprecated
class DwfSpringConfiguration {
//
//	@Bean
//	public ViewResolver getViewResolver() {
//		InternalResourceViewResolver vr =  new InternalResourceViewResolver();
//		vr.setPrefix("/WEB-INF/jsp/");
//		vr.setSuffix(".jsp");
//		vr.setViewClass(JstlView.class);
//		return vr;
//	}
//	
//	 @Bean
//	 public PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
//	    String activeProfile = System.getProperty("spring.profiles.active");
//	    String propertiesFilename = "application-" + activeProfile + ".properties";
//	    PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
//	    configurer.setLocation(new ClassPathResource(propertiesFilename));
//
//	    return configurer;
//	}
}
