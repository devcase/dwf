package dwf.web.conversion;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.context.annotation.Scope;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

/**
 * Accepts 3 types of formats - time only (locale dependent), date only  (locale dependent) and ISO8601 
 * @author Hirata
 *
 */
@Component
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class DwfCustomDateEditor extends PropertyEditorSupport implements CustomPropertyEditorFactory {
	private final DateFormat timeFormat;
	private final DateFormat dateFormat;
	private final DateTimeFormatter isoFormatter;
	
	
	private final Class<?>[] TARGET_CLASSES = new Class<?>[] {Date.class};
	@Override
	public Class<?>[] getTargetClasses() {
		return TARGET_CLASSES;
	}

	@Override
	public PropertyEditor getPropertyEditor(HttpServletRequest request) {
		return this;
	}

	public DwfCustomDateEditor() {
		LocaleContext localeContext = LocaleContextHolder.getLocaleContext();
		Locale locale = localeContext.getLocale();
		
		String datePatternJava;
		String timePatternJava;
		if(locale.equals(Locale.US)) {
			datePatternJava = "MM/dd/yyyy";
			timePatternJava="hh:mm a";
		} else if(locale.equals(Locale.JAPAN) || locale.equals(Locale.CHINA) || locale.equals(Locale.KOREAN)){
			datePatternJava = "yyyy/MM/dd";
			timePatternJava="hh:mm a";
		} else {
			datePatternJava = "dd/MM/yyyy";
			timePatternJava="HH:mm";
		}
		
		this.dateFormat = new SimpleDateFormat(datePatternJava, locale);
		this.timeFormat = new SimpleDateFormat(timePatternJava, locale);
		
		isoFormatter = ISODateTimeFormat.dateTimeParser();
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
		System.out.println(this.getClass() + "==================" + text);
		if (StringUtils.isBlank(text)) {
			setValue(null);
			return;
		}
		else {
			try {
				setValue(this.timeFormat.parse(text));
				return;
			}
			catch (ParseException ex1) {
				try {
					setValue(this.dateFormat.parse(text));
					return;
				}
				catch (ParseException ex) {
					try {
						long date = isoFormatter.parseMillis(text);
						setValue(new Date(date));
						return;
					} catch (IllegalArgumentException ex2) {
						throw new IllegalArgumentException("Could not parse date: " + ex.getMessage(), ex);
					}
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
