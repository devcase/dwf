package dwf.multilang;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Transient;

import dwf.persistence.domain.BaseEntity;

/**
 * Represents a translation for a field
 * @author Hirata
 *
 */
@Entity
public class Translation extends BaseEntity<Long> {
	private String locale;
	private String value;
	private String fieldName;
	private Long entityId;
	private String entityName;
	
	
	public String getTranslationKey() {
		return new StringBuilder().append(entityName).append('.').append(entityId).append('.').append(fieldName).append('.').append(locale).toString();
	}
	public void setTranslationKey(String key) {
		//no-op
	}
	
	
	public String getLocale() {
		return locale;
	}
	public void setLocale(String locale) {
		this.locale = locale;
	}
	@Column(length=3000)
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	@Override
	protected String displayText() {
		return value;
	}
	public Long getEntityId() {
		return entityId;
	}
	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	
}
