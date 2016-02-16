package dwf.user.utils;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import dwf.user.dao.BaseUserDAO;
import dwf.user.domain.BaseUser;
import dwf.user.domain.BaseUserRole;

@Component
@ConditionalOnProperty(prefix="dwf.superuser", value="email")
public class SuperuserCreator {
	@Autowired
	private BaseUserDAO baseUserDAO;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Value("${dwf.superuser.email:}")
	private String defaultUserEmail;
	@Value("${dwf.superuser.initialpassword:}")
	private String defaultUserPassword;
	
	@PostConstruct
	public void loadData() {
		Assert.hasText(defaultUserEmail);
		if(StringUtils.isBlank(defaultUserPassword)) defaultUserPassword = defaultUserEmail;
		baseUserDAO.findOrSaveNew(new BaseUser(defaultUserEmail, passwordEncoder.encode(defaultUserPassword), null, BaseUserRole.SUPERUSER, BaseUserRole.BACKOFFICE_ADMIN));
	}
}
