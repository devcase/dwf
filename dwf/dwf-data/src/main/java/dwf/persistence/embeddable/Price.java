package dwf.persistence.embeddable;

import java.io.Serializable;
import java.util.Currency;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonView;

import dwf.serialization.View;

@Embeddable
public class Price implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8206227063669867200L;
	private Double value;
	private String currencyCode;
	
	
	public Price() {
		super();
	}
	

	public Price(Double value, String currencyCode) {
		this.value = value;
		this.currencyCode = currencyCode;
	}



	@JsonView({View.Rest.class, View.Mongo.class})
	public Double getValue() {
		return value;
	}


	public void setValue(Double value) {
		this.value = value;
	}

	@Column(length=3)
	@JsonView({View.Rest.class, View.Mongo.class})
	public String getCurrencyCode() {
		return currencyCode;
	}

	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	
	@Transient
	public Currency getCurrency() {
		if(StringUtils.isBlank(getCurrencyCode()))
			return null;
		return Currency.getInstance(getCurrencyCode());
	}

	@Override
	public String toString() {
		return new StringBuilder().append(currencyCode).append(" ").append(value).toString();
	}


	@Override
	public boolean equals(Object obj) {
		if(obj != null && obj instanceof Price) {
			return ObjectUtils.compare(((Price) obj).value, this.value) == 0 && ObjectUtils.compare(((Price) obj).currencyCode, this.currencyCode) == 0;
		}
		return false;
	}	
	
	
}
