package dwf.persistence.utils;


import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Implementation of {@link PropertyDescriptor} without synchronized methods for 
 * @{link {@link PropertyDescriptor#getPropertyType()}, @{link {@link PropertyDescriptor#getReadMethod()} and
 * @{link {@link PropertyDescriptor#getWriteMethod()}
 * @author Hirata
 *
 */
public class NotSyncPropertyDescriptor extends java.beans.PropertyDescriptor{

	private Method readMethod;
	private Method writeMethod;
	private Class<?> propertyType;



	public NotSyncPropertyDescriptor(PropertyDescriptor original) throws IntrospectionException, IllegalAccessException, InvocationTargetException {
		super(original.getName(), original.getReadMethod(), original.getWriteMethod());
		setPropertyEditorClass(original.getPropertyEditorClass());
		this.propertyType = original.getPropertyType();
	}

	@Override
	public Method getReadMethod() {
		return this.readMethod;
	}

	@Override
	public void setReadMethod(Method readMethod) throws IntrospectionException {
		super.setReadMethod(readMethod);
		this.readMethod = readMethod;
	}

	@Override
	public Method getWriteMethod() {
		return this.writeMethod;
	}

	@Override
	public void setWriteMethod(Method writeMethod) throws IntrospectionException {
		super.setWriteMethod(writeMethod);
		this.writeMethod = writeMethod;
	}

	@Override
	public Class<?> getPropertyType() {
		return this.propertyType;
	}

}
