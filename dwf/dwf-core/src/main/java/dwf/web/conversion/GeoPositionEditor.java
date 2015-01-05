package dwf.web.conversion;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import dwf.utils.GeoPosition;

@Component
@Scope(value=org.springframework.web.context.WebApplicationContext.SCOPE_REQUEST)
public class GeoPositionEditor extends PropertyEditorSupport implements CustomPropertyEditorFactory, PropertyEditor {

	@Override
	public Class<?>[] getTargetClasses() {
		return new Class<?>[] {GeoPosition.class};
	}

	@Override
	public PropertyEditor getPropertyEditor(HttpServletRequest request) {
		return this;
	}



	/**
	 * Parse the Number from the given text, using the specified NumberFormat.
	 */
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (!StringUtils.hasText(text)) {
			// Treat empty String as null value.
			setValue(null);
		}
		else {
			// Use default valueOf methods for parsing text.
			setValue(new GeoPosition(text));
		}
	}

	/**
	 * Format the Number as String, using the specified NumberFormat.
	 */
	@Override
	public String getAsText() {
		Object value = getValue();
		if (value == null) {
			return "";
		}
		else {
			// Use toString method for rendering value.
			return value.toString();
		}
	}	
	

}
