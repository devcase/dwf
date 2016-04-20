package dwf.persistence.embeddable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import dwf.serialization.View;

public class GeoJsonPoint {
	private final String type = "Point";
	private final double[] coordinates = new double[2];
	
	public GeoJsonPoint() {
	}
	public GeoJsonPoint(double lon, double lat) {
		setLat(lat);
		setLon(lon);
	}
	
	
	@JsonIgnore
	public double getLat() {
		return coordinates[1];
	}
	public void setLat(double lat) {
		coordinates[1] = lat;
	}
	@JsonIgnore
	public double getLon() {
		return coordinates[0];
	}
	public void setLon(double lon) {
		coordinates[0] = lon;
	}
	@JsonView({View.Rest.class, View.Mongo.class})
	public String getType() {
		return type;
	}
	@JsonView({View.Rest.class, View.Mongo.class})
	public double[] getCoordinates() {
		return coordinates;
	}

	
}
