package dwf.user.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.OneToOne;

import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.validator.constraints.NotEmpty;

import dwf.persistence.annotations.NotEditableProperty;
import dwf.persistence.annotations.UniqueValue;
import dwf.persistence.domain.BaseEntity;

@Entity
@UniqueValue(field = "token")
public class VerificationToken extends BaseEntity<Long> {

	private static final long serialVersionUID = -4203897491499641698L;

	private static int EXPIRATION_IN_DAYS = 1;

	private String token;
	private BaseUser user;
	private Date expiryDate;
	private boolean verified;

	public VerificationToken() {
		super();
	}

	public VerificationToken(String token, BaseUser user) {
		this.token = token;
		this.user = user;
		this.expiryDate = DateUtils.addDays(new Date(), EXPIRATION_IN_DAYS);
		this.verified = false;
	}

	@NotEmpty
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}

	@OneToOne(optional=false, cascade={}, orphanRemoval=false)
	public BaseUser getUser() {
		return user;
	}
	
	public void setUser(BaseUser user) {
		this.user = user;
	}

	@NotEditableProperty
	public Date getExpiryDate() {
		return expiryDate;
	}
	
	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public boolean isVerified() {
		return verified;
	}
	
	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	@Override
	protected String displayText() {
		return token;
	}
	
	public boolean hasExpired() {
		return new Date().after(expiryDate);
	}
}
