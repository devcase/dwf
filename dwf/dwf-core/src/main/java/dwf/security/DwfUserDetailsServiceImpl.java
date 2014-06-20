package dwf.security;

import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;


public class DwfUserDetailsServiceImpl extends JdbcDaoImpl {
    public static final String DEF_USERS_BY_USERNAME_QUERY =
            "select username,password,enabled " +
            "from us_user " +
            "where username = ?";
	
    public static final String DEF_AUTHORITIES_BY_USERNAME_QUERY =
            "select us_user.username, us_authority.authority " +
            "from us_user left join us_authority on us_authority.user_id = us_user.id " +
            "where us_user.username = ?";
	
	public DwfUserDetailsServiceImpl() {
		super();
		setAuthoritiesByUsernameQuery(DEF_AUTHORITIES_BY_USERNAME_QUERY);
		setUsersByUsernameQuery(DEF_USERS_BY_USERNAME_QUERY);
	}

}
