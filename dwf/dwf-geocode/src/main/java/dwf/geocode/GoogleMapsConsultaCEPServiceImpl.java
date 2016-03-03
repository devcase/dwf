package dwf.geocode;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.GeocodingApiRequest;
import com.google.maps.model.AddressComponent;
import com.google.maps.model.AddressComponentType;
import com.google.maps.model.GeocodingResult;

import dwf.persistence.embeddable.Address;

public class GoogleMapsConsultaCEPServiceImpl implements ConsultaCEPService {
	private GeoApiContext context;

	public GoogleMapsConsultaCEPServiceImpl(String googleMapsApiKey) {
		super();
		context = new GeoApiContext().setApiKey(googleMapsApiKey);
	}

	@Override
	public Address[] consultaCEP(String cep) {
		GeocodingApiRequest req = GeocodingApi.geocode(context, MessageFormat.format("CEP {0}, Brasil", cep));
		GeocodingResult[] result = req.awaitIgnoreError(); //síncrono
		if(result == null || result.length == 0) {
			//nenhum resultado
			return null;
		} else {
			List<Address> ret = new ArrayList<Address>(result.length);
			for (int i = 0; i < result.length; i++) {
				GeocodingResult geocodingResult = result[i];
				Address address = new Address();
				for (AddressComponent addressComponent : geocodingResult.addressComponents) {
					for (AddressComponentType addressComponentType : addressComponent.types) {
						switch(addressComponentType) {
						case POSTAL_CODE:
							address.setPostalCode(addressComponent.longName);
							break;
						case SUBLOCALITY:
							address.setSublocality(addressComponent.longName);
							break;
						case ADMINISTRATIVE_AREA_LEVEL_2:
							address.setCity(addressComponent.longName);
							break;
						case ADMINISTRATIVE_AREA_LEVEL_1:
							address.setState(addressComponent.shortName);
							break;
						case COUNTRY:
							address.setCountryCode(addressComponent.shortName);
							break;
						}
					}
				}
				ret.add(address);
			}
			return ret.toArray(new Address[ret.size()]);
		}
	}
}
