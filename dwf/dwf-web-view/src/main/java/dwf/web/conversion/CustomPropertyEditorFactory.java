package dwf.web.conversion;

import java.beans.PropertyEditor;

import javax.servlet.http.HttpServletRequest;

public interface CustomPropertyEditorFactory {
	public Class<?>[] getTargetClasses();
	PropertyEditor getPropertyEditor(HttpServletRequest request);
}
