package dwf.user.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import dwf.persistence.annotations.UniqueValue;
import dwf.persistence.annotations.constraints.Username;
import dwf.persistence.domain.BaseEntity;

@Entity
@UniqueValue(field="username")
public class BaseUser extends BaseEntity<Long> {

	private static final long serialVersionUID = 3161746215465593657L;
	
	private String username;
	private String email;
	private String hashedpass;
	private Date expirationDate;
	private boolean verified;
	private BaseUserRole role;

	public BaseUser() {}
	
	public BaseUser(String username, String email, String hashedpass, Date expirationDate, BaseUserRole role) {
		this.username = username;
		this.email = email;
		this.hashedpass = hashedpass;
		this.expirationDate = expirationDate;
		this.role = role;
	}
	
	@NaturalId(mutable=true)
	@Column(length=200)
	@Username
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	@NotEmpty
	@Email
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	@Column(length=200)
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
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	public BaseUserRole getRole() {
		return role;
	}
	
	public void setRole(BaseUserRole role) {
		this.role = role;
	}

	public boolean isVerified() {
		return verified;
	}
	
	public void setVerified(boolean verified) {
		this.verified = verified;
	}
	
	@Override
	protected String displayText() {
		return username;
	}
}
