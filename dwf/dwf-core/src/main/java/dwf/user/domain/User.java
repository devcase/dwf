package dwf.user.domain;

import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

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
@Table(name = User.TABLE_NAME, indexes={
		@Index(name = "ix_" + User.TABLE_NAME + "_dtype", columnList = "dtype"),
		@Index(name = "ix_" + User.TABLE_NAME + "_enabled", columnList = "enabled") })
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype", discriminatorType = DiscriminatorType.STRING, length = 100)
@UniqueValue(field = "username")
public abstract class User extends BaseEntity<Long> {
	static final String TABLE_NAME = "us_user";

	private String username;
	private String hashedPassword;

	private List<String> roles;

	@Column(name = "username", length = 50, nullable = false, unique = true, updatable = false)
	@NotNull(groups = { ValidationGroups.MergePersist.class})
	@UpdatableProperty(groups = { ValidationGroups.MergePersist.class, })
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(name = "password", length = 200, nullable = false)
	@NotNull(groups = { ValidationGroups.MergePersist.class,
			ValidationGroups.ChangePassword.class })
	@UpdatableProperty(groups = { ValidationGroups.MergePersist.class,
			ValidationGroups.ChangePassword.class })
	public String getHashedPassword() {
		return hashedPassword;
	}

	public void setHashedPassword(String hashedPassword) {
		this.hashedPassword = hashedPassword;
	}

	@ElementCollection
	@CollectionTable(name = "us_authority", joinColumns = @JoinColumn(name = "user_id"), uniqueConstraints=@UniqueConstraint(columnNames={"user_id", "authority"}))
	@Column(name = "authority", length = 50, nullable = false)
	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
}
