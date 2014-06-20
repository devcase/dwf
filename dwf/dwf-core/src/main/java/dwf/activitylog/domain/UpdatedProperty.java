package dwf.activitylog.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.StringUtils;

@Embeddable
public class UpdatedProperty {
	private String propertyName;
	private String oldValue;
	private String newValue;
	@Column(length=200)
	public String getOldValue() {
		return oldValue;
	}
	public void setOldValue(String oldValue) {
		if(oldValue != null) oldValue = StringUtils.abbreviate(oldValue, 200);
		this.oldValue = oldValue;
	}
	@Column(length=200)
	public String getNewValue() {
		return newValue;
	}
	public void setNewValue(String newValue) {
		if(newValue != null) newValue = StringUtils.abbreviate(newValue, 200);
		this.newValue = newValue;
	}
	@Column(length=200)
	public String getPropertyName() {
		return propertyName;
	}
	public void setPropertyName(String propertyName) {
		if(propertyName != null) propertyName = StringUtils.abbreviate(propertyName, 200);
		this.propertyName = propertyName;
	}
	
	
}
