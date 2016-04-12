package dwf.persistence.embeddable;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
	public String getType() {
		return type;
	}
	public double[] getCoordinates() {
		return coordinates;
	}

	
}
