package dwf.web.conversion;

import java.beans.PropertyEditor;

import javax.servlet.ServletRequest;

public interface CustomPropertyEditorFactory {
	public Class<?>[] getTargetClasses();
	PropertyEditor getPropertyEditor(ServletRequest request);
}
