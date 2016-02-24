package dwf.geocode.autoconfigure;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import dwf.geocode.GeocodeService;
import dwf.geocode.GoogleMapsGeocodeServiceImpl;

@Configuration
@ConditionalOnProperty(name="dwf.geocode.googlemaps.apikey")
public class DwfGeocodeAutoConfiguration {
	
	@Value("${dwf.geocode.googlemaps.apikey}")
	private String googleMapsGeocodinApiKey;
	
	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
	public GeocodeService geocodeService() {
		return new GoogleMapsGeocodeServiceImpl(googleMapsGeocodinApiKey);
	}
}
