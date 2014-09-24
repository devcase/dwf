package dwf.web;

import java.beans.PropertyEditorSupport;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;



/**
 * Accepts two types of formats - time only and date only 
 * @author Hirata
 *
 */
public class DwfCustomDateEditor extends PropertyEditorSupport {
	private final DateFormat timeFormat;
	private final DateFormat dateFormat;
	
	public DwfCustomDateEditor(Locale locale) {
		String datePatternJava;
		if(locale.equals(Locale.US)) {
			datePatternJava = "MM/dd/yyyy";
		} else if(locale.equals(Locale.JAPAN) || locale.equals(Locale.CHINA) || locale.equals(Locale.KOREAN)){
			datePatternJava = "yyyy/MM/dd";
		} else {
			datePatternJava = "dd/MM/yyyy";
		}
		
		this.dateFormat = new SimpleDateFormat(datePatternJava);
		this.timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, locale);
	}
	
	public static DateFormat createDateFormat(Locale locale) {
		String datePatternJava;
		if(locale.equals(Locale.US)) {
			datePatternJava = "MM/dd/yyyy";
		} else if(locale.equals(Locale.JAPAN) || locale.equals(Locale.CHINA) || locale.equals(Locale.KOREAN)){
			datePatternJava = "yyyy/MM/dd";
		} else {
			datePatternJava = "dd/MM/yyyy";
		}
		return new SimpleDateFormat(datePatternJava);
	}

	/**
	 * Parse the Date from the given text, using the specified DateFormat.
	 */
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (StringUtils.isBlank(text)) {
			setValue(null);
		}
		else {
			try {
				setValue(this.timeFormat.parse(text));
			}
			catch (ParseException ex1) {
				try {
					setValue(this.dateFormat.parse(text));
				}
				catch (ParseException ex) {
					throw new IllegalArgumentException("Could not parse date: " + ex.getMessage(), ex);
				}
			}
		}
	}

	/**
	 * Format the Date as String, using the specified DateFormat.
	 */
	@Override
	public String getAsText() {
		Date value = (Date) getValue();
		
		if(value != null && value.getTime() <= 24*60*60*1000) {
			return timeFormat.format(value);
		}
		return (value != null ? this.dateFormat.format(value) : "");
	}
}
