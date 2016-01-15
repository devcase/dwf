package dwf.web.crud.security;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.expression.method.ExpressionBasedAnnotationAttributeFactory;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.prepost.PreInvocationAttribute;
import org.springframework.stereotype.Component;

import dwf.security.CustomMethodSecurityMetadataSource;
import dwf.web.controller.BaseCrudController;

/**
 * Define segurança para chamadas a métodos edit, view, save e delete de controllers
 * que extendem BaseCrudController - delega para o PermissionEvaluator da aplicação
 * @author hirata
 *
 */
@Component
public class CrudMethodSecurityMetadataSource extends CustomMethodSecurityMetadataSource {

	private ExpressionBasedAnnotationAttributeFactory attributeFactory; 

	@Autowired
	public CrudMethodSecurityMetadataSource(MethodSecurityExpressionHandler expressionHandler) {
		super();
		this.attributeFactory = new ExpressionBasedAnnotationAttributeFactory (expressionHandler);
	}
	
	@Override
	public Collection<ConfigAttribute> getAttributes(Method method, Class<?> targetClass) {
		if(BaseCrudController.class.isAssignableFrom(targetClass)) {
			String[] defaultCrudMethods = new String[] { "edit", "view", "save", "delete" };
			try {
				BaseCrudController mockupInstance = (BaseCrudController) targetClass.newInstance();
				String entityName = mockupInstance.getEntityName();
				for (String methodName : defaultCrudMethods) {
					if(method.getName().equals(methodName)) {
						PreInvocationAttribute attribute = attributeFactory.createPreInvocationAttribute(null, null, "hasPermission(" + ("save".equals(methodName) ? "#form.id" : "#id") + ",  '" + entityName + "', '" + methodName + "')");
						return Collections.singleton(attribute);
					}
				}
			} catch (InstantiationException e) {
				throw new RuntimeException(e);
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}
		return null;
	}

	@Override
	public Collection<ConfigAttribute> getAllConfigAttributes() {
		return null;
	}
	
	

}
