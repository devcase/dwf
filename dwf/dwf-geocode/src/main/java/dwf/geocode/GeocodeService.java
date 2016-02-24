package dwf.geocode;

import dwf.persistence.embeddable.Address;
import dwf.persistence.embeddable.GeoPosition;

public interface GeocodeService {
	GeoPosition[] geocode(Address address);

}
