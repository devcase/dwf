package dwf.geocode.autoconfigure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;

import dwf.geocode.ConsultaCEPService;
import dwf.geocode.GeocodeService;
import dwf.geocode.GoogleMapsConsultaCEPServiceImpl;
import dwf.geocode.GoogleMapsGeocodeServiceImpl;
import dwf.geocode.ViaCEPConsultaCEPServiceImpl;

@Configuration
public class DwfGeocodeAutoConfiguration {
	
	@Configuration
	@ConditionalOnProperty(name="dwf.geocode.googlemaps.apikey")
	public static class GoogleMapsConfiguration {
		@Value("${dwf.geocode.googlemaps.apikey}")
		private String googleMapsGeocodinApiKey;
		
		@Bean
		@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
		public GeocodeService geocodeService() {
			return new GoogleMapsGeocodeServiceImpl(googleMapsGeocodinApiKey);
		}
		
		@Bean
		@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
		public ConsultaCEPService consultaCEPService() {
			return new GoogleMapsConsultaCEPServiceImpl(googleMapsGeocodinApiKey);
		}
	}
	
	@Configuration
	@AutoConfigureAfter(GoogleMapsConfiguration.class)
	@ConditionalOnMissingBean(ConsultaCEPService.class)
	public static class ViaCEPConfiguration {
		@Bean
		@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
		public ConsultaCEPService consultaCEPService() {
			return new ViaCEPConsultaCEPServiceImpl();
		}
		
	}
	
}
