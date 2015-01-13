package dwf.web.spring;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletRequest;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.web.servlet.mvc.method.annotation.ExtendedServletRequestDataBinder;

/**
 * Trata de casos como 
 * @author Hirata
 * 
 */
public class DwfServletRequestDataBinder extends ExtendedServletRequestDataBinder {

	public DwfServletRequestDataBinder(Object target, String objectName) {
		super(target, objectName);
	}

	public DwfServletRequestDataBinder(Object target) {
		super(target);
	}

	/* (non-Javadoc)
	 * @see org.springframework.web.servlet.mvc.method.annotation.ExtendedServletRequestDataBinder#addBindValues(org.springframework.beans.MutablePropertyValues, javax.servlet.ServletRequest)
	 */
	@Override
	protected void addBindValues(MutablePropertyValues mpvs, ServletRequest request) {
		super.addBindValues(mpvs, request);
		
		transformArraysToIndexedValues(mpvs);
		ignoreEmptyEntityId(mpvs, request);
		
	}

	/**
	 * Transforma um parâmetro do tipo array com [] no nome para vários parâmetros
	 * indexados.
	 * ex: categories[].id={1,2} vira categories[0].id=1 e categories[1].id=2
	 * @param mpvs
	 */
	private void transformArraysToIndexedValues(MutablePropertyValues mpvs) {
		Set<String> renamedList = new HashSet<String>();
		PropertyValue[] values = mpvs.getPropertyValues();
		for (int i = 0; i < values.length; i++) {
			PropertyValue currentPV = values[i];
			if(!renamedList.contains(currentPV.getName()) && currentPV.getName().contains("[]")) {
				final String originalName = currentPV.getName();
				renamedList.add(originalName);
				
				if(currentPV.getValue() instanceof String[]) {
					mpvs.removePropertyValue(currentPV);
					
					//veio mais de um
					int index = 0;
					for(String value : (String[]) currentPV.getValue()) {
						mpvs.addPropertyValue(originalName.replace("[]", "[" + (index++) + "]"), value);
					}
					
				} else {
					//veio um só
					int index = 0;
					PropertyValue renamed = new PropertyValue(originalName.replace("[]", "[" + (index++) + "]"), currentPV.getValue());
					mpvs.removePropertyValue(currentPV);
					mpvs.addPropertyValue(renamed);
					
					//check for other values with the same name
					for(int i2 = i+1; i2<values.length; i2++) {
						if(originalName.equals(values[i2].getName())) {
							mpvs.removePropertyValue(values[i2]);
							mpvs.addPropertyValue(originalName.replace("[]", "[" + (index++) + "]"), values[i2].getValue());
						}
					}
				}
			}
		}
	}

	/**
	 * Remove parâmetros com nome {alguma coisa}.id. (Exemplo: combos de seleção de entidades que enviam {nome propriedade}.id vazio). 
	 * O DAO não vai funcionar corretamente nestes casos.
	 * @param mpvs
	 * @param request
	 */
	private void ignoreEmptyEntityId(MutablePropertyValues mpvs, ServletRequest request) {
		
		PropertyValue[] values = mpvs.getPropertyValues();
		for (int i = 0; i < values.length; i++) {
			PropertyValue currentPV = values[i];
			final String originalName = currentPV.getName();
			if(originalName.endsWith(".id")) {
				if(currentPV.getValue() == null || "".equals(currentPV.getValue())) {
					//encontrei!
					mpvs.removePropertyValue(currentPV);
				}
			}
		}
	
	}
	

}
