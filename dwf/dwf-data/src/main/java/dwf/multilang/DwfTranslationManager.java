package dwf.multilang;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transactional;

import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import dwf.activitylog.domain.UpdatedProperty;
import dwf.activitylog.service.ActivityLogService;
import dwf.multilang.domain.BaseMultilangEntity;
import dwf.multilang.domain.Translation;

@Component
@Scope(value=ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DwfTranslationManager implements TranslationManager {
	@Autowired
	private ActivityLogService activityLogService;
	@Autowired 
	private SessionFactory sessionFactory;
	
	private HashMap<Class<?>, Class<? extends Translation<?>>> translationClassCache = new HashMap<Class<?>, Class<? extends Translation<?>>>();
	private HashMap<Long, Translation<?>> translationCache = new HashMap<Long, Translation<?>>();
	private Map<Long, Translation<?>> syncMap = Collections.synchronizedMap(translationCache);
	
	
	/**
	 * generates a hash that identifies a translation, based on the entity class, entity id and language
	 * @param domain
	 * @param language
	 * @return
	 */
	private static long generateTranslationKey(BaseMultilangEntity<?> domain, String language) {
        long hash = 1;
        hash = hash * 17 + domain.getId().hashCode();
        hash = hash * 31 + domain.getClass().hashCode();
        hash = hash * 13 + language.hashCode();
        return hash;
	}
	
	
	private <D extends BaseMultilangEntity<?>> Class<? extends Translation<D>> getTranslationClass(D domain) {
		Class<D> clazz = (Class<D>) domain.getClass();
		Class<? extends Translation<D>> t;
		if(translationClassCache.containsKey(clazz)) {
			 t = (Class<? extends Translation<D>>) translationClassCache.get(clazz);
		} else {
			t = (Class<? extends Translation<D>>) domain.getTranslationClass();
			translationClassCache.put(clazz, t);
		}
		
		return t;
	}
	
	@Override
	@Transactional
	public <D extends BaseMultilangEntity<?>> void setTranslation(D domain, String property, String language, String value) {
		Assert.notNull(domain);
		Assert.notNull(language);
		Assert.notNull(domain.getId());
		long tKey = generateTranslationKey(domain, language);
		Translation<D> t = getTranslation(domain, language);
		String oldValue = null;
		if(t == null) {
			//não existe ainda
			Class<? extends Translation<D>> c = getTranslationClass(domain);
			try {
				t = c.newInstance();
				t.setLanguage(language);
				t.setParentEntity(domain);
				t.setText(new HashMap<String, String>());
				t.getText().put(property, value);
				sessionFactory.getCurrentSession().save(t);
			} catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException("Invalid translation class " + t  + " for entity " + domain);
			}
		} else {
			if(t.getText().containsKey(property)) {
				oldValue = t.getText().get(property);
			}
			t.getText().put(property, value);
		}
		UpdatedProperty up = new UpdatedProperty();
		up.setPropertyName(property);
		up.setNewValue(value);
		up.setOldValue(oldValue);
		activityLogService.logEntityUpdate(domain, Collections.singletonList(up));
		
		syncMap.put(tKey,t);
	}

	@Override
	public <D extends BaseMultilangEntity<?>> String getTranslation(D domain, String property, String language) {
		Assert.notNull(domain);
		Assert.notNull(language);
		
		Translation<D> t = getTranslation(domain, language);
		if(t == null) {
			try {
				return (String) PropertyUtils.getProperty(domain, property);
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				throw new RuntimeException("Invalid property " + property  + " for entity " + domain);
			}
		} else {
			return t.getText().get(property);
		}
	}
	
	@Override
	public <D extends BaseMultilangEntity<?>> Translation<D> getTranslation(D domain, String language) {
		Assert.notNull(domain);
		Assert.notNull(language);
		long tKey = generateTranslationKey(domain, language);
		if(translationCache.containsKey(tKey)) {
			return (Translation<D>) translationCache.get(tKey);
		} else {
			Class<? extends Translation<D>> c = getTranslationClass(domain);
			Translation<D> t = (Translation<D>) sessionFactory.getCurrentSession().createCriteria(c).add(Restrictions.eq("parentEntity", domain)).add(Restrictions.eq("language", language)).uniqueResult();
			if(t == null) {
				return null;
			} else {
				syncMap.put(tKey,t);
				return t;
			}
		}
	}
	
	public synchronized void clearCache() {
		translationClassCache = new HashMap<Class<?>, Class<? extends Translation<?>>>();
		HashMap<Long, Translation<?>> a = new HashMap<Long, Translation<?>>();
		translationCache = a;
		syncMap = Collections.synchronizedMap(a);
	}

}
