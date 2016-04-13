package dwf.serialization;

import dwf.persistence.embeddable.Address;
import dwf.persistence.embeddable.GeoPosition;

public interface View {
	public static interface Mongo  {}
	public static interface Rest {}
	public static interface RestList extends Rest {} 
	public static interface RestDetails extends Rest {}
}
