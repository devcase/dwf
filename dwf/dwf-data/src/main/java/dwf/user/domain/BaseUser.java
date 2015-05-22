package dwf.user.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import dwf.persistence.annotations.HideActivityLogValues;
import dwf.persistence.annotations.UniqueValue;
import dwf.persistence.annotations.UpdatableProperty;
import dwf.persistence.domain.BaseEntity;
import dwf.persistence.validation.ValidationGroups;

@Entity
@UniqueValue(field="email")
public class BaseUser extends BaseEntity<Long> {

	private static final long serialVersionUID = 3161746215465593657L;
	
	private String email;
	private String hashedpass;
	private Date expirationDate;
	private boolean verified;
	private List<String> roles;

	public BaseUser() {}
	
	public BaseUser(String email, String hashedpass, Date expirationDate, String... roles) {
		this.email = email;
		this.hashedpass = hashedpass;
		this.expirationDate = expirationDate;
		this.roles = roles != null ? Arrays.asList(roles) : Collections.emptyList();
	}
	
	@NotEmpty
	@Email
	@NaturalId(mutable=true)
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	@Column(length=200)
	@UpdatableProperty(groups=ValidationGroups.ChangePassword.class)
	@HideActivityLogValues
	public String getHashedpass() {
		return hashedpass;
	}
	
	public void setHashedpass(String hashedpass) {
		this.hashedpass = hashedpass;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	public Date getExpirationDate() {
		return expirationDate;
	}
	
	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}
	
	public boolean isVerified() {
		return verified;
	}
	
	public void setVerified(boolean verified) {
		this.verified = verified;
	}
	
	@Override
	protected String displayText() {
		return email;
	}

	@ElementCollection
	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	
	@Transient
	public boolean isExpired() {
		return expirationDate != null && expirationDate.getTime() < System.currentTimeMillis();
	}
	
}
