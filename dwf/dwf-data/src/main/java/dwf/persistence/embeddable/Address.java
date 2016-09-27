package dwf.persistence.embeddable;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import dwf.serialization.View;

@Embeddable
public class Address {
	
	private String streetNumber;
	private String route;
	private String sublocality;
	private String city;
	private String state;
	private String countryCode;
	private String postalCode;
	private String premise;
	private String additionalInfo;
	
	@JsonView({View.Rest.class, View.Mongo.class})
	public String getStreetNumber() {
		return streetNumber;
	}
	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}
	@JsonView({View.Rest.class, View.Mongo.class})
	public String getRoute() {
		return route;
	}
	public void setRoute(String route) {
		this.route = route;
	}
	@JsonView({View.Rest.class, View.Mongo.class})
	public String getSublocality() {
		return sublocality;
	}
	public void setSublocality(String sublocality) {
		this.sublocality = sublocality;
	}
	@JsonView({View.Rest.class, View.Mongo.class})
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	@JsonView({View.Rest.class, View.Mongo.class})
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	@JsonView({View.Rest.class, View.Mongo.class})
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	@JsonView({View.Rest.class, View.Mongo.class})
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	@JsonView({View.Rest.class, View.Mongo.class})
	public String getAdditionalInfo() {
		return additionalInfo;
	}
	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
	@JsonView({View.Rest.class, View.Mongo.class})
	public String getPremise() {
		return premise;
	}
	public void setPremise(String premise) {
		this.premise = premise;
	}
	
	
	@Transient
	@JsonIgnore
	public String getFullAddress() {
		StringBuilder sb = new StringBuilder();
		if(StringUtils.isNotBlank(route)) {
			sb.append(route);
		}
		if(StringUtils.isNotBlank(streetNumber)) {
			sb.append(", ").append(streetNumber);
		}
		if(StringUtils.isNotBlank(premise)) {
			sb.append(", ").append(premise);
		}
		if(StringUtils.isNotBlank(sublocality)) {
			sb.append(", ").append(sublocality);
		}
		if(StringUtils.isNotBlank(city)) {
			sb.append(" - ").append(city);
		}
		if(StringUtils.isNotBlank(state)) {
			sb.append(" - ").append(state);
		}
		if(StringUtils.isNotBlank(countryCode)) {
			sb.append(" - ").append(countryCode);
		}
		if(StringUtils.isNotBlank(postalCode)) {
			sb.append(", ").append(getFormattedPostalCode());
		}
		return sb.toString();
	}
	
	/**
	 * For geocoding (ignore premise)
	 * @return
	 */
	@Transient
	@JsonIgnore
	public String getStreetAddress() {
		StringBuilder sb = new StringBuilder();
		if(StringUtils.isNotBlank(route)) {
			sb.append(route);
		}
		if(StringUtils.isNotBlank(streetNumber)) {
			sb.append(", ").append(streetNumber);
		}
		if(StringUtils.isNotBlank(sublocality)) {
			sb.append(", ").append(sublocality);
		}
		if(StringUtils.isNotBlank(city)) {
			sb.append(" - ").append(city);
		}
		if(StringUtils.isNotBlank(state)) {
			sb.append(" - ").append(state);
		}
		if(StringUtils.isNotBlank(countryCode)) {
			sb.append(" - ").append(countryCode);
		}
		if(StringUtils.isNotBlank(postalCode)) {
			sb.append(", ").append(getFormattedPostalCode());
		}
		return sb.toString();
	}
	@Override
	public String toString() {
		return this.getFullAddress();
	}
	
	@Transient
	public String getFormattedPostalCode() {
		if(postalCode  != null && postalCode.matches("\\d{8}")) {
			return postalCode.substring(0, 5) + "-" + postalCode.substring(5);
		} else if(postalCode  != null && postalCode.matches("\\d{7}")) {
				return "0" + postalCode.substring(0, 4) + "-" + postalCode.substring(4);
		} else {
			return postalCode;
		}
	}
	
}
