package dwf.persistence.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import dwf.persistence.annotations.ExcludeFromSerialization;
import dwf.persistence.annotations.NotEditableProperty;

@MappedSuperclass
@Access(AccessType.PROPERTY)
public abstract class BaseEntity<ID extends Serializable> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6441974999647599671L;

	private ID id;
	
	@ExcludeFromSerialization
	private boolean enabled = true;
	@ExcludeFromSerialization
	private Date creationTime;
	@ExcludeFromSerialization
	private Date updateTime;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	@NotEditableProperty()
	public ID getId() {
		return id;
	}

	public void setId(ID id) {
		this.id = id;
	}
	

	@Column(name="enabled",nullable=false)
	@NotEditableProperty()
	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		else if(obj == null)
			return false;
		else if (this.id == null)
			return false;
		else if(obj instanceof BaseEntity) {
			BaseEntity<?> other = (BaseEntity<?>) obj;
			if(this.id == null || other.id == null)
				return false;
			return this.id.equals(other.id);
		} else {
			return false;
		}
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=false)
	@NotEditableProperty()
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateDate) {
		this.updateTime = updateDate;
	}
	
	/**
	 * @return the creationTime
	 */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable=false, updatable=false)
	@NotEditableProperty()
	public Date getCreationTime() {
		return creationTime;
	}

	/**
	 * @param creationTime the creationTime to set
	 */
	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	/**
	 * Override this method if this entity has a parent. For instance - a shopping cart item's parent
	 * should be the shopping cart itself. Used by the activityLog - a entity's activityLog will
	 * show it's children's activities.
	 * @return
	 */
	@Transient
	public BaseEntity<?> getParent() {
		return null;
	}
	
	/**
	 * String used by activity log, toString and autocomplete
	 * @return
	 */
	protected abstract String displayText();
	
	@Transient
	public final String getDisplayText(){
		return displayText();
	}
	
	@Column(length=1000, name="autocompletetext")
	@NotEditableProperty()
	public String getAutocompleteText() {
		String text = getDisplayText();
		if(text != null && text.length() > 1000)
			return text.substring(0, 1000);
		return text;
	}
	
	public void setAutocompleteText(String autocompleteText) {
		//do nothing
	}
	
	
	@Override
	public String toString() {
		
		String s = getDisplayText();
		if(s == null)
			return "";
		else {
			return s;
		}
	}

}