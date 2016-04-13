package dwf.geocode;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.model.GeocodingResult;

import dwf.persistence.embeddable.Address;
import dwf.persistence.embeddable.GeoPosition;

public class GoogleMapsGeocodeServiceImpl implements GeocodeService {
	
	private GeoApiContext context;
	
	public GoogleMapsGeocodeServiceImpl(String googleMapsApiKey) {
		super();
		context = new GeoApiContext().setApiKey(googleMapsApiKey);
	}
	
	

	@Override
	public GeoPosition[] geocode(Address address) {
		return geocode(address.getStreetAddress());
	}

	@Override
	public GeoPosition[] geocode(String address) {
		GeocodingApiRequest req = GeocodingApi.geocode(context, address);
		GeocodingResult[] result = req.awaitIgnoreError(); //síncrono
		if(result == null || result.length == 0) {
			//nenhum resultado
			return new GeoPosition[0];
		} else {
			GeoPosition[] ret = new GeoPosition[result.length];
			for (int i = 0; i < result.length; i++) {
				GeocodingResult geocodingResult = result[i];
				ret[i] = new GeoPosition(geocodingResult.geometry.location.lat, geocodingResult.geometry.location.lng);
			}
			return ret;
		}
	}
	
	
}
