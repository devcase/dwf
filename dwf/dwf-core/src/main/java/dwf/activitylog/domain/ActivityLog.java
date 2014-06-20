package dwf.activitylog.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import dwf.user.domain.User;

@Entity
@Table(name=ActivityLog.TABLE_NAME, indexes = {
		@Index(name = "ix_" + ActivityLog.TABLE_NAME + "_entity", columnList = "entityid, entityname"),
		@Index(name = "ix_" + ActivityLog.TABLE_NAME + "_parententity", columnList = "parententityid, parententityname")
		})
@Access(AccessType.PROPERTY)
public class ActivityLog  {
	protected static final String TABLE_NAME = "lo_activity";
	protected static final String TABLE_NAME_PROPERTY = "lo_activity_property";
	private Long id;
	private User user;
	private String operation;
	private Date timestamp;
	private String comments;
	private String entityName;
	private String entityId;
	private String entityDescription;
	private String otherEntityName;
	private String otherEntityId;
	private String otherEntityDescription;
	private String parentEntityName;
	private String parentEntityId;
	private String parentEntityDescription;
	private List<UpdatedProperty> updatedProperties;

	
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	@ManyToOne
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	@Column(length=100)
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	@Temporal(TemporalType.TIMESTAMP)
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	@Column(length=2000)
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	@Column(length=200)
	public String getEntityName() {
		return entityName;
	}
	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
	@Column(length=200)
	public String getEntityId() {
		return entityId;
	}
	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}
	@Column(length=200)
	public String getParentEntityName() {
		return parentEntityName;
	}
	public void setParentEntityName(String parentEntityName) {
		this.parentEntityName = parentEntityName;
	}
	@Column(length=200)
	public String getParentEntityId() {
		return parentEntityId;
	}
	public void setParentEntityId(String parentEntityId) {
		this.parentEntityId = parentEntityId;
	}
	@Column(length=200)
	public String getEntityDescription() {
		return entityDescription;
	}
	public void setEntityDescription(String entityDescription) {
		this.entityDescription = entityDescription;
	}
	@Column(length=200)
	public String getOtherEntityName() {
		return otherEntityName;
	}
	public void setOtherEntityName(String otherEntityName) {
		this.otherEntityName = otherEntityName;
	}
	@Column(length=200)
	public String getOtherEntityId() {
		return otherEntityId;
	}
	public void setOtherEntityId(String otherEntityId) {
		this.otherEntityId = otherEntityId;
	}
	@Column(length=200)
	public String getOtherEntityDescription() {
		return otherEntityDescription;
	}
	public void setOtherEntityDescription(String otherEntityDescription) {
		this.otherEntityDescription = otherEntityDescription;
	}
	@Column(length=200)
	public String getParentEntityDescription() {
		return parentEntityDescription;
	}
	public void setParentEntityDescription(String parentEntityDescription) {
		this.parentEntityDescription = parentEntityDescription;
	}
	
	@ElementCollection
	@JoinTable(name=TABLE_NAME_PROPERTY)
	@OrderColumn
	public List<UpdatedProperty> getUpdatedProperties() {
		if(updatedProperties == null) updatedProperties = new ArrayList<UpdatedProperty>();
		return updatedProperties;
	}
	public void setUpdatedProperties(List<UpdatedProperty> updatedProperties) {
		this.updatedProperties = updatedProperties;
	}
	
	
}
