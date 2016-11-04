package dwf.web.conversion;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.support.RequestContextUtils;

import dwf.persistence.embeddable.Price;

@Component
@Scope(value=org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST)
public class PricePropertyEditor extends PropertyEditorSupport implements CustomPropertyEditorFactory, PropertyEditor {
	private Locale locale;

	@Override
	public Class<?>[] getTargetClasses() {
		return new Class<?>[] {Price.class};
	}

	@Override
	public PropertyEditor getPropertyEditor(HttpServletRequest request) {
		locale = RequestContextUtils.getLocale(request);
		return this;
	}


	/**
	 * Parse the Number from the given text, using the specified NumberFormat.
	 */
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (!StringUtils.hasText(text)) {
			setValue(null);
		}
		else {
			// Use default valueOf methods for parsing text.
			String[] split = text.trim().split(" ");
			if(split.length != 2) {
				throw new IllegalArgumentException("Invalid string for Cost");
			}
			Price price = new Price();
			String currencyCode = split[0].trim();
			
			if(StringUtils.startsWithIgnoreCase(currencyCode, "R$")) {
				currencyCode = "BRL";
			}
			
			price.setCurrencyCode(currencyCode);	
			try {
				price.setValueAsDouble(DecimalFormat.getInstance(locale).parse(split[1]).doubleValue());
			} catch (ParseException e) {
				throw new IllegalArgumentException("Invalid string for Cost", e);
			}
			
			setValue(price);
		}
	}

	/**
	 * Format the Number as String, using the specified NumberFormat.
	 */
	@Override
	public String getAsText() {
		Object value = getValue();
		Price orice = (Price) value;
		if (orice == null || orice.getValue() == null) {
			return "";
		} else if(orice.getCurrencyCode() != null) {
			return String.format(locale, "%s %02d", orice.getCurrency().getSymbol(locale), orice.getValue());
		} else {
			return String.format(locale, "$ %02d", orice.getValue());
		}
	}	
	

}
