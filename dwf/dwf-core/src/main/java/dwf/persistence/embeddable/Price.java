package dwf.persistence.embeddable;

import java.io.Serializable;
import java.util.Currency;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

import org.apache.commons.lang3.StringUtils;

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


	@Column(name="price_value")
	public Double getValue() {
		return value;
	}


	public void setValue(Double value) {
		this.value = value;
	}

	@Column(name="price_currency_code", length=3)
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
	
	
}
