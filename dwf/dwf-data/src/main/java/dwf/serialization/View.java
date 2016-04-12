package dwf.serialization;

import dwf.persistence.embeddable.Address;
import dwf.persistence.embeddable.GeoPosition;

public interface View {
	public static interface Summary {}
	public static interface Detail extends Summary {}
	
	public static interface Private extends Detail {}
	public static interface Mongo extends Address.AddressJsonView, GeoPosition.GeoPositionJsonView {}
	public static interface RestList {}
	public static interface RestDetails {}
}
