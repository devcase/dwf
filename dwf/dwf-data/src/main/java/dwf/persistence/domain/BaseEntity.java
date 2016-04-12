package dwf.persistence.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.jongo.marshall.jackson.oid.MongoId;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import dwf.persistence.annotations.NotEditableProperty;
import dwf.serialization.View;
import dwf.utils.SearchstringUtils;

@MappedSuperclass
@Access(AccessType.PROPERTY)
@JsonIgnoreProperties({"parent", "autocompleteText"})
public abstract class BaseEntity<ID extends Serializable> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6441974999647599671L;
	private ID id;
	
	private boolean enabled = true;
	private Date creationTime;
	private Date updateTime;

	@Id 
	@MongoId
	@GeneratedValue(generator="baseEntityIdGenerator")
	@GenericGenerator(name="baseEntityIdGenerator", strategy="dwf.persistence.domain.BaseEntityIdGenerator")
	@NotEditableProperty()
	@JsonView({View.RestDetails.class, View.RestList.class, View.Mongo.class, View.Summary.class})
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
	@JsonView(View.Summary.class)
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
	@JsonIgnore
	public final String getDisplayText(){
		return displayText();
	}
	
	@Column(length=1000, name="autocompletetext")
	@NotEditableProperty()
	@JsonIgnore
	public String getAutocompleteText() {
		String text = autocompleteText();
		return SearchstringUtils.prepareForSearch(text, 1000);
	}
	
	public void setAutocompleteText(String autocompleteText) {
		//do nothing
	}
	
	/**
	 * Subclasses may override it here.
	 * @return
	 */
	protected String autocompleteText() {
		return displayText();
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
