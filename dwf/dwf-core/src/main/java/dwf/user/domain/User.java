package dwf.user.domain;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import dwf.persistence.annotations.HideActivityLogValues;
import dwf.persistence.annotations.UniqueValue;
import dwf.persistence.annotations.UpdatableProperty;
import dwf.persistence.domain.BaseEntity;
import dwf.validation.ValidationGroups;

/**
 * Montado para obedecer o esquema do spring-security
 * http://docs.spring.io/spring
 * -security/site/docs/3.1.x/reference/appendix-schema
 * .html#db_schema_users_authorities
 * <p>
 * {@link br.com.devcase.dwf.security.DwfUserDetailsServiceImpl}
 * 
 * @author Hirata
 * 
 */
@Entity
@Table(indexes={
		@Index(columnList = "dtype"),
		@Index(columnList = "enabled") })
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype", discriminatorType = DiscriminatorType.STRING, length = 100)
@UniqueValue(field = "username")
public abstract class User extends BaseEntity<Long> implements UserDetails {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7061887279574488319L;

	private String username;
	private String hashedPassword;

	private List<String> roles;

	@Column(length = 50, nullable = false, unique = true, updatable = false)
	@NotNull(groups = { ValidationGroups.MergePersist.class})
	@UpdatableProperty(groups = { ValidationGroups.MergePersist.class, })
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(length = 200, nullable = true)
	@NotNull(groups = { ValidationGroups.MergePersist.class,
			ValidationGroups.ChangePassword.class })
	@UpdatableProperty(groups = { ValidationGroups.MergePersist.class,
			ValidationGroups.ChangePassword.class })
	@HideActivityLogValues
	public String getHashedPassword() {
		return hashedPassword;
	}

	public void setHashedPassword(String hashedPassword) {
		this.hashedPassword = hashedPassword;
	}

	@ElementCollection
	@CollectionTable()
	@Column(length = 50, nullable = false)
	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetails#getAuthorities()
	 */
	@Override
	@Transient
	public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> dbAuthsSet = new HashSet<GrantedAuthority>();
        for (String userRole : this.getRoles()) {
			dbAuthsSet.add(new SimpleGrantedAuthority(userRole));
		}
		return dbAuthsSet;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetails#getPassword()
	 */
	@Override
	@Transient
	public String getPassword() {
		return getHashedPassword();
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetails#isAccountNonExpired()
	 */
	@Override
	@Transient
	public boolean isAccountNonExpired() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetails#isAccountNonLocked()
	 */
	@Override
	@Transient
	public boolean isAccountNonLocked() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.springframework.security.core.userdetails.UserDetails#isCredentialsNonExpired()
	 */
	@Override
	@Transient
	public boolean isCredentialsNonExpired() {
		return true;
	}

	
}
