package dwf.multilang;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

import org.hibernate.annotations.JoinColumnOrFormula;
import org.hibernate.annotations.JoinFormula;

import dwf.persistence.domain.BaseEntity;

@MappedSuperclass
public abstract class MultilangEntity<ID extends Serializable> extends BaseEntity<ID> {
	private String locale;
	private List<Translation> translations;

	@Column(length=6)
	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}


	@OneToMany()
	@JoinColumnOrFormula(formula=@JoinFormula(value="entity_id", referencedColumnName="id"))
	public List<Translation> getTranslations() {
		return translations;
	}

	public void setTranslations(List<Translation> translations) {
		this.translations = translations;
	}

	
	
}
