package dwf.multilang.domain;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.MapKey;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonView;

import dwf.persistence.annotations.NotEditableProperty;
import dwf.persistence.domain.BaseEntity;
import dwf.serialization.View;

@MappedSuperclass
public abstract class BaseMultilangEntity<T extends Translation<?>> extends BaseEntity<Long> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7754202986115531680L;
	protected Map<String, T> translations;
	protected String name;
	
	@OneToMany(mappedBy="parentEntity")
	@MapKey(name="language")
	@NotEditableProperty
	public Map<String, T> getTranslations() {
		return translations;
	}

	public void setTranslations(Map<String, T> translations) {
		this.translations = translations;
	}
	
	@Transient
	public Class<T> getTranslationClass() {
		Class classefilha = getClass();
		while(!BaseMultilangEntity.class.equals(classefilha.getSuperclass())) {
			classefilha = classefilha.getSuperclass();
		}
		return (Class<T>)((java.lang.reflect.ParameterizedType) classefilha.getGenericSuperclass()).getActualTypeArguments()[0];
	}

	@NotEmpty
	@Column(length=400)
	@JsonView(View.Summary.class)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	protected String displayText() {
		return name;
	}
	
	

}
