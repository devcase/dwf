package dwf.persistence.embeddable;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import org.springframework.util.NumberUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;

import dwf.serialization.View;

@Embeddable
public class GeoPosition implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7664577901488302542L;
	private Double lat;
	private Double lon;
	
	public GeoPosition() {
		super();
	}
	
	public GeoPosition(Double lat, Double lon) {
		super();
		this.lat = lat;
		this.lon = lon;
	}
	public GeoPosition(String posString) {
		// Use default valueOf methods for parsing text.
		String[] split = posString.split(",");
		if(split.length != 2) {
			throw new IllegalArgumentException("Invalid string for GeoPosition");
		}
		this.setLat(NumberUtils.parseNumber(split[0].trim(), Double.class));
		this.setLon(NumberUtils.parseNumber(split[1].trim(), Double.class));
	}

	public GeoPosition(float lat, float lon) {
		super();
		this.lat = Double.valueOf((double) lat);
		this.lon = Double.valueOf((double) lon);
	}
	
	@JsonView({View.Rest.class, View.Mongo.class})
	public Double getLat() {
		return lat;
	}
	public void setLat(Double lat) {
		this.lat = lat;
	}
	@JsonView({View.Rest.class, View.Mongo.class})
	public Double getLon() {
		return lon;
	}
	public void setLon(Double lon) {
		this.lon = lon;
	}
	@Transient
	@JsonIgnore
	public Double getLng() {
		return getLon();
	}
	@Override
	public String toString() {
		return new StringBuilder().append(lat).append(", ").append(lon).toString();
	}
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		else if(obj instanceof GeoPosition) {
			return Objects.equals(lat, ((GeoPosition) obj).getLat()) && Objects.equals(lon, ((GeoPosition) obj).getLon());
		} else {
			return false;
		}
	}
	
	@JsonIgnore
	private static DecimalFormat decimalFormat = new DecimalFormat("00.0000000000", new DecimalFormatSymbols(Locale.US));
	
	public String toGoogleMapsLatLng() {
		return "{lat:".concat(decimalFormat.format(lat)).concat(",lng:").concat(decimalFormat.format(lon)).concat("}");
	}

}
