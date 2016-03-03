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
import javax.validation.groups.Default;

import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import dwf.persistence.annotations.HideActivityLogValues;
import dwf.persistence.annotations.UniqueValue;
import dwf.persistence.annotations.UpdatableProperty;
import dwf.persistence.annotations.constraints.Lowercase;
import dwf.persistence.domain.BaseEntity;
import dwf.persistence.validation.ValidationGroups;
import dwf.serialization.View;

@Entity
@UniqueValue(field="email")
public class BaseUser extends BaseEntity<Long> {
	public static interface UpdateEmail {}
	public static interface UpdateEmailName {}

	private static final long serialVersionUID = 3161746215465593657L;
	
	@JsonView(View.Summary.class)
	private String email;
	private String hashedpass;
	private Date expirationDate;
	private boolean verified;
	private List<String> roles;
	private String firstName;
	private String lastName;

	public BaseUser() {}
	
	public BaseUser(String email, String hashedpass, Date expirationDate, String... roles) {
		this.email = email;
		this.hashedpass = hashedpass;
		this.expirationDate = expirationDate;
		this.roles = roles != null ? Arrays.asList(roles) : Collections.emptyList();
	}
	
	public BaseUser(String email) {
		super();
		this.email = email;
	}

	@NotEmpty(groups={Default.class, UpdateEmail.class, UpdateEmailName.class})
	@Email(groups={Default.class, UpdateEmail.class, UpdateEmailName.class})
	@Lowercase(groups={Default.class, UpdateEmail.class, UpdateEmailName.class})
	@NaturalId(mutable=true)
	@UpdatableProperty(groups={Default.class, UpdateEmail.class, UpdateEmailName.class})
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	@Column(length=200)
	@UpdatableProperty(groups=ValidationGroups.ChangePassword.class)
	@HideActivityLogValues
	@JsonIgnore
	public String getHashedpass() {
		return hashedpass;
	}
	
	public void setHashedpass(String hashedpass) {
		this.hashedpass = hashedpass;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonIgnore
	public Date getExpirationDate() {
		return expirationDate;
	}
	
	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}
	
	@JsonIgnore
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
	@JsonIgnore
	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
	
	@Transient
	@JsonIgnore
	public boolean isExpired() {
		return expirationDate != null && expirationDate.getTime() < System.currentTimeMillis();
	}

	@Column(length=200)
	@UpdatableProperty(groups={Default.class, UpdateEmailName.class})
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@Column(length=200)
	@UpdatableProperty(groups={Default.class, UpdateEmailName.class})
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
}
