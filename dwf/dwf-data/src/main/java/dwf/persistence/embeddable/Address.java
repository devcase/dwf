package dwf.persistence.embeddable;

import javax.persistence.Embeddable;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

@Embeddable
public class Address {
	public static interface AddressJsonView{}
	
	private String streetNumber;
	private String route;
	private String sublocality;
	private String city;
	private String state;
	private String countryCode;
	private String postalCode;
	private String additionalInfo;
	@JsonView(AddressJsonView.class)
	public String getStreetNumber() {
		return streetNumber;
	}
	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}
	@JsonView(AddressJsonView.class)
	public String getRoute() {
		return route;
	}
	public void setRoute(String route) {
		this.route = route;
	}
	@JsonView(AddressJsonView.class)
	public String getSublocality() {
		return sublocality;
	}
	public void setSublocality(String sublocality) {
		this.sublocality = sublocality;
	}
	@JsonView(AddressJsonView.class)
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	@JsonView(AddressJsonView.class)
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	@JsonView(AddressJsonView.class)
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	@JsonView(AddressJsonView.class)
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}
	@JsonView(AddressJsonView.class)
	public String getAdditionalInfo() {
		return additionalInfo;
	}
	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
	
	@Transient
	@JsonIgnore
	public String getFullAddress() {
		StringBuilder sb = new StringBuilder();
		if(StringUtils.isNotBlank(route)) {
			sb.append(route);
		}
		if(StringUtils.isNotBlank(route)) {
			sb.append(", ").append(streetNumber);
		}
		if(StringUtils.isNotBlank(additionalInfo)) {
			sb.append(", ").append(additionalInfo);
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
			sb.append(", ").append(postalCode);
		}
		return sb.toString();
	}
	
	/**
	 * For geocoding (ignore additional info)
	 * @return
	 */
	@Transient
	@JsonIgnore
	public String getStreetAddress() {
		StringBuilder sb = new StringBuilder();
		if(StringUtils.isNotBlank(route)) {
			sb.append(route);
		}
		if(StringUtils.isNotBlank(route)) {
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
			sb.append(", ").append(postalCode);
		}
		return sb.toString();
	}
	@Override
	public String toString() {
		return this.getFullAddress();
	}
	
	
}
